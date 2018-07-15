package com.github.jhorology.bitwig.websocket.protocol;
import com.github.jhorology.bitwig.websocket.protocol.jsonrpc.JsonRpcProtocolHandler;

public interface Protocols {
    public static AbstractProtocolHandler newJsonRpc20() {
        return new JsonRpcProtocolHandler();
    }
}
