package com.github.jhorology.bitwig;

import java.net.UnknownHostException;

import com.bitwig.extension.controller.api.ControllerHost;

import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;
import com.github.jhorology.bitwig.reflect.MethodRegistry;

public class WebSocketRpcServerExtension extends AbstractExtension {
    private static final Logger log = Logger.getLogger(WebSocketRpcServerExtension.class);
    protected WebSocketRpcServerExtension(WebSocketRpcServerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    @Override
    public void registerModules() {
        registerModule(MethodRegistry.getInstance());
        try {
            WebSocketRpcServer server = new WebSocketRpcServer(8887, Protocols.JSONRPC20);
            registerModule(server);
        } catch (UnknownHostException ex) {
            log.error(ex);
        }
    }
}
