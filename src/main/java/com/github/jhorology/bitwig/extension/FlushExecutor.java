package com.github.jhorology.bitwig.extension;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;

import com.google.common.eventbus.Subscribe;

/**
 * Executor class that always runs tasks in 'Control Surface Session' thread.
 */
public class FlushExecutor implements Executor {
    private Queue<Runnable> tasks;
    private AbstractExtension extension;
    
    @Subscribe
    public void onInit(InitEvent e) {
        tasks = new ArrayBlockingQueue<>(32);
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
    }

    private synchronized void runAllQueuedTasks() {
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
}
