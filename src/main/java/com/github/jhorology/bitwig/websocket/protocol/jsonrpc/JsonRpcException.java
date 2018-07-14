package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

public class JsonRpcException extends RuntimeException {
    private final Error error;
    
    public JsonRpcException(ErrorEnum error) {
        super(error.getMessage());
        this.error = new Error(error);
    }
    
    public JsonRpcException(ErrorEnum error, Object data) {
        super(error.getMessage());
        this.error = new Error(error, data);
    }
    
    public Error getError() {
        return error;
    }
}
