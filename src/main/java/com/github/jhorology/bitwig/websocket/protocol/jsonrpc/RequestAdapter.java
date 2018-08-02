package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Type;
import java.util.stream.StreamSupport;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import com.github.jhorology.bitwig.reflect.ReflectUtils;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.rpc.RpcRegistry;

public class RequestAdapter implements JsonDeserializer<Request> {
    private final RpcRegistry registry;

    public RequestAdapter(RpcRegistry registry) {
        this.registry = registry;
    }
    
    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Request req = new Request();
        try {
            if(!json.isJsonObject())
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "request should be JSON object.");
            
            JsonObject request = json.getAsJsonObject();

            // "jsonrpc": "2.0"
            JsonPrimitive jsonrpc = request.getAsJsonPrimitive("jsonrpc");
            if (jsonrpc == null)
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'jsonrpc' property does not exist.");
            if (!jsonrpc.isString())
                throw  new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'jsonrpc' property should be string.");
            req.setJsonrpc(jsonrpc.getAsString());
            if (!JsonRpcProtocolHandler.JSONRPC_VERSION.equals(req.getJsonrpc()))
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "unsupported version of 'jsonrpc' property.");

            // "method": "sum"
            JsonPrimitive method = request.getAsJsonPrimitive("method");
            if (method == null)
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'method' property does not exist.");
            if (!method.isString())
                throw new JsonRpcException(ErrorEnum.INVALID_REQUEST, "'method' property should be string.");
            req.setMethod(method.getAsString());

            final JsonElement params = request.get("params");
            RpcMethod rpcMethod = registry.getRpcMethod(req.getMethod(), toRpcParamTypes(params));
            
            JsonPrimitive id = request.getAsJsonPrimitive("id");
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

            if (rpcMethod == null) {
                throw new JsonRpcException(ErrorEnum.METHOD_NOT_FOUND, "'" + req.getMethod() + "' method not found.");
            }
            req.setRpcMethod(rpcMethod);
            
            if (params != null) {
                Type[] paramTypes = rpcMethod.getParamTypes();
                if (params.isJsonArray() && !ReflectUtils.isVarargs(paramTypes)) {
                    JsonArray ja = params.getAsJsonArray();
                    Object[] args = new Object[ja.size()];
                    for(int i = 0; i < ja.size(); i++) {
                        args[i] = context.deserialize(ja.get(i), paramTypes[i]);
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

    private RpcParamType[] toRpcParamTypes(final JsonElement json) {
        if (json == null) {
            return new RpcParamType[0];
        }
        // array paramters "params":[1,2,3]
        if (json.isJsonArray()) {
            if (json.getAsJsonArray().size() == 0)
                throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "'params' property is empty array.");
            return StreamSupport.stream(json.getAsJsonArray().spliterator(), false)
                .map(e -> paramItemTypeOf(e))
                .toArray(size -> new RpcParamType[size]);
        }
        // named paramters "params":{"left":1, "right":2}
        if(json.isJsonObject()) {
            if (json.getAsJsonObject().entrySet().isEmpty())
                throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "'params' property is empty object.");
            return  new RpcParamType[] {RpcParamType.OBJECT};
        }
        throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "unsupported type of 'params' property.");
    }

    private RpcParamType paramItemTypeOf(JsonElement json) {
        if (json.isJsonPrimitive()) {
            return primitiveParamTypeOf(json.getAsJsonPrimitive());
        }
        if (json.isJsonArray()) {
            final JsonArray ja = json.getAsJsonArray();
            if (ja.size() == 0) {
                return RpcParamType.OBJECT_ARRAY;
            }
            // exclude doble nested array
            StreamSupport.stream(ja.spliterator(), false).forEach(e -> {
                    if (e.isJsonArray())
                        throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "unsupported double nested array type of 'params' property.");
                });
            final RpcParamType firstParamType = ja.get(0).isJsonPrimitive()
                ? primitiveParamTypeOf(ja.get(0).getAsJsonPrimitive())
                : RpcParamType.OBJECT;
            if (ja.size() == 1) {
                return firstParamType.getArrayType();
            }
            boolean allMatch = StreamSupport.stream(ja.spliterator(), false)
                .skip(1)
                .map(e -> e.isJsonPrimitive() ? primitiveParamTypeOf(e.getAsJsonPrimitive()) : RpcParamType.OBJECT)
                .allMatch(e -> e == firstParamType);
            if (allMatch) {
                return firstParamType.getArrayType();
            }
            return RpcParamType.OBJECT_ARRAY;
        }
        return RpcParamType.OBJECT;
    }

    private RpcParamType primitiveParamTypeOf(JsonPrimitive json) {
        if (json.isBoolean()) {
            return RpcParamType.BOOLEAN;
        } else if (json.isNumber()) {
            return RpcParamType.NUMBER;
        } else if (json.isString()) {
            return RpcParamType.STRING;
        } else {
            throw new JsonRpcException(ErrorEnum.INVALID_PARAMS, "unsupported type of 'params' property.");
        }
    }
}


