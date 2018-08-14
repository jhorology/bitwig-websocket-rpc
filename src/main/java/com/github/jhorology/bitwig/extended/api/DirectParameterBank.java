package com.github.jhorology.bitwig.extended.api;

import com.bitwig.extension.controller.api.Value;

public interface DirectParameterBank {
    DirectParameterListValue all();
    Value<HashedValueChangedCallback<String, Double>> normalizedValue();
    Value<HashedValueChangedCallback<String, String>> displayValue();
    void inc(String id, Number increment, Number resolution);
    void set(String id, Number value, Number resolution);
}
