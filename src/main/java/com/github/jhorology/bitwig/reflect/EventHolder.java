/*
 * Copyright (c) 2018 Masafumi Fujimaru
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
package com.github.jhorology.bitwig.reflect;

import com.bitwig.extension.callback.ValueChangedCallback;
import com.bitwig.extension.controller.api.Subscribable;
import com.bitwig.extension.controller.api.Value;
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.google.common.eventbus.EventBus;
import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.NotificationEvent;

/**
 * An event holder class.<br>
 * The return value of method is granted to implementing both interface of Subscribable and Value.
 */
public class EventHolder extends MethodHolder implements RpcEvent {
    private final Collection<WebSocket> clients;
    private final Collection<WebSocket> triggerOnceClients;
    private final EventBus pushEventBus;

    EventHolder(ModuleHolder<?> module, Method method, EventBus pushEventBus) {
        this(module, method, null, pushEventBus);
    }
    
    EventHolder(ModuleHolder<?> module, Method method, MethodHolder parantChain, EventBus pushEventBus) {
        super(module, method, parantChain);
        this.pushEventBus =pushEventBus;
        clients = new LinkedList<>();
        triggerOnceClients = new ArrayList<>();
        subscribeBitwigEvent();
    }

    private void subscribeBitwigEvent() {
        try {
            Value valueObject = (Value)getReturnValue();
            ValueChangedCallback callback =
                BitwigCallbacks.newValueChangedCallback(valueObject, v -> {
                        if (!clients.isEmpty()) {
                            Notification notification = new Notification(this.getAbsoluteName(), v);
                            pushEventBus.post(new NotificationEvent(notification, clients));
                        }
                    });
            valueObject.addValueObserver(callback);
            syncSubscribedState();
            Logger.getLogger(EventHolder.class)
                .info("Event[" + getAbsoluteName() + "] Succeed registering observer.");
        } catch (Exception ex) {
            Logger.getLogger(EventHolder.class)
                .error("Event[" + getAbsoluteName() + "] Failed registering observer.", ex);
        }
    }
    
    @Override
    public void subscribe(WebSocket client) {
        if (!clients.contains(client)) {
            clients.add(client);
        }
        syncSubscribedState();
    }
    
    @Override
    public void subscribeOnce(WebSocket client) {
        subscribe(client);
        if (!triggerOnceClients.contains(client)) {
            triggerOnceClients.add(client);
        }
    }

    @Override
    public void unsubscribe(WebSocket client) {
        clients.remove(client);
        triggerOnceClients.remove(client);
        syncSubscribedState();
    }
    
    @Override
    public void post(Object params) {
        if (!clients.isEmpty()) {
            // trigger synchronous event
            Notification notification = new Notification(getAbsoluteName(), params);
            pushEventBus.post(new NotificationEvent(notification, clients));
            triggerOnceClients.stream().forEach(e -> clients.remove(e));
            triggerOnceClients.clear();
        }
        syncSubscribedState();
    }
    
    void clear() {
        clients.clear();
        triggerOnceClients.clear();
    }

    /**
     * Sync state of event subscription between RPC and Bitwig Studio.
     */
    private void syncSubscribedState() {
        try {
            // get cached return value.
            Subscribable subscribeable = (Subscribable)getReturnValue();
            if (subscribeable == null) {
                return;
            }
            if (clients.isEmpty() && subscribeable.isSubscribed()) {
                subscribeable.unsubscribe();
                return;
            }
            if (!clients.isEmpty() && !subscribeable.isSubscribed()) {
                subscribeable.subscribe();
                return;
            }
        } catch (Exception ex) {
            throw new RpcException(ex);
        }
    }
}
