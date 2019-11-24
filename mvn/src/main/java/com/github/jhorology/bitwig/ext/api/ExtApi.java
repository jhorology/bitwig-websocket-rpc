/*
 * Copyright (c) 2019 Masafumi Fujimaru
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
package com.github.jhorology.bitwig.ext.api;

// bitwig API
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.Device;

/**
 * An abstract factory interface for extended API.
 */
public interface ExtApi {
    //#if bitwig.extension.api.version >= 10
    /**
     * create an instance of extended Application API.
     * @param application the instance of base application
     * @return 
     */
    ApplicationExt createApplicationExt(Application application);
    //#endif
    
    /**
     * create an instance of extended Device API.
     * @param device the instance of base Device
     * @return 
     */
    DeviceExt createDeviceExt(Device device);
    
    /**
     * create an instance of extended Channel API.
     * @param channel
     * @param vuMeterRange
     * @param vuMeterChammelMode
     * @param vuMeterPeakMode
     * @return 
     */
    ChannelExt createChannelExt(Channel channel, int vuMeterRange, VuMeterChannelMode vuMeterChammelMode, VuMeterPeakMode vuMeterPeakMode);
    
    /**
     * create an instance of extended Clip API.
     * @param clip
     * @param gridWidth
     * @param gridHeight
     * @return 
     */
    ClipExt createClipExt(Clip clip, int gridWidth, int gridHeight);
}
