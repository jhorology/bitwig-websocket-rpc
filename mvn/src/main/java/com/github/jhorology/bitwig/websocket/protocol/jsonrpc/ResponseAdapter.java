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
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * A GSON type adapter for {@link Response}.
 * @see https://www.jsonrpc.org/specification#response_object
 */
public class ResponseAdapter implements JsonSerializer<Response> {
    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(Response src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.addProperty("jsonrpc", src.getJsonrpc());
        JsonElement result;
        // result
        //  This member is REQUIRED on success.
        //  This member MUST NOT exist if there was an error invoking the method.
        //  The value of this member is determined by the method invoked on the Server.
        if (src.getResult() == null && src.getError() == null) {
            json.add("result", JsonNull.INSTANCE);
        } else if (src.getResult() != null) {
            try {
                result = context.serialize(src.getResult());
                json.add("result", result);
            } catch (Exception ex) {
                // BitwigAdapters may thorws exceptions
                src.setResult(null);
                src.setError( new Error(ErrorEnum.INTERNAL_ERROR, ex.getMessage()));
            }
        }
        // error
        //   This member is REQUIRED on error.
        //   This member MUST NOT exist if there was no error triggered during invocation.
        //   The value for this member MUST be an Object as defined in section 5.1.
        if (src.getError() != null) {
            json.add("error", context.serialize(src.getError(), Error.class));
        }
        // id
        //   This member is REQUIRED.
        //   It MUST be the same as the value of the id member in the Request Object.
        //   If there was an error in detecting the id in the Request object (e.g. Parse error/Invalid Request), it MUST be Null.
        if (src.getId() != null) {
            json.add("id", context.serialize(src.getId()));
        } else {
            json.add("id", JsonNull.INSTANCE);
        }
        return json;
    }
}
