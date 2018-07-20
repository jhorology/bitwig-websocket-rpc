package com.github.jhorology.bitwig.extension;

/**
 * An event class for notification of extension's end of lifecycle.
 */
public class ExitEvent extends AbstractExtensionEvent {
    ExitEvent(final AbstractExtension extension) {
        super(extension);
    }
}
