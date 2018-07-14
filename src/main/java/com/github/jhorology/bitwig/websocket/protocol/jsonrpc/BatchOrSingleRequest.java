package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;
import java.util.List;

/**
 *  https://www.jsonrpc.org/specification
 */
public class BatchOrSingleRequest {
    private Request request;
    private List<Request> batch;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<Request> getBatch() {
        return batch;
    }
    
    public void setBatch(List<Request> batch) {
        this.batch = batch;
    }
    
    public boolean isBatch() {
        return batch != null;
    }
}
