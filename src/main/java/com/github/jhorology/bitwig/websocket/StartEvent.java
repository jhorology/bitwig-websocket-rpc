package com.github.jhorology.bitwig.websocket;

import com.github.jhorology.bitwig.reflect.MethodRegistry;

public class StartEvent extends AbstractWebSocketEvent {
    private final MethodRegistry methodRegistry;

    StartEvent(MethodRegistry methodRegistry) {
        super(null);
        this.methodRegistry = methodRegistry;
    }
    
    public MethodRegistry getMethodRegistry() {
        return methodRegistry;
    }
}
