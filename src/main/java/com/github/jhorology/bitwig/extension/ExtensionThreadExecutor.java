package com.github.jhorology.bitwig.extension;
import java.util.concurrent.Executor;
import com.bitwig.extension.controller.api.ControllerHost;

/**
 * 
 */
public class ExtensionThreadExecutor implements Executor {
    private final ControllerHost host;
    
    /**
     * 
     * @param host 
     */
    public ExtensionThreadExecutor(ControllerHost host) {
        this.host = host;
    }
    
    /**
     * An implementation method of Executor interface
     * @param command 
     */
    @Override
    public void execute(Runnable command) {
        // is this safe?
        // maybe not callable from other than extension thread.
        host.scheduleTask( command, 0L);
    }
}
