package com.github.jhorology.bitwig.extension;

public abstract class AbstractExtensionEvent {
    protected final AbstractExtension extension;
    
    protected AbstractExtensionEvent(AbstractExtension extension) {
        this.extension = extension;
    }
    
    public AbstractExtension getExtension() {
        return extension;
    }
}
