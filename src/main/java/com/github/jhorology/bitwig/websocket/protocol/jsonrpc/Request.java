package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Method;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import com.github.jhorology.bitwig.reflect.MethodHolder;

/**
 *  https://www.jsonrpc.org/specification
 */
public class Request {
    @Expose
    protected String jsonrpc;
    @Expose
    private String method;
    @Expose
    private Object params;
    @Expose
    protected Object id;
    
    private boolean notify;
    private Error error;
    private MethodHolder methodHolder;
    private Method rpcMethod;
        
    public Request() {
    }
    
    public Request(Error error) {
        this();
        this.error = error;
    }
    
    public String getJsonrpc() {
        return jsonrpc;
    }
    
    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }
    
    public Object getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Object[] getArgs() {
        if (params != null) {
            if (params instanceof Object[]) {
                return (Object[])params;
            }
            return new Object[] {params};
        }
        return null;
    }
    
    public Error getError() {
        return  error;
    }
    
    public void setError(Error error) {
        this.error = error;
    }

    public MethodHolder getMethodHolder() {
        return methodHolder;
    }

    public void setMethodHolder(MethodHolder methodHolder) {
        this.methodHolder = methodHolder;
    }

    public boolean hasError() {
        return error != null;
    }
    
    public boolean isNotify() {
        return notify;
    }
    
    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
