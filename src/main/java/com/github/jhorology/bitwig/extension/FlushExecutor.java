package com.github.jhorology.bitwig.extension;

import com.bitwig.extension.controller.api.ControllerHost;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

import com.google.common.eventbus.Subscribe;

/**
 * Executor class that always runs tasks within 'ControllerHost#flush()' method.
 */
public class FlushExecutor implements Executor {
    private Queue<Runnable> tasks;
    private AbstractExtension extension;
    private Logger LOG;
    private boolean requested;
    
    @Subscribe
    public void onInit(InitEvent e) {
        LOG = Logger.getLogger(FlushExecutor.class);
        tasks = new ArrayBlockingQueue<>(64);
        extension = e.getExtension();
    }
    
    @Subscribe
    public void onFlush(FlushEvent e) {
        runAllQueuedTasks();
    }
    
    @Subscribe
    public void onExit(ExitEvent e) {
        runAllQueuedTasks();
        tasks.clear();
    }
    
    /**
     * An implementation method of Executor interface
     * @param command 
     */
    @Override
    public synchronized void execute(Runnable command) {
        tasks.add(command);
        // TODO
        // is this safe?
        // maybe not callable from other than control surface session thread.
        if (!requested) {
            extension.getHost().requestFlush();
            requested = true;
        }
    }

    private synchronized void runAllQueuedTasks() {
        requested = false;
        Runnable task = tasks.poll();
        while(task != null) {
            ExecutionContext.init(extension);
            ExecutionContext context = ExecutionContext.getContext();
            try {
                task.run();
            } finally {
                if (context.isNextTickRequested()) {
                    final ControllerHost host = context.getHost();
                    task = tasks.peek();
                    if (task != null) {
                        host.scheduleTask(() -> host.requestFlush(), 0L);
                    }
                    task = null;
                } else {
                    task = tasks.poll();
                }
                ExecutionContext.destroy();
            }
        }
    }
}
