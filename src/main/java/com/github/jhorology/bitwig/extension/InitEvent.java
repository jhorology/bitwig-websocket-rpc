package com.github.jhorology.bitwig.extension;

/**
 * An event class for notification of extension's start of lifecycle.
 */
public class InitEvent extends AbstractExtensionEvent {
    InitEvent(AbstractExtension extension) {
        super(extension);
    }
}
