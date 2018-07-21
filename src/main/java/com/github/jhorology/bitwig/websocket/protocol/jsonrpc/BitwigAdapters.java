
package com.github.jhorology.bitwig.websocket.protocol.jsonrpc;

import java.lang.reflect.Type;

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

public class BitwigAdapters {
    
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

    public static GsonBuilder newGsonBuilder() {
        return new GsonBuilder()
            .registerTypeHierarchyAdapter(BooleanValue.class, new BooleanValueAdapter())
            .registerTypeHierarchyAdapter(ColorValue.class, new ColorValueAdapter())
            .registerTypeHierarchyAdapter(DoubleValue.class, new DoubleValueAdapter())
            .registerTypeHierarchyAdapter(EnumValue.class, new EnumValueAdapter())
            .registerTypeHierarchyAdapter(IntegerValue.class, new IntegerValueAdapter())
            .registerTypeHierarchyAdapter(RangedValue.class, new RangedValueAdapter())
            .registerTypeHierarchyAdapter(StringArrayValue.class, new StringArrayValueAdapter())
            .registerTypeHierarchyAdapter(StringValue.class, new StringValueAdapter());
    }
}
