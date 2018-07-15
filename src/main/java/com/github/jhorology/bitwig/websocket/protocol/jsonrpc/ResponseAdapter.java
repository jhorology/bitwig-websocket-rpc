package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ResponseAdapter implements JsonSerializer<Response> {

    @Override
    public JsonElement serialize(Response src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("jsonrpc", src.getJsonrpc());
        if (src.getResult() != null) {
            json.add("result", context.serialize(src.getResult()));
        }
        if (src.getError() != null) {
            json.add("error", context.serialize(src.getError()));
        }
        json.add("id", context.serialize(src.getId()));
        return json;
    }
}


