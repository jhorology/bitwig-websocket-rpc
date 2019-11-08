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

// bitwig api
import com.bitwig.extension.controller.api.Device;

// source
import com.github.jhorology.bitwig.ext.impl.DefaultExtApiFactory;

/**
 * Concrete factory class for extended API.
 */
public class ExtApiFactory implements ExtApi {
    private static final ExtApiFactory instance = new ExtApiFactory();
    private ExtApi factory = new DefaultExtApiFactory();
    
    private ExtApiFactory() {
    }

    public static ExtApiFactory getInstance() {
        return instance;
    }
    
    public void setFactory(ExtApi factory) {
        this.factory = factory;
    }
    
    @Override
    public DeviceExt createDeviceExt(Device device) {
        return factory.createDeviceExt(device);
    }
}
