package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import com.github.jhorology.bitwig.reflect.MethodInvoker;
import com.github.jhorology.bitwig.reflect.MethodRegistry;

public class RequestAdapter implements JsonDeserializer<Request>, JsonSerializer<Request> {
    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Request req = new Request();
        try {
            if(!json.isJsonObject())
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "message is not JSON object.");
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
            int parameterCount = 0;
            if (params == null) {
            } else if (params.isJsonArray()) {
                // "params": [1, 2]
                parameterCount = params.getAsJsonArray().size();
                if (parameterCount == 0)
                    throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "'params' property is empty array.");
            } else if (params.isJsonObject()) {
                // "params": {"arg1":1, "arg2": 2]
                parameterCount = 1;
                if (params.getAsJsonObject().entrySet().isEmpty())
                    throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "'params' property is empty object.");
            } else
                throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "unsupported type of 'params' property.");
            
            JsonPrimitive id = obj.getAsJsonPrimitive("id");
            req.setNotify(false);
            if (id == null) {
                req.setNotify(true);
            } if (id.isString()) {
                // "id": "abc"
                req.setId(id.getAsString());
            } if (id.isNumber()) {
                // "id": 1
                req.setId(id.getAsInt());
            }
            
            MethodInvoker invoker = MethodRegistry.getInstance()
                .getMethodInvoker(req.getMethod(), parameterCount);
            if (invoker == null)
                throw new JsonRpcException(ErrorEnum.METHOD_NOT_FOUND, "'" + req.getMethod() + "' method not found.");
            req.setMethodInvoker(invoker);
            
            if (params != null) {
                Class<?> [] parameterTypes = invoker.getParameterTypes();
                if (params.isJsonArray()) {
                    Object[] args = new Object[parameterCount];
                    for(int i = 0; i < parameterCount; i++) {
                        args[i] = context.deserialize(params.getAsJsonArray().get(i), parameterTypes[i]);
                    }
                    req.setParams(args);
                } else {
                    req.setParams(context.deserialize(params, parameterTypes[0]));
                }
            }
        } catch (JsonParseException ex) {
            req.setError(new Error(ErrorEnum.PARSE_ERROR, ex.getMessage()));
        } catch (JsonRpcException ex) {
            req.setError(ex.getError());
        }
        return req;
    }

    @Override
    public JsonElement serialize(Request src, Type typeOfSrc, JsonSerializationContext context) {
        // a temporary step
        JsonElement json = context.serialize(src);
        return json;
    }
}


