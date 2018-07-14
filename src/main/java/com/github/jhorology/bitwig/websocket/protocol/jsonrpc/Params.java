package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import com.google.gson.annotations.Expose;

/**
 *  https://www.jsonrpc.org/specification
 */
public class Params {
    private Object[] args;
    
    public Params() {
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
    
    public Object[] getArgs() {
        return args;
    }
}
