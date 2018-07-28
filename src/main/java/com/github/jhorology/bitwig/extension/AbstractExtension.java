/*
 * Copyright (c) 2018 Masafumi Fujimaru
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * An abstract bass class that is used to trigger extension events.
 */
public abstract class AbstractExtension extends ControllerExtension implements SubscriberExceptionHandler {
    private EventBus eventBus;
    private InitEvent initEvent;
    private ExitEvent exitEvent;
    private FlushEvent flushEvent;
    private Executor asyncExecutor;
    private Executor flushExecutor;
    private Stack<Object> extensionModules;
    private Logger log;

    /**
     * Constructor.<br/>
     * Inherited class should call this as super().
     * @param definition
     * @param host
     */
    protected AbstractExtension(ControllerExtensionDefinition definition, ControllerHost host) {
        super(definition, host);
    }

    /**
     * create events subscriber modules.
     * @return 
     */
    protected abstract Object[] createModules() throws Exception;

    /**
     * get a Executor to run the task from other than 'Control Surface Session' thread.
     * @return 
     */
    @Deprecated
    Executor getAsyncExecutor() {
        return asyncExecutor;
    }
    
    /**
     * get a Executor to run the tasks within "ControllerHost#flush()" method.
     * @return 
     */
    Executor getFlushExecutor() {
        return flushExecutor;
    }

    /**
     * This method is called from host(Bitwig Studio) when the extension is being initialized.
     */
    @Override
    public void init() {
        Logger.init(getHost(), Logger.TRACE);
        log = Logger.getLogger(AbstractExtension.class);
        log.trace("Start initialization.");
        eventBus = new EventBus(this);
        asyncExecutor = new ControlSurfaceSessionExecutor(this);
        flushExecutor = new FlushExecutor();
        initEvent = new InitEvent(this);
        exitEvent = new ExitEvent(this);
        flushEvent = new FlushEvent(this);
        extensionModules = new Stack<>();
        // register internal core module first
        register(new Logger());
        register(flushExecutor);
        try {
            Object[] modules = createModules();
            if (modules != null && modules.length > 0) {
                Stream.of(modules).forEach(m -> register(m));
            }
            eventBus.post(initEvent);
            log.trace("Extension has been initialized.");
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    
    private String createStackTraceString(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * This method is called from host at the extension's start of lifecycle.
     */
    @Override
    public void exit() {
        // trigger synchronous event
        eventBus.post(exitEvent);
        
        // unregister modules
        while(!extensionModules.empty()) {
            eventBus.unregister(extensionModules.pop());
        }
        extensionModules.clear();
    }

    /**
     * This method is called from host at the point needed feedback to control surface.
     */
    @Override
    public void flush() {
        // trigger synchronous event
        eventBus.post(flushEvent);
    }

    /**
     * Handles exceptions thrown by subscribers.
     * @param ex
     * @param context 
     */
    @Override
    public void handleException(Throwable ex,
                                SubscriberExceptionContext context) {
        if (log != null) {
            log.error( "extension event handling error. event:" +  context.getEvent().toString(), ex);
        }
    }

    /**
     * register a subscriber of extension events.
     * @param module The subscriber of extension events.
     */
    protected void register(Object module) {
        eventBus.register(module);
        extensionModules.push(module);
    }
}
