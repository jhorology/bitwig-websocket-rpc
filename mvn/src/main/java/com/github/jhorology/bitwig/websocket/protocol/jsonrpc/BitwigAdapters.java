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

import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ActionCategory;
import com.bitwig.extension.controller.api.BeatTimeValue;
import com.bitwig.extension.controller.api.BooleanValue;
import com.bitwig.extension.controller.api.ColorValue;
import com.bitwig.extension.controller.api.DoubleValue;
//#if bitwig.extension.api.version >= 11
import com.bitwig.extension.controller.api.EnumDefinition;
//#endif
import com.bitwig.extension.controller.api.EnumValue;
//#if bitwig.extension.api.version >= 11
import com.bitwig.extension.controller.api.EnumValueDefinition;
//#endif
import com.bitwig.extension.controller.api.IntegerValue;
//#if bitwig.extension.api.version >= 10
import com.bitwig.extension.controller.api.NoteStep;
//#endif
import com.bitwig.extension.controller.api.PlayingNote;
import com.bitwig.extension.controller.api.RangedValue;
import com.bitwig.extension.controller.api.StringArrayValue;
import com.bitwig.extension.controller.api.StringValue;
import com.github.jhorology.bitwig.ext.BeatTime;
import com.github.jhorology.bitwig.ext.api.CollectionValue;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * GSON type adapters for Bitwig Value Objects.
 */
public class BitwigAdapters {

  /**
   * GSON type adapters for Bitwig Value Objects.
   */
  private static final List<ImmutablePair<Class<?>, Supplier<Object>>> ADAPTED_TYPES;

  static {
    ADAPTED_TYPES = new ArrayList<>();
    ADAPTED_TYPES.add(
      new ImmutablePair<>(ShortMidiMessage.class, ShortMidiMessageAdapter::new)
    );
    ADAPTED_TYPES.add(new ImmutablePair<>(Action.class, ActionAdapter::new));
    ADAPTED_TYPES.add(
      new ImmutablePair<>(ActionCategory.class, ActionCategoryAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(BooleanValue.class, BooleanValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(ColorValue.class, ColorValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(DoubleValue.class, DoubleValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(EnumValue.class, EnumValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(IntegerValue.class, IntegerValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(PlayingNote.class, PlayingNoteAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(RangedValue.class, RangedValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(StringArrayValue.class, StringArrayValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(StringValue.class, StringValueAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(CollectionValue.class, CollectionValueAdapter::new)
    );
    //#if bitwig.extension.api.version >= 10
    ADAPTED_TYPES.add(
      new ImmutablePair<>(NoteStep.class, NoteStepAdapter::new)
    );
    //#endif
    //#if bitwig.extension.api.version >= 11
    ADAPTED_TYPES.add(
      new ImmutablePair<>(EnumDefinition.class, EnumDefinitionAdapter::new)
    );
    ADAPTED_TYPES.add(
      new ImmutablePair<>(
        EnumValueDefinition.class,
        EnumValueDefinitionAdapter::new
      )
    );
    //#endif
  }

  /**
   * Return specified value is adapted or not.
   * @param value
   * @return
   */
  static boolean isAdapted(Object value) {
    if (value == null) {
      return true;
    }
    return isAdaptedType(value.getClass());
  }

  /**
   * Return specified type is adapted or not.
   * @param type
   * @return
   */
  static boolean isAdaptedType(Class<?> type) {
    Class<?> clazz = type.isArray() ? type.getComponentType() : type;
    return ADAPTED_TYPES
      .stream()
      .anyMatch(c -> c.getLeft().isAssignableFrom(clazz));
  }

  /**
   * Adapt specified GsonBuilder to Bitiwg Value Objects.
   * @param gsonBuilder
   * @return adapted GsonBuilder.
   */
  public static GsonBuilder adapt(GsonBuilder gsonBuilder) {
    ADAPTED_TYPES.forEach(c ->
      gsonBuilder.registerTypeHierarchyAdapter(c.getLeft(), c.getRight().get())
    );
    return gsonBuilder;
  }

  /**
   * A GSON type adapter for ShortMidiMessage.
   */
  public static class ShortMidiMessageAdapter
    implements JsonSerializer<ShortMidiMessage> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      ShortMidiMessage src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("statusByte", src.getStatusByte());
      json.addProperty("data1", src.getData1());
      json.addProperty("data2", src.getData2());
      return json;
    }
  }

  /**
   * A GSON type adapter for Action.
   */
  public static class ActionAdapter implements JsonSerializer<Action> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      Action src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("id", src.getId());
      json.addProperty("name", src.getName());
      // TODO Action#isEnabled() is dead at 3.1 Beta 4
      //#if false
      json.addProperty("enabled", src.isEnabled().get());
      //#endif
      json.add(
        "category",
        context.serialize(src.getCategory(), ActionCategory.class)
      );
      json.addProperty("menuItemText", src.getMenuItemText());
      return json;
    }
  }

  /**
   * A GSON type adapter for ActionCategory.
   */
  public static class ActionCategoryAdapter
    implements JsonSerializer<ActionCategory> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      ActionCategory src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("id", src.getId());
      json.addProperty("name", src.getName());
      // this cause infinite loop.
      // json.add("actions", context.serialize(src.getActions()));
      return json;
    }
  }

  /**
   * A GSON type adapter for BooleanValue.
   */
  public static class BooleanValueAdapter
    implements JsonSerializer<BooleanValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      BooleanValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      return new JsonPrimitive(src.get());
    }
  }

  /**
   * A GSON type adapter for IntegerValue.
   */
  public static class CollectionValueAdapter
    implements JsonSerializer<CollectionValue<?>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      CollectionValue<?> src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      return context.serialize(src.values());
    }
  }

  /**
   * A GSON type adapter for ColorValue.
   */
  public static class ColorValueAdapter implements JsonSerializer<ColorValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      ColorValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("red", src.red());
      json.addProperty("green", src.green());
      json.addProperty("blue", src.blue());
      json.addProperty("alpha", src.alpha());
      return json;
    }
  }

  /**
   * A GSON type adapter for DoubleValue.
   */
  public static class DoubleValueAdapter
    implements JsonSerializer<DoubleValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      DoubleValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      if (src instanceof BeatTimeValue) {
        return context.serialize(new BeatTime(src.get(), (BeatTimeValue) src));
      }
      return new JsonPrimitive(src.get());
    }
  }

  /**
   * A GSON type adapter for EnumValue.
   */
  public static class EnumValueAdapter implements JsonSerializer<EnumValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      EnumValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      return new JsonPrimitive(src.get());
    }
  }

  /**
   * A GSON type adapter for IntegerValue.
   */
  public static class IntegerValueAdapter
    implements JsonSerializer<IntegerValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      IntegerValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      return new JsonPrimitive(src.get());
    }
  }

  /**
   * A GSON type adapter for PlayingNote.
   */
  public static class PlayingNoteAdapter
    implements JsonSerializer<PlayingNote> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      PlayingNote src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("pitch", src.pitch());
      json.addProperty("velocity", src.velocity());
      return json;
    }
  }

  /**
   * A GSON type adapter for RangedValue.
   */
  public static class RangedValueAdapter
    implements JsonSerializer<RangedValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      RangedValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("ranged", src.get());
      json.addProperty("raw", src.getRaw());
      return json;
    }
  }

  /**
   * A GSON type adapter for StringArrayValue.
   */
  public static class StringArrayValueAdapter
    implements JsonSerializer<StringArrayValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      StringArrayValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      return context.serialize(src.get());
    }
  }

  /**
   * A GSON type adapter for StringValue.
   */
  public static class StringValueAdapter
    implements JsonSerializer<StringValue> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      StringValue src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      return new JsonPrimitive(src.get());
    }
  }

  //#if bitwig.extension.api.version >= 10
  /**
   * A GSON type adapter for NoteStep.
   */
  public static class NoteStepAdapter implements JsonSerializer<NoteStep> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      NoteStep src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("x", src.x());
      json.addProperty("y", src.y());
      json.addProperty("channel", src.channel());
      json.addProperty("state", src.state().name());
      json.addProperty("velocity", src.velocity());
      json.addProperty("releaseVelocity", src.releaseVelocity());
      json.addProperty("duration", src.duration());
      json.addProperty("pan", src.pan());
      json.addProperty("timbre", src.timbre());
      json.addProperty("pressure", src.pressure());
      json.addProperty("gain", src.gain());
      json.addProperty("transpose", src.transpose());
      json.addProperty("selected", src.isIsSelected());
      //#if bitwig.extension.api.version >= 14
      json.addProperty("chance", src.chance());
      json.addProperty("chanceEnabled", src.isChanceEnabled());
      json.addProperty("occurrenceEnabled", src.isOccurrenceEnabled());
      json.addProperty("repeatEnabled", src.isRepeatEnabled());
      json.addProperty("occurrence", src.occurrence().name());
      json.addProperty("recurrenceLength", src.recurrenceLength());
      json.addProperty("recurrenceMask", src.recurrenceMask());
      json.addProperty("repeatCount", src.repeatCount());
      json.addProperty("repeatCurve", src.repeatCurve());
      json.addProperty("repeatVelocityCurve", src.repeatVelocityCurve());
      json.addProperty("repeatVelocityEnd", src.repeatVelocityEnd());
      json.addProperty("velocitySpread", src.velocitySpread());
      //#endif
      return json;
    }
  }

  //#endif

  //#if bitwig.extension.api.version >= 11
  /**
   * A GSON type adapter for EnumDefinition.
   */
  public static class EnumDefinitionAdapter
    implements JsonSerializer<EnumDefinition> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      EnumDefinition src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      List<EnumValueDefinition> array = IntStream
        .range(0, src.getValueCount())
        .mapToObj(src::valueDefinitionAt)
        .collect(Collectors.toList());
      return context.serialize(array);
    }
  }

  /**
   * A GSON type adapter for EnumValueDefinition.
   */
  public static class EnumValueDefinitionAdapter
    implements JsonSerializer<EnumValueDefinition> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(
      EnumValueDefinition src,
      Type typeOfSrc,
      JsonSerializationContext context
    ) {
      JsonObject json = new JsonObject();
      json.addProperty("valueIndex", src.getValueIndex());
      json.addProperty("id", src.getId());
      json.addProperty("displayName", src.getDisplayName());
      return json;
    }
  }
  //#endif
}
