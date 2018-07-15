package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import com.google.gson.annotations.Expose;

/**
 *  https://www.jsonrpc.org/specification
 */
public class Error {
    @Expose
    private int code;
    @Expose
    private String message;
    @Expose
    private Object data;

    public Error(ErrorEnum error, Object data) {
        this.code = error.getCode();
        this.message = error.getMessage();
        this.data = data;
    }
    
    public Error(ErrorEnum error) {
        this(error, null);
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}
