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

// source
import com.github.jhorology.bitwig.ext.api.ApplicationExt;
import com.github.jhorology.bitwig.ext.api.ObservedActionValue;


/**
 * an implementation of extended Application API.
 */
class ApplicationExtImpl implements ApplicationExt {
    private final ObservedActionValueImpl observedAction;

    /**
     * Constructor.
     * @param device
     */
    ApplicationExtImpl(Application application) {
        observedAction = new ObservedActionValueImpl(application);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ObservedActionValue observedAction() {
        return observedAction;
    }
}
