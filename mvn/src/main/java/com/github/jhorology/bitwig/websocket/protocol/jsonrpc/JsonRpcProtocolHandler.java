/*
 * Copyright (c) 2020 Masafumi Fujimaru
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
package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.logging.LoggerFactory;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.websocket.protocol.AbstractProtocolHandler;
import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.slf4j.Logger;

public class JsonRpcProtocolHandler
  extends AbstractProtocolHandler
  implements PushModel {

  public static final String JSONRPC_VERSION = "2.0";
  private static final Logger LOG = LoggerFactory.getLogger(
    JsonRpcProtocolHandler.class
  );

  private Gson gson;

  /**
   * {@inheritDoc}
   */
  @Override
  public void onStart() {
    gson =
      BitwigAdapters
        .adapt(new GsonBuilder())
        .serializeNulls()
        .addSerializationExclusionStrategy(
          new ExcludeFieldsWithoutExposeAnnotationStrategy(true)
        )
        .addDeserializationExclusionStrategy(
          new ExcludeFieldsWithoutExposeAnnotationStrategy(false)
        )
        // .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapter(
          BatchOrSingleRequest.class,
          new BatchOrSingleRequestAdapter()
        )
        .registerTypeAdapter(Request.class, new RequestAdapter(registry))
        .registerTypeAdapter(Response.class, new ResponseAdapter())
        .registerTypeAdapter(Error.class, new ErrorAdapter())
        .registerTypeAdapter(Notification.class, new NotificationAdapter())
        .registerTypeAdapter(Config.class, new ConfigAdapter())
        .create();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onStop() {
    gson = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onOpen(WebSocket conn, ClientHandshake handshake) {
    push(new Notification("_accepted"), conn);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onClose(
    WebSocket conn,
    int code,
    String reason,
    boolean remote
  ) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void onMessage(WebSocket conn, String message) {
    if (gson == null) {
      return;
    }

    BatchOrSingleRequest req;
    try {
      req = gson.fromJson(message, BatchOrSingleRequest.class);
    } catch (JsonSyntaxException ex) {
      sendError(conn, ErrorEnum.PARSE_ERROR, ex.getMessage(), null);
      return;
    } catch (Throwable ex) {
      sendError(conn, ErrorEnum.INTERNAL_ERROR, ex.getMessage(), null);
      return;
    }
    String response;
    if (req.isBatch()) {
      if (req.getBatch().isEmpty()) {
        sendError(
          conn,
          ErrorEnum.INVALID_REQUEST,
          "batch call with an empty array.",
          null
        );
        return;
      }
      if (req.getBatch().contains(null)) {
        sendError(
          conn,
          ErrorEnum.INVALID_REQUEST,
          "batch contains null.",
          null
        );
        return;
      }
      response = onBatchRequest(req.getBatch());
    } else {
      if (req.getRequest() == null) {
        sendError(
          conn,
          ErrorEnum.INVALID_REQUEST,
          "request call with null.",
          null
        );
        return;
      }
      response = onSingleRequest(req.getRequest());
    }
    if (response != null) {
      send(response, conn);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onMessage(WebSocket conn, ByteBuffer message) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void onError(WebSocket conn, Exception ex) {}

  /**
   * {@inheritDoc}
   */
  @Override
  public void push(Notification notification, WebSocket client) {
    if (gson != null) {
      send(gson.toJson(notification), client);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void push(Notification notification, Collection<WebSocket> clients) {
    if (gson != null) {
      push(gson.toJson(notification), clients);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void broadcast(Notification notification) {
    if (gson != null) {
      broadcast(gson.toJson(notification));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSerializableBitwigType(Class<?> bitwigType) {
    return bitwigType.isEnum() || BitwigAdapters.isAdaptedType(bitwigType);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PushModel getPushModel() {
    return this;
  }

  private String onBatchRequest(List<Request> batch) {
    List<Response> results = batch
      .stream()
      .map(req -> processRequest(req))
      .filter(res -> res != null)
      .collect(Collectors.toList());
    if (!results.isEmpty()) {
      return gson.toJson(results);
    }
    return null;
  }

  private String onSingleRequest(Request req) {
    Response res = processRequest(req);
    if (res != null) {
      return gson.toJson(res);
    }
    return null;
  }

  private Response processRequest(Request req) {
    if (req.hasError()) {
      return new Response(req.getError(), req.getId());
    }
    RpcMethod method = req.getRpcMethod();
    Object result;
    try {
      result = method.invoke(req.getArgs());
    } catch (Exception ex) {
      LOG.error("Error RPC method invoking.", ex);
      return createErrorResponse(
        ErrorEnum.INTERNAL_ERROR,
        ex.getMessage(),
        req.getId()
      );
    }
    if (!req.isNotify()) {
      return new Response(result, req.getId());
    }
    return null;
  }

  private Response createErrorResponse(
    ErrorEnum error,
    Object data,
    Object id
  ) {
    Response res = new Response(new Error(error, data), id);
    return res;
  }

  private void sendError(
    WebSocket conn,
    ErrorEnum error,
    Object data,
    Object id
  ) {
    Response res = createErrorResponse(error, data, id);
    send(gson.toJson(res), conn);
  }
}
