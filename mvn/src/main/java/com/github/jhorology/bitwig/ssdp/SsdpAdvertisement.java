/*
 * The MIT License
 *
 * Copyright 2019 masafumi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.jhorology.bitwig.ssdp;

// jdk
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

// bitwig api
import com.bitwig.extension.controller.api.ControllerHost;

// provided dependencies
import com.google.common.eventbus.Subscribe;

// dependenices
import com.nls.net.ssdp.SsdpCommonHeaders;
import com.nls.net.ssdp.SsdpMessage;
import com.nls.net.ssdp.SsdpMessageType;
import com.nls.net.ssdp.SsdpNotificationType;
import com.nls.net.ssdp.SsdpPacket;
import com.nls.net.ssdp.SsdpPacketListener;
import com.nls.net.ssdp.SsdpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.extension.AbstractExtensionDefinition;
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;

/**
 *
 */
public class SsdpAdvertisement implements SsdpPacketListener {
    private static final Logger LOG = LoggerFactory.getLogger(SsdpAdvertisement.class);
    private String serviceType;
    private SsdpService ssdpService;
    private Config config;
    private ControllerHost host;
    private AbstractExtensionDefinition<Config> definition;
    
    @Subscribe
    public void onInit(InitEvent<Config> e) {
        config = e.getConfig();
        definition = e.getDefinition();
        host = e.getHost();
        serviceType = String.format("urn:bitwig-websocket-rpc:service:%s:%s",
                config.getRpcProtocol(),
                definition.getVersion());
        final List<NetworkInterface> nics = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nicEnum = NetworkInterface.getNetworkInterfaces();
            while (nicEnum.hasMoreElements()) {
                NetworkInterface nic = nicEnum.nextElement();
                if (nic.isUp()
                    && nic.supportsMulticast()
                    && !nic.getName().startsWith("utun") // macos VPN interface
                    && nic.inetAddresses().anyMatch(a -> a instanceof Inet4Address && a.isSiteLocalAddress())) {
                    nics.add(nic);
                }
            }
            ssdpService = new SsdpService(nics, this);
            ssdpService.listen();
            
            SsdpMessage aliveMessage = createMessage(SsdpMessageType.NOTIFY, SsdpNotificationType.ALIVE);
            
            ssdpService.getChannels().forEach(ch -> {
                try {
                    String location = createLocation(ch.getNetworkInterface());
                    if (location != null) {
                        aliveMessage.setHeader("LOCATION", location);
                        ch.send(aliveMessage);
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("multicascast notification[{}]", aliveMessage);
                        }
                    }
                } catch (IOException ex) {
                    LOG.error("couldn't send message.", ex);
                }
            });
        } catch (IOException ex) {
            LOG.error("SSDP initialize error.", ex);
        }
    }
    
    @Subscribe
    public void onExit(ExitEvent<Config> e) {
        SsdpMessage byebyeMessage = createMessage(SsdpMessageType.NOTIFY, SsdpNotificationType.ALIVE);
        ssdpService.getChannels().forEach(ch -> {
            try {
                ch.send(byebyeMessage);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("multicascast notification[{}]", byebyeMessage);
                }
            } catch (IOException ex) {
                LOG.error("couldn't send message.", ex);
            }
        });
        ssdpService.close();
    }

    @Override
    public void received(SsdpPacket sp) {
        SsdpMessage message = sp.getMessage();
        if (LOG.isTraceEnabled()) {
            LOG.trace("SSDP recieved message:[{}] socket address:[{}]",
                       message.toString(),
                       sp.getSocketAddress());
        }
        if (message.getType() == SsdpMessageType.MSEARCH) {
            String st = message.getHeader("ST");
            if ("ssdp:all".equals(st) || serviceType.equals(st)) {
                try {
                    String location = createLocation(sp.getChannel().getNetworkInterface());
                    if (location != null) {
                        SsdpMessage response = createMessage(SsdpMessageType.RESPONSE, null);
                        response.setHeader("LOCATION", location);
                        sp.getChannel().send(response, sp.getSocketAddress());
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("unicast response[{}] to [{}]", response, sp.getSocketAddress());
                        }
                    }
                } catch (IOException ex) {
                    LOG.error("couldn't send message.", ex);
                }
            }
        }
        
    }
    
    private SsdpMessage createMessage(SsdpMessageType mType, SsdpNotificationType nType) {
        SsdpMessage message = new SsdpMessage(mType);
        message.setHeader("HOST", "239.255.255.250:1900");
        message.setHeader(mType == SsdpMessageType.RESPONSE ? "ST" : "NT", serviceType);
        if (mType == SsdpMessageType.NOTIFY) {
            message.setHeader(SsdpCommonHeaders.NTS.name(), nType.getRepresentation());
        }
        message.setHeader("USN", "uuid:" + definition.getId().toString());
        if (mType == SsdpMessageType.RESPONSE || nType == SsdpNotificationType.ALIVE) {
            message.setHeader("CACHE-CONTROL", "max-age = 7200");
            message.setHeader("EXTENSION", definition.getName());
            message.setHeader("BITWIG-VERSION", host.getHostVersion());
            message.setHeader("API-VERSION", String.valueOf(host.getHostApiVersion()));
            message.setHeader("PLATFORM", host.getPlatformType().name());
        }
        return message;
    }
    
    private String createLocation(NetworkInterface nic) {
        InetAddress addr = nic.inetAddresses()
            .filter(a -> a instanceof Inet4Address && a.isSiteLocalAddress())
            .findFirst()
            .orElse(null);
        return addr != null 
            ? String.format("ws://%s:%d", addr.getHostName(), config.getWebSocketPort())
            : null;
    }
}
