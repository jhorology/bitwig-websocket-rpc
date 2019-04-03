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

/**
 * Executor class that is used to execute task asynchronously in the 'Control Surface Session' thread.
 */
@Deprecated
public class ControlSurfaceSessionExecutor implements Executor {
    private final AbstractExtension<? extends AbstractConfiguration> extension;
    
    /**
     * Consutruct an instance of Executor.
     * @param extension 
     */
    public ControlSurfaceSessionExecutor(AbstractExtension<? extends AbstractConfiguration> extension) {
        this.extension = extension;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void execute(Runnable command) {
        // This is not Safe!
        // easy to see crash on ConcurrentModificationException
        extension.getHost().scheduleTask(() -> {
                ExecutionContext.init(extension);
                try {
                    command.run();
                } finally {
                    ExecutionContext.destroy();
                }
            }, 0L);
    }
}
