package com.github.jhorology.bitwig.extended.api;

import com.bitwig.extension.callback.ValueChangedCallback;
import java.util.List;

public interface ListValueChangedCallback<V> extends ValueChangedCallback {
    void valueChanged(List<V> values);
}
