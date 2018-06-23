package com.github.jhorology.bitwig;

import com.bitwig.extension.controller.api.ControllerHost;
import com.github.jhorology.bitwig.extension.AbstractExtension;
import com.github.jhorology.bitwig.websocket.WebSocketRpcServer;
import java.net.UnknownHostException;

public class WebSocketRpcServerExtension extends AbstractExtension {
    protected WebSocketRpcServerExtension(WebSocketRpcServerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    @Override
    public void registerModules() {
        try {
            registerModule(new WebSocketRpcServer(8887));
        } catch (UnknownHostException ex) {
            log(ex);
        }
    }
}
