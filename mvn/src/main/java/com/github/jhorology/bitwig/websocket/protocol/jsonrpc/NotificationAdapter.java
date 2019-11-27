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

import com.github.jhorology.bitwig.websocket.protocol.Notification;
import com.google.gson.JsonArray;
import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A GSON type adapter for {@link Notification}.
 */
public class NotificationAdapter implements JsonSerializer<Notification> {
    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(Notification src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("notification", src.getNotifictaion());
        Object[] srcParams = src.getParams();
        if (srcParams != null && srcParams.length > 0) {
            JsonElement params = context.serialize(srcParams);
            // flatten params
            // params:[{a:1, b:2}] -> params:{a:1, b:2}
            if (params.isJsonArray()) {
                JsonArray arrayParams = params.getAsJsonArray();
                if (arrayParams.size() == 1) {
                    JsonElement singleParam = arrayParams.get(0);
                    if (singleParam.isJsonObject()) {
                        params = singleParam;
                    }
                }
            }
            json.add("params", params);
        }
        return json;
    }
}


