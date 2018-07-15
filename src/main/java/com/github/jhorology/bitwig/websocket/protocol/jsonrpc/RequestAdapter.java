package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import com.github.jhorology.bitwig.reflect.MethodHolder;
import com.github.jhorology.bitwig.reflect.MethodRegistry;

public class RequestAdapter implements JsonDeserializer<Request> {
    private final MethodRegistry methodRegistry;
    
    public RequestAdapter(MethodRegistry methodRegistry) {
        this.methodRegistry = methodRegistry;
    }
    
    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Request req = new Request();
        try {
            if(!json.isJsonObject())
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "request should be JSON object.");
            JsonObject obj = json.getAsJsonObject();
            
            // "jsonrpc": "2.0"
            JsonPrimitive jsonrpc = obj.getAsJsonPrimitive("jsonrpc");
            if (jsonrpc == null)
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'jsonrpc' property does not exist.");
            if (!jsonrpc.isString())
                throw  new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'jsonrpc' property should be string.");
            req.setJsonrpc(jsonrpc.getAsString());
            if (!JsonRpcProtocolHandler.JSONRPC_VERSION.equals(req.getJsonrpc()))
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "unsupported version of 'jsonrpc' property.");

            // "method": "sum"
            JsonPrimitive method = obj.getAsJsonPrimitive("method");
            if (method == null)
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'method' property does not exist.");
            if (!method.isString())
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'method' property should be string.");
            req.setMethod(method.getAsString());
            
            JsonElement params = obj.get("params");
            int paramCount = 0;
            if (params != null) {
                if (params.isJsonArray()) {
                    // "params": [1, 2]
                    paramCount = params.getAsJsonArray().size();
                    if (paramCount == 0)
                        throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "'params' property is empty array.");
                } else if (params.isJsonObject()) {
                    // "params": {"arg1":1, "arg2": 2]
                    paramCount = 1;
                    if (params.getAsJsonObject().entrySet().isEmpty())
                        throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "'params' property is empty object.");
                } else
                    throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "unsupported type of 'params' property.");
            }
            JsonPrimitive id = obj.getAsJsonPrimitive("id");
            req.setNotify(false);
            if (id == null) {
                req.setNotify(true);
            } else {
                if (id.isString()) {
                    // "id": "abc"
                    req.setId(id.getAsString());
                } if (id.isNumber()) {
                    // "id": 1
                    req.setId(id.getAsInt());
                }
            }
            
            MethodHolder methodHolder = methodRegistry.getMethod(req.getMethod(), paramCount);
            if (methodHolder == null)
                throw new JsonRpcException(ErrorEnum.METHOD_NOT_FOUND, "'" + req.getMethod() + "' method not found.");
            req.setMethodHolder(methodHolder);
            
            if (params != null) {
                Type[] paramTypes = methodHolder.getParamTypes();
                if (params.isJsonArray()) {
                    Object[] args = new Object[paramCount];
                    for(int i = 0; i < paramCount; i++) {
                        args[i] = context.deserialize(params.getAsJsonArray().get(i), paramTypes[i]);
                    }
                    req.setParams(args);
                } else {
                    req.setParams(context.deserialize(params, paramTypes[0]));
                }
            }
        } catch (JsonParseException ex) {
            req.setError(new Error(ErrorEnum.PARSE_ERROR, ex.getMessage()));
        } catch (JsonRpcException ex) {
            req.setError(ex.getError());
        }
        return req;
    }
}


