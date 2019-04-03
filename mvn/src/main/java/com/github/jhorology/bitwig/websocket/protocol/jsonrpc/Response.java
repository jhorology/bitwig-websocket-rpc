package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

/**
 *  A class to use for JSON srialization of response message.
 *  @see https://www.jsonrpc.org/specification
 */
public class Response {
    private String jsonrpc;
    private Object result;
    private Error error;
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

    public String getJsonrpc() {
        return jsonrpc;
    }

    public Object getResult() {
        return result;
    }
    
    public void setResult(Object result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }
    
    public void setError(Error error) {
        this.error = error;
    }

    public Object getId() {
        return id;
    }
    
    public void setId(Object id) {
        this.id = id;
    }
}
