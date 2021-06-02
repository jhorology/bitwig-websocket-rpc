package com.github.jhorology.bitwig.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class OpenEvent extends AbstractWebSocketEvent {

  private final ClientHandshake handshake;

  OpenEvent(WebSocket conn, ClientHandshake handshake) {
    super(conn);
    this.handshake = handshake;
  }

  public ClientHandshake getHandshake() {
    return handshake;
  }
}
