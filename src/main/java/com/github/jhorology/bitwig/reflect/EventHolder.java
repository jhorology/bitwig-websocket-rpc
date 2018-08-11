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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// bitwig api
import com.bitwig.extension.callback.ObjectValueChangedCallback;
import com.bitwig.extension.callback.ValueChangedCallback;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.Value;

// provided dependencies
import org.apache.commons.lang3.ArrayUtils;

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
 * @param <T>
 */
public class EventHolder<T extends Value> extends MethodHolder<T> implements RpcEvent {
    private static final Logger LOG = Logger.getLogger(EventHolder.class);
    
    // gurantee the posting current value on 'subscibe' is called.
    private static final boolean NOTIFY_CURRENT_VALUE_ON_SUBSCRIBE = true;
    private static final long WAIT_HOST_TRIGGER_ON_SUBSCRIBE = 150L;

    private final Collection<WebSocket> clients;
    private final PushModel pushModel;
    private final ControllerHost host;
    private final List<BankedEvent> bankedEvents;
    private final BankedEvent bankedEvent;
    
    /**
     * 
     */
    private class BankedEvent {
        private boolean hostTriggered;
        // the last reported value from host
        private Object lastReportedParams;
        private final Object[] bankIndexes;
        /**
         * 
         * @param bankIndexes 
         */
        private BankedEvent(int[] bankIndexes) {
            this.bankIndexes = ArrayUtils.addAll(new Object[] {}, ArrayUtils.toObject(bankIndexes));
            registerObserver();
        }
        
        
        @SuppressWarnings({"UseSpecificCatch", "unchecked"})
        private void registerObserver() {
            try {
                Value value = getNodeInstance(bankIndexes);
                ValueChangedCallback callback =
                    BitwigCallbacks.newValueChangedCallback(value, this::onValueChanged);
                value.unsubscribe();
                // for debug
                if (Logger.isDebugEnabled() && ObjectValueChangedCallback.class.equals(callback.getClass())) {
                    LOG.debug(event() + " event usess ObjectValueChangedCallback.");
                }
                // addValueObserver raise callback calls even after call unsubscribe()
                value.addValueObserver(callback);
            } catch (Exception ex) {
                setError(ex, "Fialed registering observer.");
                LOG.error(event() + " event: Fialed registering observer.", ex);
            }
        }
        
        /**
         * post event to all subscribe[rs.
         * @param params
         */
        private void onValueChanged(Object params) {
            hostTriggered = true;
            lastReportedParams = params;
            if (!clients.isEmpty() && pushModel != null) {
                pushModel.push(newNotification(params), clients);
            }
        }

        private void internalSubscribe(WebSocket client) {
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
                LOG.debug(event() + " event has been subscribed by " + client(client));
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
                Value value = getNodeInstance(bankIndexes);
                if (clients.isEmpty() && value.isSubscribed()) {
                    value.unsubscribe();
                    return false;
                }
                if (!clients.isEmpty() && !value.isSubscribed()) {
                    value.subscribe();
                    return true;
                }
            } catch (Exception ex) {
                setError(ex, "Fialed syncing subscribed state.");
                LOG.error(event() + " event: Fialed syncing subscribed state.", ex);
                throw new RpcException(ex);
            }
            return false;
        }
        
        /**
         * post current value to client.
         */
        private void notifyCurrentValueIfHostNotTriggered(WebSocket client) {
            if (NOTIFY_CURRENT_VALUE_ON_SUBSCRIBE && pushModel != null) {
                hostTriggered = false;
                host.scheduleTask(() -> {
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
                        .addNotification(newNotification(lastReportedParams));
                }
            }
        }

        /**
         * post event to specified client.
         */
        private void post(Object params, WebSocket client) {
            if (clients.contains(client) && pushModel!= null) {
                pushModel.push(newNotification(params), client);
            }
        }
        
        /**
         * Create a new notification message.
         * @param params
         */
        private Notification newNotification(Object params) {
            if (bankIndexes.length > 0) {
                // to Object array
                Object[] array = params != null && params.getClass().isArray()
                    ? (Object[])params
                    : new Object[] {params};
                params = ArrayUtils.addAll((Object[])bankIndexes, array);
            }
            return new Notification(absoluteName, params);
        }
        
        private String event() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(absoluteName);
            if (bankIndexes.length > 0) {
                sb.append(" bank:");
                sb.append(ArrayUtils.toString(bankIndexes));
            }
            sb.append("]");
            return sb.toString();
        }
    }
    
    /**
     * 
     * @param method
     * @param nodeType
     * @param parantNode
     * @param bankItemCount
     * @param pushModel
     * @param host 
     */
    EventHolder(Method method, Class<T> nodeType, RegistryNode<?> parantNode, int bankItemCount, ControllerHost host, PushModel pushModel) {
        super(method, nodeType, parantNode, bankItemCount);
        // clients = new LinkedList<>();
        this.host = host;
        this.pushModel = pushModel;
        this.clients = new ArrayList<>();
        if (bankIndexCombinations != null) {
            this.bankedEvents = bankIndexCombinations.stream()
                .map(combination -> new BankedEvent(combination))
                .collect(Collectors.toList());
            this.bankedEvent = null;
        } else {
            this.bankedEvents = null;
            this.bankedEvent = new BankedEvent(new int[]{});
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
            if (bankedEvent != null) {
                bankedEvent.internalSubscribe(client);
            } else {
                bankedEvents.stream().forEach(e -> e.internalSubscribe(client));
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
    public void unsubscribe(WebSocket client) {
        if (getError() != null) {
            throw new RpcException(getError());
        }
        if (clients.remove(client)) {
            syncSubscribedState();
            if (Logger.isTraceEnabled())  {
                LOG.trace("[" + absoluteName + "] event has been unsubscribed by " + client(client));
            }
        }
    }
    

    void disconnect(WebSocket client) {
        boolean removed = clients.remove(client);
        if (removed) {
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
        clients.clear();
    }

    /**
     * create a report object for this class.
     * @param eh
     * @return
     */
    Object reportRpcEvent() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("event", absoluteName);
        if (bankDimension.length > 0) {
            report.put("bankDimension", bankDimension);
        }
        if (getError() != null) {
            report.put("error", getError());
        }
        return report;
    }

    /**
     * Sync state of event subscription between RPC and Bitwig Studio.
     * @return host side state will change to subscribed
     */
    private void syncSubscribedState() {
        if (bankedEvent != null) {
            bankedEvent.syncSubscribedState();
        } else {
            bankedEvents.stream().forEach(e -> e.syncSubscribedState());
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
        sb.append("]\n number of subscriber(s):");
        sb.append(clients.size());
        sb.append(']');
        return sb.toString();
    }
}
