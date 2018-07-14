package com.github.jhorology.bitwig.websocket.protocol;
import com.github.jhorology.bitwig.websocket.protocol.jsonrpc.JsonRpcProtocolHandler;

public interface Protocols {
    public AbstractProtocolHandler JSONRPC20 = JsonRpcProtocolHandler.getInstance();
}
