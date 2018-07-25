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
 */
public abstract class AbstractExtensionEvent {
    protected final AbstractExtension extension;
    
    /**
     * Construct an instance with extension.
     * @param extension 
     */
    protected AbstractExtensionEvent(AbstractExtension extension) {
        this.extension = extension;
    }
    
    /**
     * get an instance of extension.
     * @return 
     */
    public AbstractExtension getExtension() {
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
     * get a Executor to run the task from other than 'Control Surface Session' thread.
     * @return 
     */
    @Deprecated
    public Executor getAsyncExecutor() {
        return extension.getAsyncExecutor();
    }
    
    /**
     * get a Executor to run the tasks within "ControllerHost#flush()" method.
     * @return 
     */
    public Executor getFlushExecutor() {
        return extension.getFlushExecutor();
    }
}
