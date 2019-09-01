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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import org.slf4j.impl.LogSeverity;
import org.slf4j.impl.ScriptConsoleLogger;

/**
 * A base class for managing extension's configuration.
 */
public abstract class AbstractConfiguration {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractConfiguration.class);

    // options for future use.
    protected static final boolean USE_RC_FILE = true;
    private static final boolean WRITE_THROUGHT_RC_FILE = false;

    // populate from json -->
    @Expose(serialize = false)
    private LogSeverity logLevel;
    @Expose(serialize = false)
    protected boolean production;
    // <--

    protected boolean ignoreValueChanged;
    private ControllerExtensionDefinition definition;
    private Path rcFilePath;
    private boolean valueChanged;
    private boolean requestReset;

    /**
     * Default constructor.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public AbstractConfiguration() {
        resetToDefaults();
    }

    /**
     * write configuration to rc file.
     */
    public void writeRcFile(ControllerExtensionDefinition definition) {
        try {
            ExtensionUtils.writeJsonFile(this, getRcFilePath(definition));
        } catch (IOException ex) {
            LOG.error("Error writing rc file.", ex);
        }
    }

    /**
     * Return a build environment value, production build or not.
     */
    public boolean isProduction() {
        return production;
    }
    
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
     * Reset to defaults.
     */
    protected void resetToDefaults() {
        if (logLevel == null)
            logLevel = LogSeverity.ERROR;
    }

    /**
     * Initialize the this configuration.
     * @param extension
     */
    void init(ControllerHost host, ControllerExtensionDefinition definition) {
        this.definition = definition;
        this.rcFilePath = getRcFilePath(definition);
        if (USE_RC_FILE) {
            if (Files.exists(rcFilePath)) {
                try {
                    ExtensionUtils.populateJsonProperties(rcFilePath, this);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            ignoreValueChanged = true;
            host.scheduleTask(() -> {
                    ignoreValueChanged = false;
                },
                500L);
        }

        LogSeverity severity = ExtensionUtils.getPreferenceAsEnum
            (host, "Log Level", "Debug", LogSeverity.WARN, logLevel, (e, v) -> {
                if (ignoreValueChanged) {
                    v.set(logLevel.name());
                } else if (logLevel != e) {
                    logLevel = e;
                    ScriptConsoleLogger.setGlobalLogLevel(e);
                    valueChanged();
                }
            });

        if (!USE_RC_FILE) {
            logLevel = severity;
        }
        requestReset = false;
        valueChanged = false;
        onInit(host);

        host.getPreferences().getSignalSetting
            ("Apply new settings", "Restart (new settings need restart)", "Restart")
            .addSignalObserver(() -> host.restart());
        // TODO preference panel dosen't update at restat.
        host.getPreferences().getSignalSetting
            ("Reset to defaults", "Restart (new settings need restart)", "Restart")
            .addSignalObserver(() -> {
                    requestReset = true;;
                    host.restart();
                });
    }

    /**
     * De-initialize the this configuration.
     */
    void exit() {
        if (USE_RC_FILE && !WRITE_THROUGHT_RC_FILE &&
            valueChanged &&
            !requestReset) {
            try {
                ExtensionUtils.writeJsonFile(this, rcFilePath);
            } catch (IOException ex) {
                LOG.error("Error writing JSON file.",ex);
            }
        }
        if (requestReset) {
            resetToDefaults();
            deleteRcFiles();
        }
        onExit();
    }

    /**
     * Return a log level that defined as configuration value.
     * @return
     */
    LogSeverity getLogLevel() {
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
                LOG.error("Error writing JSON file.", ex);
            }
        }
        valueChanged = true;
    }

    protected void requestReset() {
        requestReset = true;
    }

    private void deleteRcFiles() {
        String prefix = ".bitwig.extension." + definition.getName();
        try {
            Files.list(Paths.get(System.getProperty("user.home")))
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().startsWith(prefix))
                .forEach((path) -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ex) {
                            LOG.error("Error deleting RC file.", ex);
                        }
                    });
        } catch (IOException ex) {
            LOG.error("Error deleting RC file.", ex);
        }
    }

    private Path getRcFilePath(ControllerExtensionDefinition def) {
        StringBuilder fileName = new StringBuilder(".bitwig.extension.");
        fileName.append(def.getName());
        fileName.append("-");
        fileName.append(def.getVersion());
        return Paths.get(System.getProperty("user.home"), fileName.toString());
    }
}
