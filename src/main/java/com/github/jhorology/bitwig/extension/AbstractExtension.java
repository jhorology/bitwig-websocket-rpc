package com.github.jhorology.bitwig.extension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import java.util.concurrent.Executor;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public abstract class AbstractExtension extends ControllerExtension implements SubscriberExceptionHandler {
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
        executor = new ExtensionThreadExecutor();
        initEvent = new InitEvent(this);
        exitEvent = new ExitEvent(this);
        flushEvent = new FlushEvent(this);
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
        registerModule(executor);
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
        log( "extension event handling error. event:" +  context.getEvent().toString(), ex);
    }
    
    public void log(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        sb.append(" ");
        sb.append(Thread.currentThread().getName());
        sb.append(" ");
        sb.append(msg);
        getHost().println(sb.toString());
    }

    public void log(String msg, Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();
        log(msg + "\n" + pw.toString());
    }

    public void log(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();
        log(sw.toString());
    }
}
