package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Method;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;

import com.github.jhorology.bitwig.reflect.MethodInvoker;

/**
 *  https://www.jsonrpc.org/specification
 */
public class Request extends JsonRpcMessage {
    @Expose
    private String method;
    @Expose
    private Object params;
    
    private boolean notify;
    private Error error;
    private MethodInvoker methodInvoker;
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

    public MethodInvoker getMethodInvoker() {
        return methodInvoker;
    }

    public void setMethodInvoker(MethodInvoker methodInvoker) {
        this.methodInvoker = methodInvoker;
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
