package com.github.jhorology.bitwig;

import java.net.UnknownHostException;

import com.bitwig.extension.controller.api.ControllerHost;

import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;
import com.github.jhorology.bitwig.reflect.ReflectionRegistry;

/**
 * 
 */
public class WebSocketRpcServerExtension extends AbstractExtension {
    /**
     * Constructor
     * @param definition
     * @param host 
     */
    protected WebSocketRpcServerExtension(WebSocketRpcServerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    @Override
    protected Object[] createModules() throws UnknownHostException {
        ReflectionRegistry registry = new ReflectionRegistry();
        return new Object[] {
            registry,
            new WebSocketRpcServer(8887, Protocols.newJsonRpc20(), registry)
        };
    }
}
