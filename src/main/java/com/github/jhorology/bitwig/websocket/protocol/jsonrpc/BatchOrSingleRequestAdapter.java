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
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

// jdk
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// dependencies
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * A GSON type adapter for BatchOrSingleRequest.
 */
public class BatchOrSingleRequestAdapter implements JsonDeserializer<BatchOrSingleRequest> {
    /**
     * {@inheritDoc}
     */
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
