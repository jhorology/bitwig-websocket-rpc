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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.lang.reflect.ParameterizedType;
import java.util.stream.Stream;

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
import com.bitwig.extension.controller.api.RemoteConnection;
import com.bitwig.extension.controller.api.Value;

/**
 * A utility class for creating all known callbacks of Bitwig API.
 */
public class BitwigCallbacks {
    // TODO
    // which is better?
    // convert multiple paramers to named parameter or not.
    private static final boolean PREFER_NAMED_PARAMS = true;

    // All knwown Subinterfaces of ValueChangedCallback
    private static final Map<Class<?>, Function<Consumer<Object>, ? extends ValueChangedCallback>> CALLBACK_FACTORY = new HashMap<>();
    static {
        CALLBACK_FACTORY.put(BooleanValueChangedCallback.class,     BitwigCallbacks::newBooleanValueChangedCallback);
        CALLBACK_FACTORY.put(ColorValueChangedCallback.class,       BitwigCallbacks::newColorValueChangedCallback);
        CALLBACK_FACTORY.put(DoubleValueChangedCallback.class,      BitwigCallbacks::newDoubleValueChangedCallback);
        CALLBACK_FACTORY.put(EnumValueChangedCallback.class,        BitwigCallbacks::newEnumValueChangedCallback);
        CALLBACK_FACTORY.put(IntegerValueChangedCallback.class,     BitwigCallbacks::newIntegerValueChangedCallback);
        CALLBACK_FACTORY.put(StringArrayValueChangedCallback.class, BitwigCallbacks::newStringArrayValueChangedCallback);
        CALLBACK_FACTORY.put(StringValueChangedCallback.class,      BitwigCallbacks::newStringValueChangedCallback);
        CALLBACK_FACTORY.put(ObjectValueChangedCallback.class,      BitwigCallbacks::newObjectValueChangedCallback);
    }

    /**
     * create a new optimunm callback for 'addValueObserber' of specified instance of Value intreface.
     * All Konwn Subinterfaces of ValueChangedCallback:
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
     * @param value the instance of Value interface.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return new callback instance
     */
    public static ValueChangedCallback newValueChangedCallback(Value<? extends ValueChangedCallback>value, Consumer<Object> lambda) {
        // TODO
        // I'm sure that there is more smarter ways...
        // couldn't find the rules from API.
        Class<?> callbackType = Stream.of(value.getClass().getMethods())
            .filter(m -> "addValueObserver".equals(m.getName()))
            .map(m -> m.getParameterTypes())
            .filter(t -> t.length == 1)
            .map(t -> t[0])
            .filter(t -> !ValueChangedCallback.class.equals(t))
            .findFirst().orElse(null);

        if (callbackType == null) {
            throw new UnsupportedOperationException("Couldn't identify callback type from Value instance type ["
                                                    + value.getClass()
                                                    + "].");
        }
        Function<Consumer<Object>, ? extends ValueChangedCallback> factory = CALLBACK_FACTORY.get(callbackType);
        if (factory == null) {
            throw new UnsupportedOperationException("Unsupported callback type ["
                                                    + callbackType
                                                    + "].");
        }
        return factory.apply(lambda);
    }

    /**
     * create new BooleanValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static BooleanValueChangedCallback newBooleanValueChangedCallback(Consumer<Object> lambda) {
        return (boolean value) -> {
            lambda.accept(createParams(new String[] {"value"},
                                       new Object[] { value}));
        };
    }

    /**
     * create new ClipLauncherSlotBankPlaybackStateChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ClipLauncherSlotBankPlaybackStateChangedCallback newClipLauncherSlotBankPlaybackStateChangedCallback(Consumer<Object> lambda) {
        return (int slotIndex, int playbackState, boolean isQueued) -> {
            lambda.accept(createParams(new String[] {"slotIndex", "playbackState", "isQueued"},
                                       new Object[] { slotIndex,   playbackState,   isQueued}));
        };
    }

    /**
     * create new ColorValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ColorValueChangedCallback newColorValueChangedCallback(Consumer<Object> lambda) {
        return (float red, float green, float blue) -> {
            lambda.accept(createParams(new String[] {"red", "green", "blue"},
                                       new Object[] {red,    green,   blue}));
        };
    }

    /**
     * create new ConnectionEstablishedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ConnectionEstablishedCallback newConnectionEstablishedCallback(Consumer<Object> lambda) {
        return (RemoteConnection rc) -> {
            lambda.accept(rc);
        };
    }

    /**
     * create new DataReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DataReceivedCallback newDataReceivedCallback(Consumer<Object> lambda) {
        return (byte[] bytes) -> {
            lambda.accept(bytes);
        };
    }

    /**
     * create new DirectParameterDisplayedValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterDisplayedValueChangedCallback newDirectParameterDisplayedValueChangedCallback(Consumer<Object> lambda) {
        return (String id, String value) -> {
            lambda.accept(createParams(new String[] {"id", "value"},
                                       new Object[] { id,   value}));
        };
    }

    /**
     * create new DirectParameterNameChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterNameChangedCallback newDirectParameterNameChangedCallback(Consumer<Object> lambda) {
        return (String id, String name) -> {
            lambda.accept(createParams(new String[] {"id", "name"},
                                       new Object[] { id,   name}));
        };
    }

    /**
     * create new DirectParameterNormalizedValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterNormalizedValueChangedCallback newDirectParameterNormalizedValueChangedCallback(Consumer<Object> lambda) {
        return (String id, double normalizedValue) -> {
            lambda.accept(createParams(new String[] {"id", "normalizedValue"},
                                       new Object[] { id,   normalizedValue}));
        };
    }

    /**
     * create new DoubleValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DoubleValueChangedCallback newDoubleValueChangedCallback(Consumer<Object> lambda) {
        return (double value) -> {
            lambda.accept(createParams(new String[] {"value"},
                                       new Object[] { value}));
        };
    }

    /**
     * create new EnumValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static EnumValueChangedCallback newEnumValueChangedCallback(Consumer<Object> lambda) {
        return (String value) -> {
            lambda.accept(createParams(new String[] {"value"},
                                       new Object[] { value}));
        };
    }

    /**
     * create new FloatValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static FloatValueChangedCallback newFLoatValueChangedCallback(Consumer<Object> lambda) {
        return (float value) -> {
            lambda.accept(createParams(new String[] {"value"},
                                       new Object[] { value}));
        };
    }

    /**
     * create new IndexedBooleanValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedBooleanValueChangedCallback newIndexedBooleanValueChangedCallback(Consumer<Object> lambda) {
        return (int index, boolean value) -> {
            lambda.accept(createParams(new String[] {"index", "value"},
                                       new Object[] { index,   value}));
        };
    }

    /**
     * create new IndexedColorValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedColorValueChangedCallback newIndexedColorValueChangedCallback(Consumer<Object> lambda) {
        return (int index, float red, float green, float blue) -> {
            lambda.accept(createParams(new String[] {"index", "red", "green", "blue"},
                                       new Object[] { index,   red,   green,   blue}));
        };
    }

    /**
     * create new IndexedStringValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedStringValueChangedCallback newIndexedStringValueChangedCallback(Consumer<Object> lambda) {
        return (int index, String value) -> {
            lambda.accept(createParams(new String[] {"index", "value"},
                                       new Object[] { index,   value}));
        };
    }

    /**
     * create new IntegerValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IntegerValueChangedCallback newIntegerValueChangedCallback(Consumer<Object> lambda) {
        return (int value) -> {
            lambda.accept(createParams(new String[] {"value"},
                                       new Object[] { value}));
        };
    }

    /**
     * create new NoArgsCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static NoArgsCallback newNoArgsCallback(Consumer<Object> lambda) {
        return () -> {
            lambda.accept(null);
        };
    }

    /**
     * create new NotePlaybackCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static NotePlaybackCallback newNotePlaybackCallback(Consumer<Object> lambda) {
        return (boolean isNoteOn, int key, float velocity) -> {
            lambda.accept(createParams(new String[] {"isNoteOn", "key", "velocity"},
                                       new Object[] { isNoteOn,   key,   velocity}));
        };
    }

    /**
     * create new ObjectValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static <T> ObjectValueChangedCallback<T> newObjectValueChangedCallback(Consumer<Object> lambda) {
        return (T value) -> {
            lambda.accept(value);
        };
    }

    /**
     * create new ShortMidiDataReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ShortMidiDataReceivedCallback newShortMidiDataReceivedCallback(Consumer<Object> lambda) {
        return (int statusByte, int data1, int data2) -> {
            lambda.accept(createParams(new String[] {"statusByte", "data1", "data2"},
                                       new Object[] { statusByte,   data1,   data2}));
        };
    }

    /**
     * create new ShortMidiMessageReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ShortMidiMessageReceivedCallback newShortMidiMessageReceivedCallback(Consumer<Object> lambda) {
        return (ShortMidiMessage value) -> {
            lambda.accept(value);
        };
    }

    /**
     * create new StepDataChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StepDataChangedCallback newStepDataChangedCallback(Consumer<Object> lambda) {
        return (int x, int y, int state) -> {
            lambda.accept(createParams(new String[] {"x", "y", "state"},
                                       new Object[] { x,   y,   state}));
        };
    }

    /**
     * create new StringArrayValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StringArrayValueChangedCallback newStringArrayValueChangedCallback(Consumer<Object> lambda) {
        return (String[] value) -> {
            lambda.accept(value);
        };
    }

    /**
     * create new StringValueChangedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StringValueChangedCallback newStringValueChangedCallback(Consumer<Object> lambda) {
        return (String value) -> {
            lambda.accept(createParams(new String[] {"value"},
                                       new Object[] { value}));
        };
    }

    /**
     * create new SysexMidiDataReceivedCallback.
     * @param lambda the lambda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static SysexMidiDataReceivedCallback newSysexMidiDataReceivedCallback(Consumer<Object> lambda) {
        return (String value) -> {
            lambda.accept(createParams(new String[] {"value"},
                                       new Object[] { value}));
        };
    }

    /**
     * create params for Notification message.
     * @param names
     * @param values
     */
    private static Object createParams(final String[] names, final Object[] values) {
        if (PREFER_NAMED_PARAMS && values.length > 1) {
            return IntStream.range(0, values.length)
                .collect(HashMap::new,(m,i) -> m.put(names[i], values[i]), Map::putAll);
        }
        return values;
    }
}
