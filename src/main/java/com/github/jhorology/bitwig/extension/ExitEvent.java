package com.github.jhorology.bitwig.extension;

/**
 * 
 */
public class ExitEvent extends AbstractExtensionEvent {
    /**
     * Constructs an event that notifies extension's end of lifecycle.
     * @param extension 
     */
    public ExitEvent(final AbstractExtension extension) {
        super(extension);
    }
}
