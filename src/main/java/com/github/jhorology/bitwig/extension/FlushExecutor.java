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
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executor;

// provided dependencies
import com.google.common.eventbus.Subscribe;

// dependencies
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Executor class that always runs tasks within 'ControllerHost#flush()' method.
 */
public class FlushExecutor implements Executor {
    private static final Logger LOG = LoggerFactory.getLogger(FlushExecutor.class);

    private static final int IDLE = 0;
    private static final int REQUESTED = 1;
    private static final int SHUTDOWN = 2;
    private static final int QUEUE_SIZE = 64;

    private Thread controlSurfaceSession;
    private Queue<Runnable> tasks;
    private AbstractExtension<? extends AbstractConfiguration> extension;
    private int state = IDLE;
    @Subscribe
    public void onInit(InitEvent e) {
        tasks = new ArrayDeque<>(QUEUE_SIZE);
        extension = e.getExtension();
        controlSurfaceSession = Thread.currentThread();
        state = IDLE;
    }
    
    @Subscribe
    public void onFlush(FlushEvent e) {
        synchronized(this) {
            runAllQueuedTasks();
        }
    }
    
    @Subscribe
    public void onExit(ExitEvent e) {
        synchronized(this) {
            runAllQueuedTasks();
            state = SHUTDOWN;
        }
    }
    
    /**
     * An implementation method of Executor interface
     * @param command 
     */
    @Override
    public synchronized void execute(Runnable command) {
        tasks.add(command);
        // after 'exit' was called, 'flush'is no more called from host.
        if (state == SHUTDOWN) {
            // to available to execute event that triggerd from another module's onExit.
            if (Thread.currentThread() == controlSurfaceSession) {
                runAllQueuedTasks();
            }
        } else if (state == IDLE) {
            // TODO
            // is this safe?
            // maybe not callable from other than control surface session thread.
            extension.getHost().requestFlush();
            state = REQUESTED;
        }
        if (LOG.isWarnEnabled()) {
            if (tasks.size() > (QUEUE_SIZE * 3 / 4)) {
                LOG.warn("task queues will reach full capacity. currently queued tasks:" + tasks.size());
            }
        }
    }

    private void runAllQueuedTasks() {
        Runnable task = tasks.poll();
        while(task != null) {
            ExecutionContext.init(extension);
            ExecutionContext context = ExecutionContext.getContext();
            try {
                task.run();
            } finally {
                task = tasks.poll();
                ExecutionContext.destroy();
            }
        }
        if (state == REQUESTED) state = IDLE;
    }
}
