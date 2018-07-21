package com.github.jhorology.bitwig.websocket.protocol;

import java.util.Collection;
import org.java_websocket.WebSocket;

public class NotificationEvent {
    private final Notification notification;
    private final Collection<WebSocket> clients;
    
    public NotificationEvent(Notification notification) {
        this.notification = notification;
        this.clients = null;
    }

    public NotificationEvent(Notification notification, Collection<WebSocket> clients) {
        this.notification = notification;
        this.clients = clients;
    }

    public Notification getNotification() {
        return notification;
    }

    public Collection<WebSocket> getClients() {
        return clients;
    }
}
