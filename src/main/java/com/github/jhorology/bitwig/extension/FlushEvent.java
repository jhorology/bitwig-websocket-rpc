package com.github.jhorology.bitwig.extension;

/**
 * An event class for notification of recommended point to feedback controller surface.
 */
public class FlushEvent extends AbstractExtensionEvent {
    FlushEvent(final AbstractExtension extension) {
        super(extension);
    }
}
