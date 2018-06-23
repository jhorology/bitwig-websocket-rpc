package com.github.jhorology.bitwig;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class WebSocketRpcServerExtensionDefinition extends ControllerExtensionDefinition
{
    private static final UUID DRIVER_ID = UUID.fromString("68aa62d8-5a50-48d4-b9e4-9d684307f7ce");
   
    public WebSocketRpcServerExtensionDefinition() {
    }

    @Override
    public String getName() {
        return "WebSocket RPC";
    }
   
    @Override
    public String getAuthor() {
        return "hogehoge";
    }

    @Override
    public String getVersion() {
        return "snapshot";
    }

    @Override
    public UUID getId() {
        return DRIVER_ID;
    }
   
    @Override
    public String getHardwareVendor() {
        return "WebSocket RPC";
    }
   
    @Override
    public String getHardwareModel() {
        return "WebSocket RPC";
    }

    @Override
    public int getRequiredAPIVersion() {
        return 6;
    }

    @Override
    public int getNumMidiInPorts() {
        return 0;
    }

    @Override
    public int getNumMidiOutPorts() {
        return 0;
    }

    @Override
    public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType) {
    }

    @Override
    public WebSocketRpcServerExtension createInstance(final ControllerHost host) {
        return new WebSocketRpcServerExtension(this, host);
    }
}
