package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import com.google.gson.annotations.Expose;

public abstract class JsonRpcMessage {
    @Expose
    protected String jsonrpc;

    @Expose
    protected Object id;

    private boolean hasStringId;
    private boolean hasIntegerId;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }
        
    public Object getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        hasStringId = false;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public boolean hasIntegerId() {
        return (id != null && id instanceof Integer);
    }
    
    public boolean hasStringId() {
        return (id != null && id instanceof String);
    }
    
    public boolean hasId() {
        return (id != null);
    }
}
