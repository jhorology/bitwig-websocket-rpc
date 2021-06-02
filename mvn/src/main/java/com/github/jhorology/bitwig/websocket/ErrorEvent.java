package com.github.jhorology.bitwig.websocket;

import org.java_websocket.WebSocket;

public class ErrorEvent extends AbstractWebSocketEvent {

  private final Exception ex;

  ErrorEvent(WebSocket conn, Exception ex) {
    super(conn);
    this.ex = ex;
  }

  public Exception getException() {
    return ex;
  }
}
