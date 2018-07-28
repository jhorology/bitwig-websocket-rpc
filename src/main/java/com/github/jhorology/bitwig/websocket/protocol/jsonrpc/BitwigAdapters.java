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

import java.lang.reflect.Type;

import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ActionCategory;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ColorValue;
import com.bitwig.extension.controller.api.DoubleValue;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.IntegerValue;
import com.bitwig.extension.controller.api.RangedValue;
import com.bitwig.extension.controller.api.StringArrayValue;
import com.bitwig.extension.controller.api.StringValue;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Gson TypeAdapters for Bitwig Value Objects.
 */
public class BitwigAdapters {
    
    /**
     * create new GsonBuilder that support Bitwig Value Objects.
     * @return 
     */
    public static GsonBuilder newGsonBuilder() {
        return new GsonBuilder()
            .registerTypeHierarchyAdapter(BooleanValue.class, new BooleanValueAdapter())
            .registerTypeHierarchyAdapter(ColorValue.class, new ColorValueAdapter())
            .registerTypeHierarchyAdapter(DoubleValue.class, new DoubleValueAdapter())
            .registerTypeHierarchyAdapter(EnumValue.class, new EnumValueAdapter())
            .registerTypeHierarchyAdapter(IntegerValue.class, new IntegerValueAdapter())
            .registerTypeHierarchyAdapter(RangedValue.class, new RangedValueAdapter())
            .registerTypeHierarchyAdapter(StringArrayValue.class, new StringArrayValueAdapter())
            .registerTypeHierarchyAdapter(StringValue.class, new StringValueAdapter())
            .registerTypeHierarchyAdapter(Action.class, new ActionAdapter())
            .registerTypeHierarchyAdapter(ActionCategory.class, new ActionCategoryAdapter());
    }
    
    public static class BooleanValueAdapter implements JsonSerializer<BooleanValue> {
        @Override
        public JsonElement serialize(BooleanValue src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }
    }
    
    public static class ColorValueAdapter implements JsonSerializer<ColorValue> {
        @Override
        public JsonElement serialize(ColorValue src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("red", src.red());
            json.addProperty("green", src.green());
            json.addProperty("blue", src.blue());
            json.addProperty("alpha", src.alpha());
            return json;
        }
    }
    
    public static class DoubleValueAdapter implements JsonSerializer<DoubleValue> {
        @Override
        public JsonElement serialize(DoubleValue src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }
    }
    
    public static class EnumValueAdapter implements JsonSerializer<EnumValue> {
        @Override
        public JsonElement serialize(EnumValue src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }
    }
    
    public static class IntegerValueAdapter implements JsonSerializer<IntegerValue> {
        @Override
        public JsonElement serialize(IntegerValue src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }
    }
    
    public static class RangedValueAdapter implements JsonSerializer<RangedValue> {
        @Override
        public JsonElement serialize(RangedValue src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("ranged", src.get());
            json.addProperty("raw", src.getRaw());
            return json;
        }
    }

    public static class StringArrayValueAdapter implements JsonSerializer<StringArrayValue> {
        @Override
        public JsonElement serialize(StringArrayValue src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.get());
        }
    }
    
    public static class StringValueAdapter implements JsonSerializer<StringValue> {
        @Override
        public JsonElement serialize(StringValue src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.get());
        }
    }
    
    public static class ActionAdapter implements JsonSerializer<Action> {
        @Override
        public JsonElement serialize(Action src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("id", src.getId());
            json.addProperty("name", src.getName());
            json.add("category", context.serialize(src.getCategory(), ActionCategory.class));
            json.addProperty("menuItemText", src.getMenuItemText());
            return json;
        }
    }
    
    public static class ActionCategoryAdapter implements JsonSerializer<ActionCategory> {
        @Override
        public JsonElement serialize(ActionCategory src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("id", src.getId());
            json.addProperty("name", src.getName());
            json.add("actions", context.serialize(src.getActions()));
            return json;
        }
    }
}
