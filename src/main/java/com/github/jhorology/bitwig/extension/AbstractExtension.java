package com.github.jhorology.bitwig.extension;

import java.util.Stack;
import java.util.concurrent.Executor;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public abstract class AbstractExtension extends ControllerExtension implements SubscriberExceptionHandler {
    private static final Logger log = Logger.getLogger(AbstractExtension.class);
    private final EventBus eventBus;
    private final InitEvent initEvent;
    private final ExitEvent exitEvent;
    private final FlushEvent flushEvent;
    private final Executor executor;
    private final Stack<Object> modules;
    
    /**
     * constructor.
     * @param definition
     * @param host
     */
    protected AbstractExtension(ControllerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
        this.modules = new Stack<>();
        eventBus = new EventBus(this);
        executor = new ExtensionThreadExecutor(host);
        initEvent = new InitEvent(this);
        exitEvent = new ExitEvent(this);
        flushEvent = new FlushEvent(this);
        Logger.init(host, Logger.TRACE);
    }

    /**
     * register events subscriber modules.
     */
    public abstract void registerModules();

    /**
     * return instance of EventBus.
     * @return event bus
     */
    public EventBus getEventBus() {
        return eventBus;
    }
    
    /**
     * return instance of EventBus.
     * @return event bus
     */
    public Executor getExecutor() {
        return executor;
    }

    protected void registerModule(Object module) {
        eventBus.register(module);
        modules.push(module);
    }
        
    @Override
    public void init() {
        registerModules();
        eventBus.post(initEvent);
    }

    @Override
    public void exit() {
        eventBus.post(exitEvent);
        while(!modules.empty()) {
            eventBus.unregister(modules.pop());
        }
    }

    @Override
    public void flush() {
        eventBus.post(flushEvent);
    }
    
    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        log.error( "extension event handling error. event:" +  context.getEvent().toString(), ex);
    }
}
