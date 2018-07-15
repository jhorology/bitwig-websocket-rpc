package com.github.jhorology.bitwig.extension;

/**
 * 
 */
public abstract class AbstractExtensionEvent {
    protected final AbstractExtension extension;
    
    /**
     * Constructor
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
