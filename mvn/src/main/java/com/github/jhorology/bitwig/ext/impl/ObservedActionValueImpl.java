//#excludeif bitwig.extension.api.version < 10
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

// jdk
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// bitwig API
import com.bitwig.extension.callback.ObjectValueChangedCallback;
import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.BooleanValue;

// source
import com.github.jhorology.bitwig.ext.IdValuePair;
import com.github.jhorology.bitwig.ext.api.ObservedActionValue;
import java.util.Collection;


/**
 * An implementation of Value that reports observed direct parameter.
 */
public class ObservedActionValueImpl implements ObservedActionValue {
    private final Map<String, BooleanValue> enabledBooleanValues;
    private Map<String, IdValuePair<String, Boolean>> observedValues;
    private final List<ObjectValueChangedCallback<IdValuePair<String, Boolean>>> callbacks;
    private boolean subscribed;

    /**
     * Constructor.
     * @param name
     */
    ObservedActionValueImpl(Application application) {
        this.callbacks = new ArrayList<>();
        this.observedValues = Collections.emptyMap();
        this.enabledBooleanValues = Stream.of(application.getActions())
             .collect(Collectors.toMap(a -> a.getId(), a -> {
                 BooleanValue isEnabled = a.isEnabled();
                 isEnabled.addValueObserver((boolean b) -> {
                     IdValuePair<String, Boolean> v = observedValues.get(a.getId());
                     if (v != null) {
                         v.setValue(b);
                         notifyValue(v);
                     }
                 });
                 return isEnabled;
             }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObservedIds(String[] ids) {
        observedValues.values().forEach(o -> {
            BooleanValue v = enabledBooleanValues.get(o.getId());
            if (v != null) {
                v.unsubscribe();
            }
        });
        if (ids != null) {
            observedValues = Stream.of(ids)
                .collect(Collectors.toMap(id -> id, id -> {
                    BooleanValue v = enabledBooleanValues.get(id);
                    if (v != null) {
                        v.subscribe();
                    }
                    return new IdValuePair<>(id, v !=  null ? v.get() : false);
                }));
        }
        if (subscribed) {
            notifyValues();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled(String id) {
        return enabledBooleanValues.get(id).get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<IdValuePair<String, Boolean>> values() {
        return observedValues.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void markInterested() {
        subscribe();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSubscribed() {
        return subscribed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setIsSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
        setObservedIds(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe() {
        setIsSubscribed(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe() {
        setIsSubscribed(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addValueObserver(ObjectValueChangedCallback<IdValuePair<String, Boolean>> callback) {
        callbacks.add(callback);
    }

    /**
     * Notify observed values to observers.
     */
    void notifyValues() {
        values().stream().forEach(this::notifyValue);
    }

    private void notifyValue(IdValuePair<String, Boolean> v) {
        callbacks.stream().forEach(cb -> cb.valueChanged(v));
    }

}
