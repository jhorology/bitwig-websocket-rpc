package com.github.jhorology.bitwig.extension;

import com.bitwig.extension.controller.api.ControllerHost;
import java.util.Queue;
import java.util.concurrent.Executor;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayDeque;

/**
 * Executor class that always runs tasks within 'ControllerHost#flush()' method.
 */
public class FlushExecutor implements Executor {
    private static final int IDLE = 0;
    private static final int REQUESTED = 1;
    private static final int WAITING = 2;
    private static final int SHUTDOWN = 3;
    private static final int QUEUE_SIZE = 64;

    private Thread controlSurfaceSession;
    private Queue<Runnable> tasks;
    private AbstractExtension extension;
    private Logger log;
    private int state = IDLE;
    @Subscribe
    public void onInit(InitEvent e) {
        log = Logger.getLogger(FlushExecutor.class);
        tasks = new ArrayDeque<>(QUEUE_SIZE);
        extension = e.getExtension();
        controlSurfaceSession = Thread.currentThread();
        state = IDLE;
    }
    
    @Subscribe
    public void onFlush(FlushEvent e) {
        runAllQueuedTasks();
    }
    
    @Subscribe
    public void onExit(ExitEvent e) {
        // runAllQueuedTasks();
        synchronized(this) {
            tasks.clear();
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
            // to aveilable to execute event that triggerd from another module's onExit.
            if (Thread.currentThread() == controlSurfaceSession) {
                Runnable task = tasks.poll();
                while(task != null) {
                    ExecutionContext.init(extension);
                    try {
                        task.run();
                    } finally {
                        ExecutionContext.destroy();
                        task = tasks.poll();
                    }
                }
            }
        } else if (state != WAITING) {
            // TODO
            // is this safe?
            // maybe not callable from other than control surface session thread.
            extension.getHost().requestFlush();
            state = REQUESTED;
        }
        if (Logger.isWarnEnabled()) {
            if (tasks.size() > (QUEUE_SIZE * 3 / 4)) {
                log.warn("task queues will reach full capacity. currently queued tasks:" + tasks.size());
            }
        }
    }

    private synchronized void runAllQueuedTasks() {
        if (state != REQUESTED) return;
        Runnable task = tasks.poll();
        while(task != null) {
            ExecutionContext.init(extension);
            ExecutionContext context = ExecutionContext.getContext();
            try {
                task.run();
            } finally {
                if (context.isNextTickRequested()) {
                    final ControllerHost host = context.getHost();
                    long waitMillis = context.getNextTickMillis();
                    if (waitMillis > 0) {
                        state = WAITING;
                        host.scheduleTask(() -> {
                                host.requestFlush();
                                state = REQUESTED;
                            },
                            waitMillis);
                    }
                    task = null;
                } else {
                    task = tasks.poll();
                }
                ExecutionContext.destroy();
            }
        }
        if (state != WAITING) state = IDLE;
    }
}
