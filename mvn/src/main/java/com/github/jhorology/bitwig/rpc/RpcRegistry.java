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
package com.github.jhorology.bitwig.rpc;

import org.java_websocket.WebSocket;

/**
 * An interface for RPC registry model.
 */
public interface RpcRegistry {
  /**
   * Get an interface for RPC method model.
   * @param name the method name.
   * @param paramTypes the parameter types
   * @return
   */
  RpcMethod getRpcMethod(String name, RpcParamType[] paramTypes);

  /**
   * Get an interface for RPC event model.
   * @param name the event name.
   * @return
   */
  RpcEvent getRpcEvent(String name);

  /**
   * Clean up a client that has been disconnected.
   * @param client remote connection.
   */
  void disconnect(WebSocket client);

  /**
   * Reports all registered methods and evnets.
   * @return
   */
  Object report();

  /**
   * report all RPC events of current configuration.
   * @return
   */
  Object reportEvents();
}
