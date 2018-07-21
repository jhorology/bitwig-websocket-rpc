package com.github.jhorology.bitwig.extension;

import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

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
    private final Executor flushExecutor;
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
        flushExecutor = new FlushExecutor();
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
     * get a executor that always runs tasks on 'Control Surface Session' thread.
     * @return executor
     */
    public Executor getExecutor() {
        return executor;
    }
    
    /**
     * get a executor that always runs tasks within 'ControllerHost#flush()' method.
     * @return executor
     */
    public Executor getFlushExecutor() {
        return flushExecutor;
    }

    /**
     * implementation method of ControllerHost
     */
    @Override
    public void init() {
        log = Logger.getLogger(this.getClass());
        modules = new Stack<>();
        register(flushExecutor);
        try {
            Object[] createdModules = createModules();
            if (createdModules != null && createdModules.length > 0) {
                Stream.of(createdModules).forEach(m -> register(m));
            }
        } catch (Exception ex) {
            log.error(ex);
        }
        // trigger synchronous event
        eventBus.post(initEvent);
    }

    /**
     * implementation method of ControllerHost
     */
    @Override
    public void exit() {
        // trigger synchronous event
        eventBus.post(exitEvent);
        
        // unregister modules
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
        // trigger synchronous event
        eventBus.post(flushEvent);
    }

    /**
     * register a subscriber module.
     * @param module
     */
    protected void register(Object module) {
        eventBus.register(module);
        modules.push(module);
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
