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
package com.github.jhorology.bitwig.ext;

// dependencies
import com.google.gson.annotations.Expose;

/**
 * JSON serializable raw value object class for id/value pair.
 * @author masafumi
 */
public class IdValuePair<I,V> {
    @Expose
    private final I id;

    @Expose
    private V value;

    public IdValuePair(I id) {
        this.id = id;
    }
    
    public IdValuePair(I id, V value) {
        this(id);
        this.value = value;
    }

    public I getId() {
        return id;
    }
    
    public V getValue() {
        return value;
    }
    
    public void setValue(V value) {
        this.value = value;
    }
}
