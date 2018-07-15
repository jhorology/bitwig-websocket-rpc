package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import com.google.gson.annotations.Expose;

/**
 *  https://www.jsonrpc.org/specification
 */
public class Response {
    @Expose
    private String jsonrpc;
    @Expose
    private Object result;
    @Expose
    private Error error;
    @Expose
    private Object id;

    public Response() {
        this.jsonrpc = JsonRpcProtocolHandler.JSONRPC_VERSION;
    }
    
    public Response(Object result, Object id) {
        this();
        this.result = result;
        this.id = id;
    }
    
    public Response(Error error, Object id) {
        this();
        this.error = error;
        this.id = id;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
