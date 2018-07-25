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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.lang.reflect.ParameterizedType;
import java.util.function.Function;
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
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class BitwigCallbacks {
    // TODO
    // which is better?
    // convert multiple paramers to named parameter or not.
    private static final boolean PREFER_NAMED_PARAMS = true;
    
    // All knwon Subinterfaces of ValueChangedCallback
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
     * create a new callback for 'addValueObserber' method from the instance of Value interface.
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
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return new callback instance
     */
    public static ValueChangedCallback newValueChangedCallback(Value<? extends ValueChangedCallback>value, Consumer<Object> lamda) {
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
        return factory.apply(lamda);
    }
    
    /**
     * create new BooleanValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static BooleanValueChangedCallback newBooleanValueChangedCallback(Consumer<Object> lamda) {
        return (boolean value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new ClipLauncherSlotBankPlaybackStateChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ClipLauncherSlotBankPlaybackStateChangedCallback newClipLauncherSlotBankPlaybackStateChangedCallback(Consumer<Object> lamda) {
        return (int slotIndex, int playbackState, boolean isQueued) -> {
            lamda.accept(createParams(new String[] {"slotIndex", "playbackState", "isQueued"},
                                      new Object[] { slotIndex,   playbackState,   isQueued}));
        };
    }

    /**
     * create new ColorValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ColorValueChangedCallback newColorValueChangedCallback(Consumer<Object> lamda) {
        return (float red, float green, float blue) -> {
            lamda.accept(createParams(new String[] {"red", "green", "blue"},
                                      new Object[] {red,    green,   blue}));
        };
    }

    /**
     * create new ConnectionEstablishedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ConnectionEstablishedCallback newConnectionEstablishedCallback(Consumer<Object> lamda) {
        return (RemoteConnection rc) -> {
            lamda.accept(rc);
        };
    }

    /**
     * create new DataReceivedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DataReceivedCallback newDataReceivedCallback(Consumer<Object> lamda) {
        return (byte[] bytes) -> {
            lamda.accept(bytes);
        };
    }

    /**
     * create new DirectParameterDisplayedValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterDisplayedValueChangedCallback newDirectParameterDisplayedValueChangedCallback(Consumer<Object> lamda) {
        return (String id, String value) -> {
            lamda.accept(createParams(new String[] {"id", "value"},
                                      new Object[] { id,   value}));
        };
    }

    /**
     * create new DirectParameterNameChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterNameChangedCallback newDirectParameterNameChangedCallback(Consumer<Object> lamda) {
        return (String id, String name) -> {
            lamda.accept(createParams(new String[] {"id", "name"},
                                      new Object[] { id,   name}));
        };
    }

    /**
     * create new DirectParameterNormalizedValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DirectParameterNormalizedValueChangedCallback newDirectParameterNormalizedValueChangedCallback(Consumer<Object> lamda) {
        return (String id, double normalizedValue) -> {
            lamda.accept(createParams(new String[] {"id", "normalizedValue"},
                                      new Object[] { id,   normalizedValue}));
        };
    }

    /**
     * create new DoubleValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static DoubleValueChangedCallback newDoubleValueChangedCallback(Consumer<Object> lamda) {
        return (double value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new EnumValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static EnumValueChangedCallback newEnumValueChangedCallback(Consumer<Object> lamda) {
        return (String value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new FloatValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static FloatValueChangedCallback newFLoatValueChangedCallback(Consumer<Object> lamda) {
        return (float value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new IndexedBooleanValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedBooleanValueChangedCallback newIndexedBooleanValueChangedCallback(Consumer<Object> lamda) {
        return (int index, boolean value) -> {
            lamda.accept(createParams(new String[] {"index", "value"},
                                      new Object[] { index,   value}));
        };
    }

    /**
     * create new IndexedColorValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedColorValueChangedCallback newIndexedColorValueChangedCallback(Consumer<Object> lamda) {
        return (int index, float red, float green, float blue) -> {
            lamda.accept(createParams(new String[] {"index", "red", "green", "blue"},
                                      new Object[] { index,   red,   green,   blue}));
        };
    }

    /**
     * create new IndexedStringValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IndexedStringValueChangedCallback newIndexedStringValueChangedCallback(Consumer<Object> lamda) {
        return (int index, String value) -> {
            lamda.accept(createParams(new String[] {"index", "value"},
                                      new Object[] { index,   value}));
        };
    }

    /**
     * create new IntegerValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static IntegerValueChangedCallback newIntegerValueChangedCallback(Consumer<Object> lamda) {
        return (int value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new NoArgsCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static NoArgsCallback newNoArgsCallback(Consumer<Object> lamda) {
        return () -> {
            lamda.accept(null);
        };
    }

    /**
     * create new NotePlaybackCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static NotePlaybackCallback newNotePlaybackCallback(Consumer<Object> lamda) {
        return (boolean isNoteOn, int key, float velocity) -> {
            lamda.accept(createParams(new String[] {"isNoteOn", "key", "velocity"},
                                      new Object[] { isNoteOn,   key,   velocity}));
        };
    }

    /**
     * create new ObjectValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static <T> ObjectValueChangedCallback<T> newObjectValueChangedCallback(Consumer<Object> lamda) {
        return (T value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new ShortMidiDataReceivedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ShortMidiDataReceivedCallback newShortMidiDataReceivedCallback(Consumer<Object> lamda) {
        return (int statusByte, int data1, int data2) -> {
            lamda.accept(createParams(new String[] {"statusByte", "data1", "data2"},
                                      new Object[] { statusByte,   data1,   data2}));
        };
    }

    /**
     * create new ShortMidiMessageReceivedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static ShortMidiMessageReceivedCallback newShortMidiMessageReceivedCallback(Consumer<Object> lamda) {
        return (ShortMidiMessage value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new StepDataChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StepDataChangedCallback newStepDataChangedCallback(Consumer<Object> lamda) {
        return (int x, int y, int state) -> {
            lamda.accept(createParams(new String[] {"x", "y", "state"},
                                      new Object[] { x,   y,   state}));
        };
    }

    /**
     * create new StringArrayValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StringArrayValueChangedCallback newStringArrayValueChangedCallback(Consumer<Object> lamda) {
        return (String[] value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new StringValueChangedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static StringValueChangedCallback newStringValueChangedCallback(Consumer<Object> lamda) {
        return (String value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create new SysexMidiDataReceivedCallback.
     * @param lamda the lamda consumer to observe the callback parameter(s).
     * @return return the instance of callback.
     */
    public static SysexMidiDataReceivedCallback newSysexMidiDataReceivedCallback(Consumer<Object> lamda) {
        return (String value) -> {
            lamda.accept(value);
        };
    }

    /**
     * create params for Notification message.
     * @param names
     * @param values
     */
    private static Object createParams(final String[] names, final Object[] values) {
        if (PREFER_NAMED_PARAMS) {
            return IntStream.range(0, values.length)
                .collect(HashMap::new,(m,i) -> m.put(names[i], values[i]), Map::putAll);
        }
        return values;
    }
}
