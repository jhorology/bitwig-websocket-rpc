package com.github.jhorology.bitwig.extension;
import java.util.concurrent.Executor;
import com.bitwig.extension.controller.api.ControllerHost;

/**
 * Executor class  that always runs tasks in 'Controller Surface Session' thread.
 */
public class ExtensionThreadExecutor implements Executor {
    private final ControllerHost host;
    
    /**
     * Create an Executor that always runs tasks in 'Controller Surface Session' thread.
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
        host.scheduleTask(() -> {
                ExecutionContext.init(host);
                try {
                    command.run();
                } finally {
                    ExecutionContext.destroy();
                }
            }, 0L);
    }
}
