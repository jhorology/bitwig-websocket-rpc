package com.github.jhorology.bitwig.extension;
import java.util.concurrent.Executor;

/**
 * Executor class that always runs tasks in 'Control Surface Session' thread.
 */
public class ControlSurfaceSessionExecutor implements Executor {
    private final AbstractExtension extension;
    
    /**
     * Create an Executor that always runs tasks in 'Controller Surface Session' thread.
     * @param extension 
     */
    public ControlSurfaceSessionExecutor(AbstractExtension extension) {
        this.extension = extension;
    }
    
    /**
     * An implementation method of Executor interface
     * @param command 
     */
    @Override
    public void execute(Runnable command) {
        // is this safe?
        // maybe not callable from other than extension thread.
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
