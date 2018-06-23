package com.github.jhorology.bitwig.extension;
import java.util.concurrent.Executor;
import com.google.common.eventbus.Subscribe;
import java.util.ArrayDeque;
import java.util.Queue;

public class ExtensionThreadExecutor implements Executor {
    final Queue<Runnable> tasks = new ArrayDeque<Runnable>();
    
    public ExtensionThreadExecutor() {
    }
    
    @Override
    public synchronized void execute(Runnable command) {
        tasks.offer(command);
    }

    @Subscribe
    public synchronized void onFlush(FlushEvent e) {
        Runnable task;
        while((task = tasks.poll()) != null) {
            task.run();
        }
    }
}
