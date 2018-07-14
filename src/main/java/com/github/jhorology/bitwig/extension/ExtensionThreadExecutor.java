package com.github.jhorology.bitwig.extension;
import java.util.concurrent.Executor;
import com.bitwig.extension.controller.api.ControllerHost;

public class ExtensionThreadExecutor implements Executor {
    private final ControllerHost host;
    
    public ExtensionThreadExecutor(ControllerHost host) {
        this.host = host;
    }
    
    @Override
    public synchronized void execute(Runnable command) {
        // maybe not callable from extension thread.
        host.scheduleTask( command, 0L);
    }
}
