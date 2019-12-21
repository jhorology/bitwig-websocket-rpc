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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.Map;

// bitwig api

// provided dependencies
import com.google.common.eventbus.Subscribe;
import java.util.HashMap;

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
import com.github.jhorology.bitwig.extension.ExitEvent;
import com.github.jhorology.bitwig.extension.InitEvent;

/**
 *
 */
public class SsdpAdvertisement implements SsdpPacketListener {
    private static final Logger LOG = LoggerFactory.getLogger(SsdpAdvertisement.class);
    // https://cloud.tencent.com/developer/article/1011721
    private static byte[][] VIRTUAL_HARDWARE_ADDRESSES = {
        {0x00, 0x05, 0x69},             // VMWare
        {0x00, 0x1C, 0x14},             // VMWare
        {0x00, 0x0C, 0x29},             // VMWare
        {0x00, 0x50, 0x56},             // VMWare
        {0x08, 0x00, 0x27},             // Virtualbox
        {0x0A, 0x00, 0x27},             // Virtualbox
        {0x00, 0x03, (byte)0xFF},       // Virtual-PC
        {0x00, 0x15, 0x5D}              // Hyper-V
    };
    private SsdpService ssdpService;
    private String serviceType;
    private String usn;
    private SsdpMessage byebyeMessage;
    private Map<NetworkInterface, SsdpMessage> responses;

    /**
     * Handles the start of extension's liefe-cycle.
     * @param e
     */
    @Subscribe
    public void onInit(InitEvent<Config> e) {
        serviceType = String.format("urn:bitwig-websocket-rpc:service:%s:%s",
                                    e.getConfig().getRpcProtocol().getDisplayName().toLowerCase(),
                                    e.getDefinition().getVersion());
        usn = "uuid:" + UUID.randomUUID().toString();

        final List<NetworkInterface> nics = new ArrayList<>();
        responses = new HashMap<>();
        try {
            Enumeration<NetworkInterface> nicEnum = NetworkInterface.getNetworkInterfaces();
            while (nicEnum.hasMoreElements()) {
                NetworkInterface nic = nicEnum.nextElement();
                if (nic.isUp()
                    && nic.supportsMulticast()
                    && !isVirtualNic(nic)) {
                    String location = createLocation(nic, e.getConfig().getWebSocketPort());
                    if (location != null) {
                        responses.put(nic, createResponse(e, location));
                        nics.add(nic);
                    }
                }
            }
            byebyeMessage = createNotification(e, SsdpNotificationType.BYEBYE);
            
            ssdpService = new SsdpService(nics, this);
            ssdpService.listen();

            // notify ssdp:alive
            ssdpService.getChannels().forEach(ch -> {
                    try {
                        String location = createLocation(ch.getNetworkInterface(), e.getConfig().getWebSocketPort());
                        if (location != null) {
                            SsdpMessage alive = createNotification(e, SsdpNotificationType.ALIVE, location);
                            ch.send(alive);
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("multicascast notification [{}]", alive);
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

    /**
     * Handles the end of extension's liefe-cycle.
     * @param e
     */
    @Subscribe
    public void onExit(ExitEvent<Config> e) {
        // notify ssdp:byebye
        try {
            ssdpService.getChannels().forEach(ch -> {
                    try {
                        ch.send(byebyeMessage);
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("multicascast notification [{}]", byebyeMessage);
                        }
                    } catch (IOException ex) {
                        LOG.error("couldn't send message.", ex);
                    }
                });
        } finally {
            ssdpService.close();
        }
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
                    SsdpMessage response = responses.get(sp.getChannel().getNetworkInterface());
                    if (response != null) {
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

    private SsdpMessage createResponse(InitEvent<Config> e, String location) {
        return createMessage(e, SsdpMessageType.RESPONSE, null, location);
    }
    
    private SsdpMessage createNotification(InitEvent<Config> e, SsdpNotificationType nType) {
        return createMessage(e, SsdpMessageType.NOTIFY, nType, null);
    }

    private SsdpMessage createNotification(InitEvent<Config> e, SsdpNotificationType nType, String location) {
        return createMessage(e, SsdpMessageType.NOTIFY, nType, location);
    }
    
    private SsdpMessage createMessage(InitEvent<Config> e, SsdpMessageType mType, SsdpNotificationType nType, String location) {
        SsdpMessage message = new SsdpMessage(mType);
        message.setHeader("HOST", "239.255.255.250:1900");
        message.setHeader(mType == SsdpMessageType.RESPONSE ? "ST" : "NT", serviceType);
        if (mType == SsdpMessageType.NOTIFY) {
            message.setHeader(SsdpCommonHeaders.NTS.name(), nType.getRepresentation());
        }
        message.setHeader("USN", usn);
        if (mType == SsdpMessageType.RESPONSE || nType == SsdpNotificationType.ALIVE) {
            message.setHeader("CACHE-CONTROL", "max-age = 7200");
            message.setHeader("EXTENSION", e.getDefinition().getName());
            message.setHeader("BITWIG-VERSION", e.getHost().getHostVersion());
            message.setHeader("API-VERSION", String.valueOf(e.getHost().getHostApiVersion()));
            message.setHeader("PLATFORM", e.getHost().getPlatformType().name());
        }
        if (location != null) {
            message.setHeader("LOCATION", location);
        }
        return message;
    }

    private String createLocation(NetworkInterface nic, int port) {
        Enumeration<InetAddress> addrEnum = nic.getInetAddresses();
       while(addrEnum.hasMoreElements()) {
           InetAddress addr = addrEnum.nextElement();
           if (addr instanceof Inet4Address && addr.isSiteLocalAddress()) {
                return String.format("ws://%s:%d", addr.getHostAddress(), port);
           }
       }
       return null;
    }

    // https://cloud.tencent.com/developer/article/1011721
    private boolean isVirtualNic(NetworkInterface nic) throws SocketException {
        if (nic.isVirtual()) {
            return true;
        }
        if (nic.getName().startsWith("utun")) {
            // macos VPN interfaces
            return true;
        }
        byte[] macAddress = nic.getHardwareAddress();
        if(null == macAddress || macAddress.length < 3) {
            return true;
        }
        for (byte[] virtualMac: VIRTUAL_HARDWARE_ADDRESSES){
            if (virtualMac[0] == macAddress[0]
                && virtualMac[1] == macAddress[1]
                && virtualMac[2] == macAddress[2]) {
                return true;
            }
        }
        return false;
    }
}
