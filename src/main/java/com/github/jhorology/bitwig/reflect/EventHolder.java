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

// jdk
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.lang.reflect.Method;

// bitwig api
import com.bitwig.extension.callback.ObjectValueChangedCallback;
import com.bitwig.extension.callback.ValueChangedCallback;
import com.bitwig.extension.controller.api.Value;

// dependencies
import org.java_websocket.WebSocket;

// source
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcEvent;
import com.github.jhorology.bitwig.rpc.RpcException;
import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;
import com.github.jhorology.bitwig.websocket.protocol.RequestContext;

/**
 * A RPC event holder class.<br>
 * The return value of method is guranteed to be implemented Value inteface.
 */
public class EventHolder extends MethodHolder implements RpcEvent {
    private static final Logger LOG = Logger.getLogger(EventHolder.class);
    
    // TODO make it configurable
    // late observer binding is not allowed by bitwig.
    // "This can only be called during driver initialization."
    private static final boolean AVAILABLE_LATE_BINDING_TO_HOST = false;
    // gurantee the posting current value on 'subscibe' is called.
    private static final boolean NOTIFY_CURRENT_VALUE_ON_SUBSCRIBE = true;
    private static final long WAIT_HOST_TRIGGER_ON_SUBSCRIBE = 150L;

    private final Collection<WebSocket> clients;
    private final Collection<WebSocket> triggerOnceClients;
    // observer is binded to host?
    private boolean bindedToHost;
    private boolean hostTriggered;
    // the last reported value from host
    private Object lastReportedParams;

    EventHolder(ModuleHolder<?> owner, Method method) {
        this(owner, method, null);
    }

    EventHolder(ModuleHolder<?> owner, Method method, MethodHolder parantChain) {
        super(owner, method, parantChain);
        // clients = new LinkedList<>();
        clients = new ArrayList<>();
        triggerOnceClients = new ArrayList<>();
        if (!AVAILABLE_LATE_BINDING_TO_HOST) {
            syncSubscribedState();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(WebSocket client) {
        if (getError() != null) {
            throw new RpcException(getError());
        }
        if (!clients.contains(client)) {
            clients.add(client);
            if (syncSubscribedState()) {
                // if Subscribable#subscribed() was called, (it's meaning first client of subscribers list.)
                // host may trigger callback, but it's only for the value that has changed since last reported.
                // so need to notify current value to the client if host will not trigger callback.
                notifyCurrentValueIfHostNotTriggered(client);
            } else {
                // should send a current value to the client comming after the first one.
                notifyCurrentValue();
            }
            if (Logger.isDebugEnabled())  {
                LOG.debug("[" + absoluteName + "] event has been subscribed by " + client(client));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribeOnce(WebSocket client) {
        if (getError() != null) {
            throw new RpcException(getError());
        }
        if (!clients.contains(client)) {
            clients.add(client);
            if (!triggerOnceClients.contains(client)) {
                triggerOnceClients.add(client);
            }
            if (syncSubscribedState()) {
                // if subscribed() was called
                notifyCurrentValueIfHostNotTriggered(client);
            } else {
                notifyCurrentValue();
            }
            if (Logger.isTraceEnabled())  {
                LOG.trace("[" + absoluteName + "] event has been subscribed once by " + client(client));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(WebSocket client) {
        if (getError() != null) {
            throw new RpcException(getError());
        }
        if (clients.remove(client)) {
            triggerOnceClients.remove(client);
            syncSubscribedState();
            if (Logger.isTraceEnabled())  {
                LOG.trace("[" + absoluteName + "] event has been unsubscribed by " + client(client));
            }
        }
    }
    

    void disconnect(WebSocket client) {
        boolean removed = clients.remove(client);
        if (removed) {
            triggerOnceClients.remove(client);
            syncSubscribedState();
            if (Logger.isTraceEnabled())  {
                LOG.trace("subscriber of [" + absoluteName + "] event has been disconnected. client:" + client(client));
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
        report.put("event", absoluteName);
        if (getError() != null) {
            report.put("error", getError());
        }
        return report;
    }

    /**
     * post event to all subscribers.
     * @param params
     */
    protected void onValueChanged(Object params) {
        hostTriggered = true;
        lastReportedParams = params;
        if (!clients.isEmpty() && owner.getPushModel() != null) {
            owner.getPushModel()
                .push(new Notification(absoluteName, params), clients);
            if (!triggerOnceClients.isEmpty()) {
                triggerOnceClients.stream().forEach(e -> clients.remove(e));
                triggerOnceClients.clear();
                if (clients.isEmpty()) {
                    syncSubscribedState();
                }
            }
        }
    }
    
    /**
     * post event to specified client.
     */
    private void post(Object params, WebSocket client) {
        if (clients.contains(client) && owner.getPushModel() != null) {
            owner.getPushModel()
                .push(new Notification(absoluteName, params), client);
            if (triggerOnceClients.remove(client)) {
                if (clients.remove(client)) {
                    syncSubscribedState();
                }
            }
        }
    }
    
    /**
     * post current value to client.
     */
    private void notifyCurrentValueIfHostNotTriggered(WebSocket client) {
        if (NOTIFY_CURRENT_VALUE_ON_SUBSCRIBE) {
            hostTriggered = false;
            PushModel pushModel = owner.getPushModel();
            owner.getHost().scheduleTask(() -> {
                    if (hostTriggered ||
                        lastReportedParams == null) {
                        return;
                    }
                    post(lastReportedParams, client);
                },
                WAIT_HOST_TRIGGER_ON_SUBSCRIBE);
        }
    }

    /**
     * post current value to client.
     */
    private void notifyCurrentValue() {
        // do not post message directly in here.
        // 'cause message should be sent at after Request/Reponse sequence.
        if (NOTIFY_CURRENT_VALUE_ON_SUBSCRIBE) {
            if (lastReportedParams != null) {
                RequestContext.getContext()
                    .addNotification(new Notification(absoluteName, lastReportedParams));
            }
        }
    }

    /**
     * Sync state of event subscription between RPC and Bitwig Studio.
     * @return host side state will change to subscribed
     */
    @SuppressWarnings({"UseSpecificCatch", "unchecked"})
    private boolean syncSubscribedState() {
        try {
            // TODO support method chain that has parameters
            Value value = (Value)getReturnValue(ReflectUtils.EMPTY_ARRAY);
            // I think late binding observer are probably better for host performance.
            if (!bindedToHost) {
                lastReportedParams = null;
                ValueChangedCallback callback =
                    BitwigCallbacks.newValueChangedCallback(value, this::onValueChanged);

                // for debug
                if (Logger.isDebugEnabled() && ObjectValueChangedCallback.class.equals(callback.getClass())) {
                    LOG.debug("[" + absoluteName + "] event usess ObjectValueChangedCallback.");
                }
                    
                // addValueObserver raise callback calls even after call unsubscribe()
                value.addValueObserver(callback);
                bindedToHost = true;
                value.unsubscribe();
                return false;
            }
            if (bindedToHost) {
                if (clients.isEmpty() && value.isSubscribed()) {
                    value.unsubscribe();
                    return false;
                }
                if (!clients.isEmpty() && !value.isSubscribed()) {
                    value.subscribe();
                    return true;
                }
            }
        } catch (Exception ex) {
            setError(ex, "Fialed syncing subscribed state.");
            throw new RpcException(ex);
        }
        return false;
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
