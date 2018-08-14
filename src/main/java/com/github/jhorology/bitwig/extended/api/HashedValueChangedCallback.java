package com.github.jhorology.bitwig.extended.api;

import com.bitwig.extension.callback.ValueChangedCallback;

public interface HashedValueChangedCallback<K,V> extends ValueChangedCallback {
    public void valueChanged(K id, V value);
}
