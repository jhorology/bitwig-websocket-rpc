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
package com.github.jhorology.bitwig.ext.impl;

// bitwig api
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.Clip;
import com.bitwig.extension.controller.api.Device;

// source

// TODO Action#isEnabled() is dead at 3.1 Beta 4
//#if bitwig.extension.api.version >= 99
import com.github.jhorology.bitwig.ext.api.ApplicationExt;
//#endif
import com.github.jhorology.bitwig.ext.api.ChannelExt;
import com.github.jhorology.bitwig.ext.api.ClipExt;
import com.github.jhorology.bitwig.ext.api.ExtApi;
import com.github.jhorology.bitwig.ext.api.DeviceExt;
import com.github.jhorology.bitwig.ext.api.VuMeterChannelMode;
import com.github.jhorology.bitwig.ext.api.VuMeterPeakMode;

/**
 * A Default factory class for extended API
 * @author masafumi
 */
public class DefaultExtApiFactory implements ExtApi {
    // TODO Action#isEnabled() is dead at 3.1 Beta 4
    //#if bitwig.extension.api.version >= 99
    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationExt createApplicationExt(Application application) {
        return new ApplicationExtImpl(application);
    }
    //#endif
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DeviceExt createDeviceExt(Device device) {
        return new DeviceExtImpl(device);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelExt createChannelExt(Channel channel, int vuMeterRange, VuMeterChannelMode vuMeterChannelMode, VuMeterPeakMode vuMeterPeakMode) {
        return new ChannelExtImpl(channel, vuMeterRange, vuMeterChannelMode, vuMeterPeakMode);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ClipExt createClipExt(Clip clip, int gridWidth, int gridHeight) {
        return new ClipExtImpl(clip, gridWidth, gridHeight);
    }
}
