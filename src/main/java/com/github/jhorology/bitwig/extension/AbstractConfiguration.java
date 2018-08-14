/*
 * Copyright (c) 2018 Masafumi Fujimaru
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.extension;

// jdk
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// bitwig api
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

// dependencies
import com.google.gson.annotations.Expose;
import java.util.logging.Level;

/**
 * A base class for managing extension's configuration.
 */
public abstract class AbstractConfiguration {
    private final static Logger LOG = Logger.getLogger(AbstractConfiguration.class);
    // options for future use.
    protected static final boolean USE_RC_FILE = true;
    private static final boolean WRITE_THROUGHT_RC_FILE = false;
    
    // populate from json -->
    @Expose
    private Logger.Severity logLevel = Logger.Severity.ERROR;
    // <--

    private ControllerHost host;
    private ControllerExtensionDefinition definition;
    private Path rcFilePath;
    private boolean valueChanged;
    private boolean requestedReset;

    /**
     * Initialize the this configuration.
     * @param host
     */
    protected abstract void onInit(ControllerHost host);
    
    /**
     * De-initialize the this configuration.
     */
    protected abstract void onExit();
    
    /**
     * Initialize the this configuration.
     * @param extension 
     */
    void init(ControllerHost host, ControllerExtensionDefinition definition) {
        this.host = host;
        this.definition = definition;
        
        if (USE_RC_FILE) {
            rcFilePath = rcFilePath();
            if (Files.exists(rcFilePath)) {
                try {
                    ExtensionUtils.populateJsonProperties(rcFilePath, this);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
        Logger.Severity severity = ExtensionUtils.getPreferenceAsEnum
            (host, "Log Level", "Debug", logLevel, v -> {
                if (logLevel != v) {
                    logLevel = v;
                    Logger.setLevel(v);
                    valueChanged();
                }
            });
        
        if (!USE_RC_FILE) {
            logLevel = severity;
        }
        valueChanged = false;
        onInit(host);
    }

    /**
     * De-initialize the this configuration.
     */
    void exit() {
        if (USE_RC_FILE && !WRITE_THROUGHT_RC_FILE &&
            valueChanged &&
            !requestedReset) {
            try {
                ExtensionUtils.writeJsonFile(this, rcFilePath);
            } catch (IOException ex) {
                LOG.error(ex);
            }
        }
        if (requestedReset) {
            deleteRcFiles();
        }
        onExit();
    }
    
    /**
     * Return a log level that defined as configuration value.
     * @return 
     */
    Logger.Severity getLogLevel() {
        return logLevel;
    }

    /**
     * Inherited class should call this method when configuration value has been changed.
     */
    protected void valueChanged() {
        if (USE_RC_FILE && WRITE_THROUGHT_RC_FILE) {
            try {
                ExtensionUtils.writeJsonFile(this, rcFilePath);
            } catch (IOException ex) {
                LOG.error(ex);
            }
        }
        valueChanged = true;
    }

    protected void requestReset() {
        requestedReset = true;
    }

    private void deleteRcFiles() {
        String prefix = ".bitwig.extension." + definition.getName(); 
        try {
            Files.list(Paths.get(System.getProperty("user.home")))
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().startsWith(prefix))
                .forEach((path) -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ex) {
                            LOG.error(ex);
                        }
                    });
        } catch (IOException ex) {
            LOG.error(ex);
        }
                   
    }
    private Path rcFilePath() {
        StringBuilder fileName = new StringBuilder(".bitwig.extension.");
        fileName.append(definition.getName());
        fileName.append("-");
        fileName.append(definition.getVersion());
        return Paths.get(System.getProperty("user.home"), fileName.toString());
    }
}
