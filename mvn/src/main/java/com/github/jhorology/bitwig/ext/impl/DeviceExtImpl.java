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
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DirectParameterValueDisplayObserver;
import com.bitwig.extension.controller.api.StringArrayValue;

// source
import com.github.jhorology.bitwig.ext.api.DeviceExt;
import com.github.jhorology.bitwig.ext.api.ObservedDirectParameterValue;

/**
 * an implementation of extended Device API.
 */
class DeviceExtImpl implements DeviceExt {
    private final DirectParameterIdArrayValueImpl directParameterIdArrayValue;
    private final ObservedDirectParameterValueImpl<String> observedDirectParameterNameValue;
    private final ObservedDirectParameterValueImpl<Double> observedDirectParameterNormalizedValue;
    private final ObservedDirectParameterValueImpl<String> observedDirectParameterDisplayValue;
    private final DirectParameterValueDisplayObserver observer;

    /**
     * Constructor.
     * @param device
     */
    DeviceExtImpl(Device device) {
        directParameterIdArrayValue = new DirectParameterIdArrayValueImpl();
        observedDirectParameterNameValue = new ObservedDirectParameterValueImpl<>(true);
        observedDirectParameterNormalizedValue = new ObservedDirectParameterValueImpl<>(true);
        observedDirectParameterDisplayValue = new ObservedDirectParameterValueImpl<>(false);
        
        device.addDirectParameterIdObserver((String[] parameterIds) -> deviceChanged(parameterIds));
        device.addDirectParameterNameObserver(256,
                (String id, String value) -> observedDirectParameterNameValue.put(id, value));
        device.addDirectParameterNormalizedValueObserver((String id, 
                double value) -> observedDirectParameterNormalizedValue.put(id, value));
        observer = device.addDirectParameterValueDisplayObserver(256,
                (String id, String value) -> observedDirectParameterDisplayValue.put(id, value));
        observer.setObservedParameterIds(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public StringArrayValue directParameterIdArray() {
        return directParameterIdArrayValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObservedDirectParameterIds(String[] ids) {
        observedDirectParameterNameValue.setObservedIds(ids);
        observedDirectParameterNormalizedValue.setObservedIds(ids);
        observedDirectParameterDisplayValue.setObservedIds(ids);
        observer.setObservedParameterIds(ids);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservedDirectParameterValue<String> observedDirectParameterDisplayValue() {
        return observedDirectParameterDisplayValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ObservedDirectParameterValue<String> observedDirectParameterName() {
        return observedDirectParameterNameValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObservedDirectParameterValue<Double> observedDirectParameterNormalizedValue() {
        return observedDirectParameterNormalizedValue;
    }
    
    private void deviceChanged(String[] paramaterIds) {
        directParameterIdArrayValue.valueChanged(paramaterIds);
        observedDirectParameterNameValue.deviceChanged();
        observedDirectParameterNormalizedValue.deviceChanged();
        observedDirectParameterDisplayValue.deviceChanged();
    }
}
