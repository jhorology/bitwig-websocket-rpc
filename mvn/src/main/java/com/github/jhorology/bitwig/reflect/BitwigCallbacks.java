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
package com.github.jhorology.bitwig.reflect;

// jdk
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

// bitwig api
import com.bitwig.extension.api.util.midi.ShortMidiMessage;
import com.bitwig.extension.callback.BooleanValueChangedCallback;
import com.bitwig.extension.callback.ClipLauncherSlotBankPlaybackStateChangedCallback;
import com.bitwig.extension.callback.ColorValueChangedCallback;
import com.bitwig.extension.callback.ConnectionEstablishedCallback;
import com.bitwig.extension.callback.DataReceivedCallback;
import com.bitwig.extension.callback.DirectParameterDisplayedValueChangedCallback;
import com.bitwig.extension.callback.DirectParameterNameChangedCallback;
import com.bitwig.extension.callback.DirectParameterNormalizedValueChangedCallback;
import com.bitwig.extension.callback.DoubleValueChangedCallback;
import com.bitwig.extension.callback.EnumValueChangedCallback;
import com.bitwig.extension.callback.FloatValueChangedCallback;
import com.bitwig.extension.callback.IndexedBooleanValueChangedCallback;
import com.bitwig.extension.callback.IndexedColorValueChangedCallback;
import com.bitwig.extension.callback.IndexedStringValueChangedCallback;
import com.bitwig.extension.callback.IntegerValueChangedCallback;
import com.bitwig.extension.callback.NoArgsCallback;
import com.bitwig.extension.callback.NotePlaybackCallback;
import com.bitwig.extension.callback.ObjectValueChangedCallback;
import com.bitwig.extension.callback.ShortMidiDataReceivedCallback;
import com.bitwig.extension.callback.ShortMidiMessageReceivedCallback;
import com.bitwig.extension.callback.StepDataChangedCallback;
import com.bitwig.extension.callback.StringArrayValueChangedCallback;
import com.bitwig.extension.callback.StringValueChangedCallback;
import com.bitwig.extension.callback.SysexMidiDataReceivedCallback;
import com.bitwig.extension.callback.ValueChangedCallback;
import com.bitwig.extension.controller.api.BeatTimeValue;
import com.bitwig.extension.controller.api.RemoteConnection;
import com.bitwig.extension.controller.api.Value;

// dependencies
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.ext.BeatTime;
import com.github.jhorology.bitwig.websocket.protocol.jsonrpc.BitwigAdapters;

/**
 * A utility class for creating all known callbacks of Bitwig API.
 */
public class BitwigCallbacks {
    private static final Logger LOG = LoggerFactory.getLogger(BitwigCallbacks.class);
    
    // All knwown Subinterfaces of ValueChangedCallback
    private static final List<ImmutablePair<Class<? extends ValueChangedCallback>, Function<Consumer<Object[]>, ? extends ValueChangedCallback>>> GENERAL_CALLBACKS = new ArrayList<>();
    static {
        GENERAL_CALLBACKS.add(new ImmutablePair<>(BooleanValueChangedCallback.class,     BitwigCallbacks::newBooleanValueChangedCallback));
        GENERAL_CALLBACKS.add(new ImmutablePair<>(ColorValueChangedCallback.class,       BitwigCallbacks::newColorValueChangedCallback));
        GENERAL_CALLBACKS.add(new ImmutablePair<>(DoubleValueChangedCallback.class,      BitwigCallbacks::newDoubleValueChangedCallback));
        GENERAL_CALLBACKS.add(new ImmutablePair<>(EnumValueChangedCallback.class,        BitwigCallbacks::newEnumValueChangedCallback));
        GENERAL_CALLBACKS.add(new ImmutablePair<>(IntegerValueChangedCallback.class,     BitwigCallbacks::newIntegerValueChangedCallback));
        GENERAL_CALLBACKS.add(new ImmutablePair<>(StringArrayValueChangedCallback.class, BitwigCallbacks::newStringArrayValueChangedCallback));
        GENERAL_CALLBACKS.add(new ImmutablePair<>(StringValueChangedCallback.class,      BitwigCallbacks::newStringValueChangedCallback));
        GENERAL_CALLBACKS.add(new ImmutablePair<>(ObjectValueChangedCallback.class,      BitwigCallbacks::newObjectValueChangedCallback));
    }
    /**
     * create a new optimum callback for 'addValueObserber' of specified Value instance.
     * @param <T> the type of bitwig observer.
     * @param value the instance of Value interface.
     * @param observer the consumer to observe the callback parameter(s).
     */
    public static <T extends ValueChangedCallback> void registerObserver(Value<T> value, Consumer<Object[]> observer) {
        T callback = newValueChangedCallback(value, observer);
        value.addValueObserver(callback);
    }

    /**
     * create a new optimum callback for 'addValueObserber' of specified Value instance.
     * @param <T> the type of bitwig observer.
     * @param value the instance of Value interface.
     * @param observer the consumer to observe the callback parameter(s).
     * @return new callback instance
     */
    @SuppressWarnings("unchecked")
    public static <T extends ValueChangedCallback> T newValueChangedCallback(Value<T> value, Consumer<Object[]> observer) {
        // special callbacks
        if (value instanceof BeatTimeValue) {
            return (T)newBeatTimeValueChangedCallback((BeatTimeValue)value, observer);
        }
        // general callbacks
        return newGeneralValueChangedCallback(value, observer);
    }
    
    /**
     * create a new optimum callback for 'addValueObserber' of specified Value instance.
     * All known subinterfaces of ValueChangedCallback:
     * <pre>{@code
     *   BooleanValueChangedCallback
     *   ColorValueChangedCallback
     *   DoubleValueChangedCallback
     *   IntegerValueChangedCallback
     *   ObjectValueChangedCallback
     *     EnumValueChangedCallback
     *     StringValueChangedCallback
     *   StringArrayValueChangedCallback
     * }</pre>
     * @param <T> the type of bitwig observer.
     * @param value the instance of Value interface.
     * @param observer the consumer to observe the callback parameter(s).
     * @return new callback instance
     */
    @SuppressWarnings({"unchecked"})
    public static <T extends ValueChangedCallback> T newGeneralValueChangedCallback(Value<T> value, Consumer<Object[]> observer) {
        Function<Consumer<Object[]>, ? extends ValueChangedCallback> factory = GENERAL_CALLBACKS
            .stream()
            .filter(p -> {
                    try {
                        return value.getClass().getMethod("addValueObserver", p.getLeft()) != null;
                    } catch (NoSuchMethodException | SecurityException ex) {
                        return false;
                    }
                })
            .map(p -> p.getRight())
            .findFirst().orElse(null);
        
        if (factory == null) {
            throw new UnsupportedOperationException("Couldn't identify callback type from Value instance type:"
                                                    + value.getClass());
        }
        return (T)factory.apply(observer);
    }

    /**
     * create new special DoubleValueChangedCallback for BeatTimeValue
     * @param beatTimeValue
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DoubleValueChangedCallback newBeatTimeValueChangedCallback(final BeatTimeValue beatTimeValue, final Consumer<Object[]> lambda) {
        return (double value) -> {
            lambda.accept(new Object[] { new BeatTime(value, beatTimeValue)});
        };
    }

    /**
     * create new BooleanValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static BooleanValueChangedCallback newBooleanValueChangedCallback(Consumer<Object[]> lambda) {
        return (boolean value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new ClipLauncherSlotBankPlaybackStateChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ClipLauncherSlotBankPlaybackStateChangedCallback newClipLauncherSlotBankPlaybackStateChangedCallback(Consumer<Object[]> lambda) {
        return (int slotIndex, int playbackState, boolean isQueued) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("slotIndex",     slotIndex);
            obj.put("playbackState", playbackState);
            obj.put("queued",        isQueued);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new ColorValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ColorValueChangedCallback newColorValueChangedCallback(Consumer<Object[]> lambda) {
        return (float red, float green, float blue) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("red",   red);
            obj.put("green", green);
            obj.put("blue",  blue);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new ConnectionEstablishedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ConnectionEstablishedCallback newConnectionEstablishedCallback(Consumer<Object[]> lambda) {
        return (RemoteConnection rc) -> {
            lambda.accept(new Object[] {rc});
        };
    }

    /**
     * create new DataReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DataReceivedCallback newDataReceivedCallback(Consumer<Object[]> lambda) {
        return (byte[] bytes) -> {
            lambda.accept(new Object[] {bytes});
        };
    }

    /**
     * create new DirectParameterDisplayedValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterDisplayedValueChangedCallback newDirectParameterDisplayedValueChangedCallback(Consumer<Object[]> lambda) {
        return (String id, String value) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id",    id);
            obj.put("value", value);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new DirectParameterNameChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterNameChangedCallback newDirectParameterNameChangedCallback(Consumer<Object[]> lambda) {
        return (String id, String name) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id",   id);
            obj.put("name", name);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new DirectParameterNormalizedValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterNormalizedValueChangedCallback newDirectParameterNormalizedValueChangedCallback(Consumer<Object[]> lambda) {
        return (String id, double normalizedValue) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("id", id);
            obj.put("normalizedValue", normalizedValue);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new DoubleValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DoubleValueChangedCallback newDoubleValueChangedCallback(Consumer<Object[]> lambda) {
        return (double value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new EnumValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static EnumValueChangedCallback newEnumValueChangedCallback(Consumer<Object[]> lambda) {
        return (String value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new FloatValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static FloatValueChangedCallback newFLoatValueChangedCallback(Consumer<Object[]> lambda) {
        return (float value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new IndexedBooleanValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedBooleanValueChangedCallback newIndexedBooleanValueChangedCallback(Consumer<Object[]> lambda) {
        return (int index, boolean value) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("index", index);
            obj.put("value", value);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new IndexedColorValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedColorValueChangedCallback newIndexedColorValueChangedCallback(Consumer<Object[]> lambda) {
        return (int index, float red, float green, float blue) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("index", index);
            obj.put("red",   red);
            obj.put("green", green);
            obj.put("blue",  blue);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new IndexedStringValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedStringValueChangedCallback newIndexedStringValueChangedCallback(Consumer<Object[]> lambda) {
        return (int index, String value) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("index", index);
            obj.put("value", value);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new IntegerValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IntegerValueChangedCallback newIntegerValueChangedCallback(Consumer<Object[]> lambda) {
        return (int value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new NoArgsCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static NoArgsCallback newNoArgsCallback(Consumer<Object[]> lambda) {
        return () -> {
            lambda.accept(null);
        };
    }

    /**
     * create new NotePlaybackCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static NotePlaybackCallback newNotePlaybackCallback(Consumer<Object[]> lambda) {
        return (boolean isNoteOn, int key, float velocity) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("noteOn", isNoteOn);
            obj.put("key", key);
            obj.put("velocity", velocity);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new ObjectValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ObjectValueChangedCallback<?> newObjectValueChangedCallback(Consumer<Object[]> lambda) {
        return (Object value) -> {
            // for debug
            if (LOG.isDebugEnabled() &&
                value != null &&
                ReflectUtils.isBitwigAPI(value.getClass()) &&
                !BitwigAdapters.isAdapted(value)) {
                LOG.debug("maybe need seiralization adapter valueType:{}", value.getClass());
            }
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new ShortMidiDataReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ShortMidiDataReceivedCallback newShortMidiDataReceivedCallback(Consumer<Object[]> lambda) {
        return (int statusByte, int data1, int data2) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("status", statusByte);
            obj.put("data1", data1);
            obj.put("data2", data2);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new ShortMidiMessageReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ShortMidiMessageReceivedCallback newShortMidiMessageReceivedCallback(Consumer<Object[]> lambda) {
        return (ShortMidiMessage value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new StepDataChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StepDataChangedCallback newStepDataChangedCallback(Consumer<Object[]> lambda) {
        return (int x, int y, int state) -> {
            Map<String, Object> obj = new HashMap<>();
            obj.put("x", x);
            obj.put("y", y);
            obj.put("state", state);
            lambda.accept(new Object[] {obj});
        };
    }

    /**
     * create new StringArrayValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StringArrayValueChangedCallback newStringArrayValueChangedCallback(Consumer<Object[]> lambda) {
        return (String[] value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new StringValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StringValueChangedCallback newStringValueChangedCallback(Consumer<Object[]> lambda) {
        return (String value) -> {
            lambda.accept(new Object[] {value});
        };
    }

    /**
     * create new SysexMidiDataReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static SysexMidiDataReceivedCallback newSysexMidiDataReceivedCallback(Consumer<Object[]> lambda) {
        return (String value) -> {
            lambda.accept(new Object[] {value});
        };
    }
}
