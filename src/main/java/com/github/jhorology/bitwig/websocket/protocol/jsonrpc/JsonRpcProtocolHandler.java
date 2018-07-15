package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.reflect.MethodHolder;
import com.github.jhorology.bitwig.websocket.CloseEvent;
import com.github.jhorology.bitwig.websocket.OpenEvent;
import com.github.jhorology.bitwig.websocket.StartEvent;
import com.github.jhorology.bitwig.websocket.StopEvent;
import com.github.jhorology.bitwig.websocket.TextMessageEvent;
import com.github.jhorology.bitwig.websocket.protocol.AbstractProtocolHandler;

public class JsonRpcProtocolHandler extends AbstractProtocolHandler {
    public static final String JSONRPC_VERSION = "2.0";
        
    private Logger log;

    private Gson gson;
    
    @Subscribe
    public void onStart(StartEvent e) {
        log = Logger.getLogger(this.getClass());
        GsonBuilder gsonBuilder = new GsonBuilder()
            .serializeNulls()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(BatchOrSingleRequest.class, new BatchOrSingleRequestAdapter())
            .registerTypeAdapter(Request.class, new RequestAdapter(e.getMethodRegistry()))
            .registerTypeAdapter(Response.class, new ResponseAdapter());
        gson = gsonBuilder.create();
    }
    
    @Subscribe
    public void onStop(StopEvent e) {
        gson = null;
        log = null;
    }
    
    @Subscribe
    public void onOpen(OpenEvent e) {
        if (log.isTraceEnabled()) {
            WebSocket conn = e.getConnection();
            log.trace("new connection. remoteAddress:" + conn.getRemoteSocketAddress() +
                      "\nresourceDescriptor:" + e.getHandshake().getResourceDescriptor());
        }
    }
    
    @Subscribe
    public void onClose(CloseEvent e) {
        if (log.isTraceEnabled()) {
            WebSocket conn = e.getConnection();
            log.trace("connection closed. remoteAddress:" + conn.getRemoteSocketAddress() +
                      "\ncode:" + e.getCode() +
                      "\nreason:" + e.getReason() +
                      "\nremote:" + e.isRemote());
        }
    }
    
    @Subscribe
    public void onMessage(TextMessageEvent e) {
        WebSocket conn = e.getConnection();
        String message = e.getMessage();
        if (log.isTraceEnabled()) {
            log.trace("onMessage\n" + message);
        }
        BatchOrSingleRequest req;
        try {
            req = gson.fromJson(message, BatchOrSingleRequest.class);
        } catch (JsonSyntaxException ex) {
            sendError(conn, ErrorEnum.PARSE_ERROR, ex.getMessage(), null);
            return;
        } catch (Exception ex) {
            sendError(conn, ErrorEnum.INTERNAL_ERROR, ex.getMessage(), null);
            return;
        }
        String response;
        if (req.isBatch()) {
            if (req.getBatch().isEmpty()) {
                sendError(conn, ErrorEnum.INVALID_REQUEST, "batch call with an empty Array.", null);
                return;
            }
            if (req.getBatch().contains(null)) {
                sendError(conn, ErrorEnum.INVALID_REQUEST, "batch contains null object.", null);
                return;
            }
            response = onBatchRequest(req.getBatch());
        } else {
            if (req.getRequest() == null) {
                sendError(conn, ErrorEnum.INVALID_REQUEST, "request call with null objects.", null);
                return;
            }
            response = onSingleRequest(req.getRequest());
        }
        if (response != null) {
            conn.send(response);
        }
    }

    private String onBatchRequest(List<Request> batch) {
        List<Response> list = new ArrayList<>();
        for(Request req : batch) {
            Response res = processRequest(req);
            if (res != null) {
                list.add(res);
            }
        }
        if (!list.isEmpty()) {
            Response[] batchRes = list.toArray(new Response[list.size()]);
            return gson.toJson(batchRes);
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
        if (log.isTraceEnabled()) {
            log.trace("processRequest - request:\n" + gson.toJson(req));
        }
        if (req.hasError()) {
            return new Response(req.getError(), req.getId());
        }
        MethodHolder method = req.getMethodHolder();
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
        conn.send(gson.toJson(res));
    }
}
