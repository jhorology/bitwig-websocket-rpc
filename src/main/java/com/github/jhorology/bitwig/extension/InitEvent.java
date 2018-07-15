package com.github.jhorology.bitwig.extension;

/**
 *  An event that notifies extension's start of lifecycle
 */
public class InitEvent extends AbstractExtensionEvent {
    /**
     * Constructs an event that notifies extensio's start of lifecycle
     * @param extension 
     */
    public InitEvent(AbstractExtension extension) {
        super(extension);
    }
}
