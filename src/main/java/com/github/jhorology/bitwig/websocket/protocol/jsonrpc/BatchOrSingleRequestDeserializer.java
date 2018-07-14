package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class BatchOrSingleRequestDeserializer implements JsonDeserializer<BatchOrSingleRequest> {
    @Override
    public BatchOrSingleRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        BatchOrSingleRequest req = new BatchOrSingleRequest();
        if(json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            List<Request> batch = new ArrayList<>();
            for(JsonElement elm : array) {
                batch.add(context.deserialize(elm, Request.class));
            }
            req.setBatch(batch);
        } else {
            req.setRequest(context.deserialize(json, Request.class));
        }
        return req;
    }
}


