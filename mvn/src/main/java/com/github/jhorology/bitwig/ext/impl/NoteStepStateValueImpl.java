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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

// bitwig api
import com.bitwig.extension.callback.ObjectValueChangedCallback;
import com.bitwig.extension.controller.api.Clip;

// source
import com.github.jhorology.bitwig.ext.NoteStepState;
import com.github.jhorology.bitwig.ext.api.CollectionValue;


/**
 * an implementation of extended Clip API.
 */
class NoteStepStateValueImpl implements CollectionValue<NoteStepState> {
    private final List<ObjectValueChangedCallback<NoteStepState>> callbacks;
    private final Set<NoteStepState> values;
    private boolean subscribed;

    /**
     * Constructor.
     * @param clip the instance or Clip API.
     */
    NoteStepStateValueImpl(Clip clip, int gridWidth, int gridHeight) {
        callbacks = new ArrayList<>();
        values = new HashSet<>();
        clip.addStepDataObserver(this::notifyNoteStepState);
    }
    
    @Override
    public Collection<NoteStepState> values() {
        return values;
    }

    @Override
    public void markInterested() {
        subscribe();
    }

    @Override
    public void addValueObserver(ObjectValueChangedCallback<NoteStepState> callback) {
        callbacks.add(callback);
    }

    @Override
    public boolean isSubscribed() {
        return subscribed;
    }

    @Override
    public void setIsSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    @Override
    public void subscribe() {
        setIsSubscribed(true);
    }

    @Override
    public void unsubscribe() {
        setIsSubscribed(false);
    }
    
    private void notifyNoteStepState(int x, int y, int state) {
        NoteStepState noteStepState =  new NoteStepState(x, y);
        noteStepState.setState(state);
        values.add(noteStepState);
        if (subscribed) {
            callbacks.forEach(c -> c.valueChanged(noteStepState));
        }
    }
}
