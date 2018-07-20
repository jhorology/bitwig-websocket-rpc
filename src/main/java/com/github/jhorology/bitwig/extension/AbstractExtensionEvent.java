package com.github.jhorology.bitwig.extension;

/**
 * An abstract base class for extension event. 
 */
public abstract class AbstractExtensionEvent {
    protected final AbstractExtension extension;
    
    /**
     * Construct instance with extension.
     * @param extension 
     */
    protected AbstractExtensionEvent(AbstractExtension extension) {
        this.extension = extension;
    }
    
    /**
     * get an instance of extension.
     * @return 
     */
    public AbstractExtension getExtension() {
        return extension;
    }
}
