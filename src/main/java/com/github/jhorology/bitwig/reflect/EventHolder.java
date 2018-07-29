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

// jvm
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.lang.reflect.Method;

// bitwig api
import com.bitwig.extension.callback.ValueChangedCallback;
import com.bitwig.extension.controller.api.Subscribable;
import com.bitwig.extension.controller.api.Value;

// dependencies
import org.java_websocket.WebSocket;

// source
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcException;
import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;

/**
 * An RPC event holder class.<br>
 * The return value of method is guranteed to be implemented Value inteface.
 */
public class EventHolder extends MethodHolder implements RpcEvent {
    private final Logger log;
    
    private final Collection<WebSocket> clients;
    private final Collection<WebSocket> triggerOnceClients;
    EventHolder(ModuleHolder<?> owner, Method method) {
        this(owner, method, null);
    }
    
    EventHolder(ModuleHolder<?> owner, Method method, MethodHolder parantChain) {
        super(owner, method, parantChain);
        log = Logger.getLogger(EventHolder.class);
        clients = new LinkedList<>();
        triggerOnceClients = new ArrayList<>();
        subscribeHostEvent();
    }

    @Override
    public void subscribe(WebSocket client) {
        if (getError() != null) {
            throw new RpcException(getError());
        }
        boolean alreadySubscribed = clients.contains(client);
        if (!alreadySubscribed) {
            clients.add(client);
            syncSubscribedState();
            if (Logger.isDebugEnabled())  {
                log.debug("[" + getAbsoluteName() + "] event has been subscribed by " + client(client));
            }
        }
    }
    
    @Override
    public void subscribeOnce(WebSocket client) {
        if (getError() != null) {
            throw new RpcException(getError());
        }
        boolean alreadySubscribed = clients.contains(client);
        if (!alreadySubscribed) {
            clients.add(client);
            if (!triggerOnceClients.contains(client)) {
                triggerOnceClients.add(client);
            }
            syncSubscribedState();
            if (Logger.isTraceEnabled())  {
                log.trace("[" + getAbsoluteName() + "] event has been subscribed once by " + client(client));
            }
        }
    }

    @Override
    public void unsubscribe(WebSocket client) {
        if (getError() != null) {
            throw new RpcException(getError());
        }
        boolean removed = clients.remove(client);
        if (removed) {
            triggerOnceClients.remove(client);
            syncSubscribedState();
            if (Logger.isTraceEnabled())  {
                log.trace("[" + getAbsoluteName() + "] event has been unsubscribed by " + client(client));
            }
        }
    }
    
    @Override
    public void post(Object params) {
        if (!clients.isEmpty()) {
            PushModel pushModel = owner.getPushModel();
            if (pushModel != null) {
                Notification notification = new Notification(getAbsoluteName(), params);
                pushModel.push(notification, clients);
            }
            triggerOnceClients.stream().forEach(e -> clients.remove(e));
            triggerOnceClients.clear();
            syncSubscribedState();
        }
    }

    void disconnect(WebSocket client) {
        boolean removed = clients.remove(client);
        if (removed) {
            triggerOnceClients.remove(client);
            syncSubscribedState();
            if (Logger.isTraceEnabled())  {
                log.trace("subscriber of [" + getAbsoluteName() + "] event has been disconnected. client:" + client(client));
            }
        }
    }

    /**
     * clear this instance.
     */
    @Override
    void clear() {
        super.clear();
        clients.clear();
        triggerOnceClients.clear();
    }
    
    /**
     * create a report object for this class.
     * @param eh
     * @return 
     */
    Object reportRpcEvent() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("event", getAbsoluteName());
        if (getError() != null) {
            report.put("error", getError());
        }
        return report;
    }

    @SuppressWarnings({"UseSpecificCatch", "unchecked"})
    private void subscribeHostEvent() {
        try {
            // TODO
            Value valueObject =
                (Value)getReturnValue(ReflectUtils.EMPTY_ARRAY);
            ValueChangedCallback callback =
                BitwigCallbacks.newValueChangedCallback(valueObject, params -> post(params));
            valueObject.addValueObserver(callback);
            syncSubscribedState();
            if (Logger.isDebugEnabled()) {
                log.debug("Event[" + getAbsoluteName() + "] Succeed registering observer.");
            }
        } catch (Exception ex) {
            setError(ex, "Fialed registering observer.");
            if (Logger.isWarnEnabled()) {
                log.warn("Event[" + getAbsoluteName() + "] Failed registering observer.", ex);
            }
        }
    }
    
    /**
     * Sync state of event subscription between RPC and Bitwig Studio.
     */
    @SuppressWarnings("UseSpecificCatch")
    private void syncSubscribedState() {
        try {
            // get cached return value.
            // TODO
            Subscribable subscribable = (Subscribable)getReturnValue(ReflectUtils.EMPTY_ARRAY);
            if (clients.isEmpty() && subscribable.isSubscribed()) {
                subscribable.unsubscribe();
                traceSubscribedStatus(subscribable);
                return;
            }
            if (!clients.isEmpty() && !subscribable.isSubscribed()) {
                subscribable.subscribe();
                traceSubscribedStatus(subscribable);
            }
        } catch (Exception ex) {
            setError(ex, "Fialed syncing subscribe state.");
            throw new RpcException(ex);
        }
    }

    private void traceSubscribedStatus(Subscribable subscribable) {
        if (Logger.isDebugEnabled()) {
            log.debug("Event[" + getAbsoluteName() + "] Subscribed status was changed. state:" + subscribable.isSubscribed());
        }
    }
    
    private String client(WebSocket client) {
        if (client == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(client.toString());
        if (client.getRemoteSocketAddress() != null) {
            sb.append(", ");
            sb.append(client.getRemoteSocketAddress().toString());
        }
        sb.append("]\n number of subscriber(s):" + clients.size());
        sb.append(']');
        return sb.toString();
    }
}
