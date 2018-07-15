package com.github.jhorology.bitwig;

import java.net.UnknownHostException;

import com.bitwig.extension.controller.api.ControllerHost;

import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import com.github.jhorology.bitwig.websocket.protocol.Protocols;
import com.github.jhorology.bitwig.reflect.MethodRegistry;

/**
 * 
 */
public class WebSocketRpcServerExtension extends AbstractExtension {
    /**
     * Constructor
     * 
     * @param definition
     * @param host 
     */
    protected WebSocketRpcServerExtension(WebSocketRpcServerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    @Override
    public Object[] createModules() throws UnknownHostException {
        MethodRegistry methodRegistry = new MethodRegistry();
        return new Object[] {
            methodRegistry,
            new WebSocketRpcServer(8887, Protocols.newJsonRpc20(), methodRegistry)
        };
    }
}
