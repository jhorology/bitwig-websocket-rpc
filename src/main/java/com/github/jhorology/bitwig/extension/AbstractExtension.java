package com.github.jhorology.bitwig.extension;

import java.util.Stack;
import java.util.concurrent.Executor;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

/**
 * 
 */
public abstract class AbstractExtension extends ControllerExtension implements SubscriberExceptionHandler {
    private final EventBus eventBus;
    private final InitEvent initEvent;
    private final ExitEvent exitEvent;
    private final FlushEvent flushEvent;
    private final Executor executor;
    private Stack<Object> modules;
    private Logger log;
    
    /**
     * constructor.
     * @param definition
     * @param host
     */
    protected AbstractExtension(ControllerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
        eventBus = new EventBus(this);
        executor = new ControlSurfaceSessionExecutor(this);
        initEvent = new InitEvent(this);
        exitEvent = new ExitEvent(this);
        flushEvent = new FlushEvent(this);
        Logger.init(host, Logger.TRACE);
    }

    /**
     * create events subscriber modules.
     * @return 
     * @throws java.lang.Exception
     */
    protected abstract Object[] createModules() throws Exception;

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

    /**
     * implementation method of ControllerHost
     */
    @Override
    public void init() {
        log = Logger.getLogger(this.getClass());
        modules = new Stack<>();
        try {
            Object[] cratedModules = createModules();
            if (cratedModules != null && cratedModules.length > 0) {
                for(Object module : cratedModules) {
                    eventBus.register(module);
                    modules.push(module);
                }
            }
        } catch (Exception ex) {
            log.error(ex);
        }
        eventBus.post(initEvent);
    }

    /**
     * implementation method of ControllerHost
     */
    @Override
    public void exit() {
        eventBus.post(exitEvent);
        while(!modules.empty()) {
            eventBus.unregister(modules.pop());
        }
        modules.clear();
    }

    /**
     * implementation method of ControllerHost
     */
    @Override
    public void flush() {
        eventBus.post(flushEvent);
    }

    /**
     * implementation method of SubscriberExceptionHandler
     * @param ex
     * @param context 
     */
    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        log.error( "extension event handling error. event:" +  context.getEvent().toString(), ex);
    }
}
