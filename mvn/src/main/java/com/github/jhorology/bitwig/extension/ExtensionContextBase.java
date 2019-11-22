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

import java.util.concurrent.Executor;

import com.bitwig.extension.controller.api.ControllerHost;

/**
 * An abstract base class for extension event. 
 * @param <T> the type of configuration
 */
public abstract class ExtensionContextBase<T extends AbstractConfiguration> {
    protected final AbstractExtension<T> extension;
    
    /**
     * Construct an instance with extension.
     * @param extension
     */
    protected ExtensionContextBase(AbstractExtension<T> extension) {
        this.extension = extension;
    }
    
    /**
     * get an instance of extension.
     * @return 
     */
    public AbstractExtension<? extends AbstractConfiguration> getExtension() {
        return extension;
    }
    
    /**
     * get a ControllerHost interface.
     * @return 
     */
    public ControllerHost getHost() {
        return extension.getHost();
    }
    
    /**
     * Returns configuration.
     * @return 
     */
    public T getConfig() {
        return extension.getConfig();
    }

    /**
     * get a Executor to run the task from other than 'Control Surface Session' thread.
     * @return 
     */
    public Executor getAsyncExecutor() {
        return extension.getAsyncExecutor();
    }
    
    /**
     * get a ControllerExtension definition
     * @return 
     */
    public AbstractExtensionDefinition<T> getDefinition() {
        return extension.getDefinition();
    }
    
    /**
     * get a default configuration
     * @return 
     */
    public T getDefaultConfig() {
        return getDefinition().getDefaultConfig();
    }
}

