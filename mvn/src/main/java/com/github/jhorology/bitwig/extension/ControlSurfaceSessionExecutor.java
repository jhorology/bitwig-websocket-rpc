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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

// provided dependencies
import com.google.common.eventbus.Subscribe;

// dependencies
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Executor class that always runs tasks within 'ControllerHost#flush()' method.
 */
public class ControlSurfaceSessionExecutor implements Executor, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ControlSurfaceSessionExecutor.class);

    private static final int QUEUE_SIZE = 64;
    
    private final AbstractExtension<? extends AbstractConfiguration> extension;
    private final ConcurrentLinkedQueue<Runnable> tasks;
    private Thread controlSurfaceSession;
    
    /**
     * Constructor.
     * @param extension
     */
    public ControlSurfaceSessionExecutor(AbstractExtension<? extends AbstractConfiguration> extension) {
        this.tasks = new ConcurrentLinkedQueue<>();
        this.extension = extension;
    }
    
    @Subscribe
    public void onInit(InitEvent e) {
        e.getHost().scheduleTask(this, 0L);
        this.controlSurfaceSession = Thread.currentThread();
    }
    
    @Subscribe
    public void onFlush(FlushEvent e) {
        runAllQueuedTasks();
    }
    
    @Subscribe
    public void onExit(ExitEvent e) {
        runAllQueuedTasks();
    }
    
    /**
     * An implementation method of Executor interface
     * @param command 
     */
    @Override
    public void execute(Runnable command) {
        tasks.offer(command);
        if (Thread.currentThread() == controlSurfaceSession) {
            runAllQueuedTasks();
        } else {
            extension.getHost().requestFlush();
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
            try {
                task.run();
            } catch (Exception ex) {
                LOG.error("Executer task execution error.", ex);
            } finally {
                task = tasks.poll();
                ExecutionContext.destroy();
            }
        }
    }

    @Override
    public void run() {
        try {
            runAllQueuedTasks();
        } finally {
            extension.getHost().scheduleTask(this, 0L);
        }
    }
}
