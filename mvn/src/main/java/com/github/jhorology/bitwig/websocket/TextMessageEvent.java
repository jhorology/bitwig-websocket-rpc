package com.github.jhorology.bitwig.websocket;

import org.java_websocket.WebSocket;

public class TextMessageEvent extends AbstractWebSocketEvent {

  private final String message;

  TextMessageEvent(WebSocket conn, String message) {
    super(conn);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
