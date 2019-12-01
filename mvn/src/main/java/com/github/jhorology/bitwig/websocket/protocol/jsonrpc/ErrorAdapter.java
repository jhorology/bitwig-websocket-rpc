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

// dependencies
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A GSON type adapter for {@link Error}.
 * @see https://www.jsonrpc.org/specification#error_object
 */
public class ErrorAdapter implements JsonSerializer<Error> {
    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(Error src, Type typeOfSrc, JsonSerializationContext context) {
        // code
        //   A Number that indicates the error type that occurred.
        //   This MUST be an integer.
        // message
        //   A String providing a short description of the error.
        //   The message SHOULD be limited to a concise single sentence.
        // data
        //   A Primitive or Structured value that contains additional information about the error.
        //   This may be omitted.
        //   The value of this member is defined by the Server (e.g. detailed error information, nested errors etc.).
        JsonObject json = new JsonObject();
        json.addProperty("code", src.getCode());
        json.addProperty("message", src.getMessage());
        if (src.getData() != null) {
            json.add("data", context.serialize(src.getData()));
        }
        return json;
    }
}


