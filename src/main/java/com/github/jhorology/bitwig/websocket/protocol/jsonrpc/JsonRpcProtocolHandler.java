package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.java_websocket.WebSocket;

import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.reflect.MethodInvoker;
import com.github.jhorology.bitwig.websocket.StringMessageEvent;
import com.github.jhorology.bitwig.websocket.protocol.AbstractProtocolHandler;

public class JsonRpcProtocolHandler extends AbstractProtocolHandler {
    public static final String JSONRPC_VERSION = "2.0";
        
    private static final Logger log = Logger.getLogger(JsonRpcProtocolHandler.class);
    private static JsonRpcProtocolHandler instance = new JsonRpcProtocolHandler();

    private final Gson gson;
    
    private JsonRpcProtocolHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(BatchOrSingleRequest.class, new BatchOrSingleRequestDeserializer())
            .registerTypeAdapter(Request.class, new RequestAdapter());
        gson = gsonBuilder.create();
    }

    public static JsonRpcProtocolHandler getInstance() {
        return instance;
    }

    @Subscribe
    public void onMessage(StringMessageEvent e) {
        WebSocket conn = e.getConnection();
        String message = e.getMessage();
        if (log.isTraceEnabled()) {
            log.trace("onMessage\n" + message);
        }
        BatchOrSingleRequest req = gson.fromJson(message, BatchOrSingleRequest.class);
        String response = req.isBatch() ?
            onBatchRequest(req.getBatch()) :
            onSingleRequest(req.getRequest());
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
        MethodInvoker method = req.getMethodInvoker();
        Object result;
        try {
            result = method.invoke(req.getArgs());
        } catch (Exception ex) {
            log.error("rpc method invoking error.", ex);
            return new Response(new Error(ErrorEnum.INTERNAL_ERROR, ex.getMessage()), req.getId());
        }
        if (!req.isNotify()) {
            return new Response(result, req.getId());
        }
        return null;
    }
}
