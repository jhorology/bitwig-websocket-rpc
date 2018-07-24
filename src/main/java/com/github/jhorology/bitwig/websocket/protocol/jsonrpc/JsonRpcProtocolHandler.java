package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.util.List;
import java.util.stream.Collectors;
import java.nio.ByteBuffer;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.websocket.protocol.AbstractProtocolHandler;
import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;

public class JsonRpcProtocolHandler extends AbstractProtocolHandler implements PushModel {
    public static final String JSONRPC_VERSION = "2.0";
        
    private Logger log;
    private Gson gson;
    
    @Override
    public void onStart() {
        log = Logger.getLogger(this.getClass());
        GsonBuilder gsonBuilder = BitwigAdapters.newGsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(BatchOrSingleRequest.class, new BatchOrSingleRequestAdapter())
            .registerTypeAdapter(Request.class, new RequestAdapter(registry))
            .registerTypeAdapter(Response.class, new ResponseAdapter())
            .registerTypeAdapter(Notification.class, new NotificationAdapter());
        gson = gsonBuilder.create();
    }
    
    @Override
    public void onStop() {
        gson = null;
        log = null;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
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
                sendError(conn, ErrorEnum.INVALID_REQUEST, "batch call with an empty array.", null);
                return;
            }
            if (req.getBatch().contains(null)) {
                sendError(conn, ErrorEnum.INVALID_REQUEST, "batch contains null.", null);
                return;
            }
            response = onBatchRequest(req.getBatch());
        } else {
            if (req.getRequest() == null) {
                sendError(conn, ErrorEnum.INVALID_REQUEST, "request call with null.", null);
                return;
            }
            response = onSingleRequest(req.getRequest());
        }
        if (response != null) {
            send(conn, response);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
    }
    
    @Override
    public void push(Notification notification, Collection<WebSocket> clients) {
        push(gson.toJson(notification), clients);
    }
    
    @Override
    public void broadcast(Notification notification) {
        broadcast(gson.toJson(notification));
    }
    
    private String onBatchRequest(List<Request> batch) {
        List<Response> results = batch.stream()
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
            log.error("rpc method invoking error.", ex);
            return createErrorResponse(ErrorEnum.INTERNAL_ERROR, ex.getMessage(), req.getId());
        }
        if (!req.isNotify()) {
            return new Response(result, req.getId());
        }
        return null;
    }

    private Response createErrorResponse(ErrorEnum error, Object data, Object id) {
        Response res = new Response(new Error(error, data), id);
        return res;
    }
    
    private void sendError(WebSocket conn, ErrorEnum error, Object data, Object id) {
        Response res = createErrorResponse(error, data, id);
        send(conn, gson.toJson(res));
    }

    private void send(WebSocket conn, String message) {
        conn.send(message);
        if (Logger.isTraceEnabled()) {
            log.trace("message sended to " + conn.getRemoteSocketAddress() +
                      "\n <-- " + message);
        }
    }
    
    private void push(String message, Collection<WebSocket> clients) {
        server.broadcast(message, clients);
        if (Logger.isTraceEnabled()) {
            log.trace("broadcast message to " + clients.size() + " clients." +
                      "\n <-- " + message);
        }
    }
    
    private void broadcast( String message) {
        server.broadcast(message);
        if (Logger.isTraceEnabled()) {
            log.trace("broadcast message to all " + server.getConnections().size() + " clients." +
                      "\n <-- " + message);
        }
    }
}
