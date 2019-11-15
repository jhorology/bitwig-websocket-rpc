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
import java.util.UUID;
import java.util.function.BiConsumer;

// bitwig api
import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;

// provided dependencies

// dependencies
import com.google.gson.annotations.Expose;

/**
 * A base definition class for supporting instantiation from JSON resource.<br>
 * JSON file should be named {@code "bitwig-extension.json"} and need to be placed in root class path of extension jar.<br>
 * JSON example:
 * <pre>{@code
 * {
 *   "name": "WebSocket RPC",                           // required
 *   "author" : "Masafumi",                             // required
 *   "version": "1.0-SHANPSHOT",                        // required
 *   "id": "68aa62d8-5a50-48d4-b9e4-9d684307f7ce",      // required
 *   "requiredAPIVersion": 7,                           // required
 *   "usingBetaAPI": true,                              // optional, default false
 *   "helpFilePath": null,                              // optional, default null 
 *   "shouldFailOnDeprecatedUse": false,                // optional, default false
 *   "errorReportingEMail": "jhorology2014@gmail.com",  // optional, default null
 *   "hardwareVendor": "WebSocket RPC",                 // required
 *   "hardwareModel": "WebSocket RPC",                  // required
 *   "numMidiInPorts": 0,                               // optional, default 0
 *   "numMidiOutPorts": 0,                              // optional, default 0
 *   "macMidiInPortNames": [],                          // optional, default null
 *   "macMidiOutPortNames": [],                         // optional, default null
 *   "windowsMidiInPortNames": [],                      // optional, default null
 *   "windowsMidiOutPortNames": [],                     // optional, default null
 *   "linuxMidiInPortNames": [],                        // optional, default null
 *   "linuxMidiOutPortNames": [],                       // optional, default null
 *   "config": {                                        // optional, should define field(s) in inherited class.
 *       "webSocketPort": 8887,
 *       "logLevel": "WARN",
 *       "useRcFile": true,
 *       "writeThroughtRcFile": false
 *   }
 * }
 * }</pre>
 */
public abstract class AbstractExtensionDefinition
    extends ControllerExtensionDefinition {
    private static final String EXTENSION_JSON = "bitwig-extension.json";
    private static final String[] EMPTY_STRING_ARRAY = {};
    // populate from json -->
    @Expose
    private String name;
    @Expose
    private String author;
    @Expose
    private String version;
    @Expose
    private String id;
    @Expose
    private int requiredAPIVersion;
    @Expose
    private boolean usingBetaAPI;
    @Expose
    private String helpFilePath;
    @Expose
    private boolean shouldFailOnDeprecatedUse;
    @Expose
    private String errorReportingEMail;
    @Expose
    private String hardwareVendor;
    @Expose
    private String hardwareModel;
    @Expose
    private int numMidiInPorts;
    @Expose
    private int numMidiOutPorts;
    @Expose
    private String[] macMidiInPortNames;
    @Expose
    private String[] macMidiOutPortNames;
    @Expose
    private String[] windowsMidiInPortNames;
    @Expose
    private String[] windowsMidiOutPortNames;
    @Expose
    private String[] linuxMidiInPortNames;
    @Expose
    private String[] linuxMidiOutPortNames;
    // <--

    /**
     * Default constructor. 
     */
    protected AbstractExtensionDefinition() {
        try {
            ExtensionUtils.populateJsonProperties(EXTENSION_JSON, this);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * The name of the extension.<br>
     * An implementation of {@link com.bitwig.extension.ExtensionDefinition#getName()}
     * @return 
     */
    @Override
    public String getName() {
        return name;
    }
    
    /**
     * The author of the extension.<br>
     * An implementation of {@link com.bitwig.extension.ExtensionDefinition#getAuthor()}
     * @return 
     */
    @Override
    public String getAuthor() {
        return author;
    }

    /**
     * The version of the extension.<br>
     * An implementation of {@link com.bitwig.extension.ExtensionDefinition#getVersion()}
     * @return 
     */
    @Override
    public String getVersion() {
        return version;
    }

    /**
     * A unique id that identifies this extension.<br>
     * A override of {@link com.bitwig.extension.ExtensionDefinition#getId()}
     * @return 
     */
    @Override
    public UUID getId() {
        return UUID.fromString(id);
    }

    /**
     * The minimum API version number that this extensions requires.<br>
     * A override of {@link com.bitwig.extension.ExtensionDefinition#getRequiredAPIVersion()}
     * @return 
     */
    @Override
    public int getRequiredAPIVersion() {
        return requiredAPIVersion;
    }
    
    /**
     * Is this extension is using Beta APIs?<br>
     * A override of {@link com.bitwig.extension.ExtensionDefinition#isUsingBetaAPI()}
     * @return 
     */
    @Override
    public boolean isUsingBetaAPI() {
        return usingBetaAPI;
    }

    /**
     * Gets a path within the extension's jar file where documentation for this extension can be found or null if there is none. 
     * At the moment this file needs to be a PDF file but other file formats maybe supported in the future.<br>
     * A override of {@link com.bitwig.extension.ExtensionDefinition#getHelpFilePath()}
     * @return 
     */
    @Override
    public String getHelpFilePath() {
        return helpFilePath;
    }
    
    /**
     * If true then this extension should fail when it calls a deprecated method in the API. This is useful during development.
     * A override of {@link com.bitwig.extension.ExtensionDefinition#shouldFailOnDeprecatedUse()}
     * @return 
     */
    @Override
    public boolean shouldFailOnDeprecatedUse() {
        return shouldFailOnDeprecatedUse;
    }
    
    /**
     * An e-mail address that can be used to contact the author of this extension if a problem is detected with it or null if none.
     * A override of {@link com.bitwig.extension.ExtensionDefinition#getErrorReportingEMail()}
     * @return 
     */
    @Override
    public String getErrorReportingEMail() {
        return errorReportingEMail;
    }
    
    /**
     * The vendor of the controller that this extension is for.<br>
     * An implementation of {@link com.bitwig.extension.controller.ControllerExtensionDefinition#getHardwareVendor()}
     * @return 
     */
    @Override
    public String getHardwareVendor() {
        return hardwareVendor;
    }

    /**
     * The model name of the controller that this extension is for.<br>
     * An implementation of {@link com.bitwig.extension.controller.ControllerExtensionDefinition#getHardwareModel()}
     * @return 
     */
    @Override
    public String getHardwareModel() {
        return hardwareModel;
    }

    /**
     * The number of MIDI in ports that this controller extension has.<br>
     * An implementation of {@link com.bitwig.extension.controller.ControllerExtensionDefinition#getNumMidiInPorts()}
     * @return 
     */
    @Override
    public int getNumMidiInPorts() {
        return numMidiInPorts;
    }

    /**
     * The number of MIDI out ports that this controller extension has.<br>
     * An implementation of {@link com.bitwig.extension.controller.ControllerExtensionDefinition#getNumMidiOutPorts()}
     * @return 
     */
    @Override
    public int getNumMidiOutPorts() {
        return numMidiOutPorts;
    }

    /**
     * Lists the {@link com.bitwig.extension.controller.AutoDetectionMidiPortNamesList AutoDetectionMidiPortNames}
     * that defines the names of the MIDI in and out ports that can be used for auto detection of the controller for
     * the supplied platform type.<br>
     * An implementation of {@link com.bitwig.extension.controller.ControllerExtensionDefinition#listAutoDetectionMidiPortNames()}
     * @param list
     * @param platformType 
     */
    @Override
    public void listAutoDetectionMidiPortNames(AutoDetectionMidiPortNamesList list, PlatformType platformType) {
        if (numMidiInPorts <=0 && numMidiOutPorts <= 0) return;
        
        BiConsumer<String[], String[]> add = (in, out) -> {
            if ((in != null && in.length > 0) || (out != null && out.length > 0)) {
                list.add(in != null ? in : EMPTY_STRING_ARRAY,
                         out != null ? out : EMPTY_STRING_ARRAY);
            }
        };
        switch(platformType) {
        case MAC:
            add.accept(macMidiInPortNames, macMidiOutPortNames);
            break;
        case WINDOWS:
            add.accept(windowsMidiInPortNames, windowsMidiOutPortNames);
            break;
        case LINUX:
            add.accept(linuxMidiInPortNames, linuxMidiOutPortNames);
            break;
        }
    }
}
