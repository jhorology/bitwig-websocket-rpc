package com.github.jhorology.bitwig.reflect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import com.google.common.eventbus.EventBus;
import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.websocket.protocol.AbstractProtocolHandler;
import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.NotificationEvent;

public class EventHolder {
    private final AbstractProtocolHandler protocol;
    private final Collection<WebSocket> clients;
    private final Collection<WebSocket> triggerOnce;
    private final EventBus eventBus;

    public EventHolder(AbstractProtocolHandler protocol) {
        this.protocol = protocol;
        clients = new LinkedList<>();
        triggerOnce = new ArrayList<>();
        eventBus = new EventBus();
        eventBus.register(protocol);
    }

    public void subscribe(WebSocket client) {
        if (!clients.contains(client)) {
            clients.add(client);
        }
    }
    
    public void subscribeOnce(WebSocket client) {
        subscribe(client);
        if (!triggerOnce.contains(client)) {
            triggerOnce.add(client);
        }
    }

    public void unsubscribe(WebSocket client) {
        clients.remove(client);
        triggerOnce.remove(client);
    }
    
    private void post(Notification notification) {
        // trigger synchronous event
        eventBus.post(new NotificationEvent(notification, clients));
        triggerOnce.stream().forEach(e -> clients.remove(e));
        triggerOnce.clear();
    }
    
    public void clear() {
        eventBus.unregister(protocol);
        clients.clear();
        triggerOnce.clear();
    }
}
