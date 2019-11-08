/*
 * Copyright (c) 2018 Masafumi Fujimaru
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY>, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

// jdk
import java.lang.reflect.Type;
import java.util.stream.StreamSupport;

// dependencies
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

// source
import com.github.jhorology.bitwig.reflect.ReflectUtils;
import com.github.jhorology.bitwig.rpc.RpcMethod;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.rpc.RpcRegistry;

/**
 * A GSON type adapter for {@link Request}.
 */
public class RequestAdapter implements JsonDeserializer<Request> {
    private final RpcRegistry registry;

    /**
     * Constructs this instance with RPC registry.
     * @param registry 
     */
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

            JsonElement params = request.get("params");
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
            
            // TODO rpc-websockets client send null as empty params.
            if (params == null || params.isJsonNull()) {
                req.setParams(new Object[0]);
            } else {
                Type[] paramTypes = rpcMethod.getParamTypes();
                boolean isVarargs = ReflectUtils.isVarargs(paramTypes);
                boolean isArrayParams = params.isJsonArray();
                
                // matching params:
                //    ['a','b','c'] or args = ['a', 'b', 'c']; [args]
                // to method arguments:
                //   foobar(String... args) or foobar(String[] args)
                //
                if (isArrayParams) {
                    JsonArray arrayParam = params.getAsJsonArray();
                    if (arrayParam.size() == 1 && arrayParam.get(0).isJsonArray()) {
                        params = arrayParam.get(0);
                    } 
                }
                if (params.isJsonArray() && !isVarargs) {
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
        if (json == null || json.isJsonNull()) {
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
            return new RpcParamType[] {RpcParamType.OBJECT};
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


