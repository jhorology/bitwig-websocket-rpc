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

// jdk
import java.util.Stack;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

// bitwig api
import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

// provided dependencies
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

// dependencies
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.ScriptConsoleLogger;

// source

/**
 * An abstract bass class that is used to trigger extension events.
 * @param <T> the type of this extension's configuration.
 */
public abstract class AbstractExtension<T extends AbstractConfiguration>
        extends ControllerExtension
        implements SubscriberExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractExtension.class);

    protected final T config;
    private EventBus eventBus;
    private InitEvent initEvent;
    private ExitEvent exitEvent;
    private FlushEvent flushEvent;
    private Executor asyncExecutor;
    private Stack<Object> extensionModules;
    private Logger log;
    
    /**
     * Constructor.
     * Inherited class should call this as super().
     * @param definition
     * @param host
     * @param config
     */
    protected AbstractExtension(ControllerExtensionDefinition definition, ControllerHost host, T config) {
        super(definition, host);
        this.config = config;
    }

    /**
     * create events subscriber modules.
     * @return 
     * @throws java.lang.Exception 
     */
    protected abstract Object[] createModules() throws Exception;

    /**
     * get a Executor to run the task from other than 'Control Surface Session' thread.
     * @return 
     */
    Executor getAsyncExecutor() {
        return asyncExecutor;
    }
    
    /**
     * This method is called from host(Bitwig Studio) when the extension is being initialized.
     */
    @Override
    public void init() {
        ControllerHost host = getHost();
        // always return intial value at this time.
        config.init(host, getExtensionDefinition());
        ScriptConsoleLogger.setGlobalLogLevel(config.getLogLevel());
        ScriptConsoleLogger.setControllerHost(host);
        LOG.trace("Start initialization.");
        eventBus = new EventBus(this);
        asyncExecutor = new ControlSurfaceSessionExecutor2(this);
        initEvent = new InitEvent(this);
        exitEvent = new ExitEvent(this);
        flushEvent = new FlushEvent(this);
        extensionModules = new Stack<>();
        // register internal core module first
        register(asyncExecutor);
        try {
            Object[] modules = createModules();
            if (modules != null && modules.length > 0) {
                Stream.of(modules).forEach(m -> register(m));
            }
            eventBus.post(initEvent);
            LOG.trace("Extension has been initialized.");
        } catch (Exception ex) {
            LOG.error("Error registering extension modules.", ex);
        }
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
        config.exit();
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
            LOG.error( "Error handling extension event:" +  context.getEvent().toString(), ex);
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