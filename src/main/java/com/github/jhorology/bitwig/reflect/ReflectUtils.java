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
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

// bitwig api
import com.bitwig.extension.callback.Callback;
import com.bitwig.extension.controller.api.Bank;
import com.bitwig.extension.controller.api.BrowserFilterColumn;
import com.bitwig.extension.controller.api.BrowserFilterColumnBank;
import com.bitwig.extension.controller.api.BrowserItem;
import com.bitwig.extension.controller.api.BrowserItemBank;
import com.bitwig.extension.controller.api.BrowsingSessionBank;
import com.bitwig.extension.controller.api.Channel;
import com.bitwig.extension.controller.api.ChannelBank;
import com.bitwig.extension.controller.api.ClipLauncherSlot;
import com.bitwig.extension.controller.api.ClipLauncherSlotBank;
import com.bitwig.extension.controller.api.ClipLauncherSlotOrScene;
import com.bitwig.extension.controller.api.ClipLauncherSlotOrSceneBank;
import com.bitwig.extension.controller.api.CueMarker;
import com.bitwig.extension.controller.api.CueMarkerBank;
import com.bitwig.extension.controller.api.Device;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.DeviceLayer;
import com.bitwig.extension.controller.api.DeviceLayerBank;
import com.bitwig.extension.controller.api.DrumPad;
import com.bitwig.extension.controller.api.DrumPadBank;
import com.bitwig.extension.controller.api.GenericBrowsingSession;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.ParameterBank;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.RemoteControlsPage;
import com.bitwig.extension.controller.api.Scene;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.Send;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.StringArrayValue;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.Value;
import com.github.jhorology.bitwig.extension.Logger;

// source
import com.github.jhorology.bitwig.rpc.RpcParamType;
import java.util.stream.Collectors;

/**
 * utility class
 */
@SuppressWarnings("UseSpecificCatch")
public class ReflectUtils {
    private static final Logger LOG = Logger.getLogger(ReflectUtils.class);
    // TODO make it configurable
    private static final boolean PUBLISH_SUBSCRIBABLE_METHODS = true;

    /**
     * empty array of Object.
     */
    public static final Object[] EMPTY_ARRAY = {};
    /**
     * empty array of Class<?>.
     */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = {};
    public static final Class<?>[] BANK_METHOD_PARAM_TYPES = {int.class};
    private static final Map<Class<? extends Bank>, Class<?>> BANK_ITEM_TYPES;
    private static final Map<Method, Class<?>> BANK_METHOD_TYPES;
    static {
        BANK_ITEM_TYPES = new LinkedHashMap<>();
        BANK_METHOD_TYPES = new LinkedHashMap<>();
        try {
            // All known sub-interfaces of Bank
            // ItemType can't be resolved at runtime.
            // public interface Bank<ItemType extends ObjectProxy> extends ObjectProxy, Scrollable
            BANK_ITEM_TYPES.put(BrowsingSessionBank.class, GenericBrowsingSession.class);
            BANK_ITEM_TYPES.put(BrowserFilterColumnBank.class, BrowserFilterColumn.class);
            BANK_ITEM_TYPES.put(BrowserItemBank.class, BrowserItem.class);
            BANK_ITEM_TYPES.put(CueMarkerBank.class, CueMarker.class);
            BANK_ITEM_TYPES.put(DeviceBank.class, Device.class);
            BANK_ITEM_TYPES.put(SendBank.class, Send.class);
            //public interface ChannelBank<ChannelType extends Channel> extends ObjectProxy, Bank<ChannelType>
            BANK_ITEM_TYPES.put(DeviceLayerBank.class, DeviceLayer.class);
            BANK_ITEM_TYPES.put(DrumPadBank.class, DrumPad.class);
            BANK_ITEM_TYPES.put(TrackBank.class, Track.class);
            BANK_ITEM_TYPES.put(ChannelBank.class, Channel.class);
            // public interface ClipLauncherSlotOrSceneBank<ItemType extends ClipLauncherSlotOrScene> extends Bank<ItemType> 
            BANK_ITEM_TYPES.put(ClipLauncherSlotBank.class, ClipLauncherSlot.class);
            BANK_ITEM_TYPES.put(SceneBank.class, Scene.class);
            BANK_ITEM_TYPES.put(ClipLauncherSlotOrSceneBank.class, ClipLauncherSlotOrScene.class);

            // bank methods
            // null for returns ObjectProxy at runtime
            BANK_METHOD_TYPES.put(Bank.class.getMethod("getItemAt", BANK_METHOD_PARAM_TYPES), null);
            BANK_METHOD_TYPES.put(RemoteControlsPage.class.getMethod("getParameter", BANK_METHOD_PARAM_TYPES), RemoteControl.class);
            BANK_METHOD_TYPES.put(ParameterBank.class.getMethod("getParameter", BANK_METHOD_PARAM_TYPES), Parameter.class);

            // TODO need more methods...
        } catch (Exception ex) {
        }
    }

    public static boolean isVarargs(Type[] paramTypes) {
        if (paramTypes.length == 1) {
            Type t = paramTypes[0];
            return (t instanceof Class && ((Class<?>)t).isArray())
                || t instanceof GenericArrayType;
        }
        return false;
    }

    /**
     * convert to varargs type if available
     * [Number, Number, Number] -> [Namber[]]
     * @param rpcParamTypes
     * @return
     */
    public static RpcParamType[] toVarargs(RpcParamType[] rpcParamTypes) {
        if (rpcParamTypes.length >= 1) {
            final RpcParamType expectedType = rpcParamTypes[0];
            if(!expectedType.isArray()) {
                boolean allSameType = Stream.of(rpcParamTypes)
                    .allMatch(t -> (t == expectedType));
                if (allSameType) {
                    RpcParamType arrayType = expectedType.getArrayType();
                    return new RpcParamType[] {arrayType};
                }
            }
        }
        return null;
    }

    /**
     * return interface type is Bitwig API or not.
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigAPI(Class<?> interfaceType) {
        return interfaceType.getName().startsWith("com.bitwig.extension.");
    }

    /**
     * return interface type is Bitwig Controller API or not.
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigControllerAPI(Class<?> interfaceType) {
        return interfaceType.getName().startsWith("com.bitwig.extension.controller.api.");
    }

    /**
     * Specified interfaceType is Bitwig Extension API or not.
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigExtensionAPI(Class<?> interfaceType) {
        return interfaceType.getName().startsWith("com.bitwig.extension.api.");
    }

    /**
     * Return value of spcified method is implemented Value interface or not ?
     * it mean having addValueObserver method or not.
     * @param method
     * @return
     */
    public static boolean isBitwigValue(Method method) {
        return isBitwigValue(method.getReturnType());
    }

    /**
     * Specifid interfaceType is implemented Value interface or not ?
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigValue(Class<?> interfaceType) {
        return Value.class.isAssignableFrom(interfaceType);
    }

    /**
     * Return value of specified method is implemented Prameter interface or not ?
     * @param method
     * @return
     */
    public static boolean isBitwigParameter(Method method) {
        return isBitwigParameter(method.getReturnType());
    }

    /**
     * Secified interfaceType is implemented Prameter interface or not ?
     * @param interfaceType
     * @return
     */
    public static boolean isBitwigParameter(Class<?> interfaceType) {
        return Parameter.class.isAssignableFrom(interfaceType);
    }

    /**
     * Method has any paramater of Callback or not.
     * @param method
     * @return
     */
    public static boolean hasAnyCallbackParameter(Method method) {
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) return false;
        return Stream.of(types)
            .anyMatch(Callback.class::isAssignableFrom);
    }

    /**
     * Returns specified method has any object parameter or not.
     * @param method
     * @return
     */
    public static boolean hasAnyBitwigObjectParameter(Method method) {
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 0) return false;
        return Stream.of(types)
            .map(t -> t.isArray() ? t.getComponentType() : t)
            .filter(ReflectUtils::isBitwigAPI)
            .map(RpcParamType::of)
            .anyMatch(t -> t == RpcParamType.OBJECT);
    }

    /**
     * Method has any parameter of OBJECT or ARRAY.
     * @param method
     * @return
     */
    public static boolean hasAnyObjectOrArrayParameter(Method method) {
        Type [] types = method.getGenericParameterTypes();
        if (types.length == 0) return false;
        return Stream.of(types)
            .map(RpcParamType::of)
            .anyMatch(t -> (t == RpcParamType.OBJECT || t.isArray()));
    }

    /**
     * Returns method is deprecated or not.
     * @param method
     * @return
     */
    public static boolean isDeprecated(Method method) {
        return method.getAnnotation(Deprecated.class) != null;
    }

    /**
     * Return class is deprecated or not.
     * @param clazz
     * @return
     */
    public static boolean isDeprecated(Class<?> clazz) {
        return clazz.getAnnotation(Deprecated.class) != null;
    }

    /**
     * Retun specified method is core module factory or not.
     *  it's should be managed as RPC module.
     * @param method
     * @return
     */
    public static boolean isModuleFactory(Method method) {
        // TODO
        // need to investigate core modules that can be instantiated at only within init.
        //
        // this is enough for now.
        boolean result = method.getName().startsWith("create")
            && !isBitwigValue(method.getReturnType())
            && isBitwigAPI(method.getReturnType());
        if (result && Logger.isDebugEnabled()) {
            LOG.debug("Method[" + method.getDeclaringClass().getSimpleName() + "#" + method.getName()
                      + "] has been ignored as factory method for core module.");
        }
        return result;
    }

    /**
     * Secified interfaceType is implemented Bank or not ?
     * @param interfaceType
     * @return
     */
    public static boolean isBank(Class<?> interfaceType) {
        return Bank.class.isAssignableFrom(interfaceType);
    }

    /**
     * Secified interfaceType is implemented Bank or not ?
     * @param method
     * @return
     */
    public static boolean isBankMethod(Method method) {
        return BANK_METHOD_TYPES.containsKey(method);
    }

    /**
     * Returns a bank item type of specified bank type.
     * @param bankType the type of bank.
     * @return the type of bank item.
     */
    public static Class<?> getBankItemType(Class<? extends Bank> bankType) {
        return BANK_ITEM_TYPES.get(bankType);
    }

    /**
     * Returns a bank item type of specified method.
     * @param bankMethod the type of bank.
     * @return the type of bank item.
     */
    public static Class<?> getBankItemType(Method bankMethod) {
        return BANK_METHOD_TYPES.get(bankMethod);
    }

    /**
     * Returns a bank item type of specified interface type and method.
     * @param interfaceType
     * @param method
     * @return the type of bank item.
     */
    @SuppressWarnings("element-type-mismatch")
    public static Class<?> getBankItemType(Class<?> interfaceType, Method method) {
        if (!isBankMethod(method)) {
            return null;
        }
        Class<?> bankItemType = BANK_METHOD_TYPES.get(method);
        if (bankItemType != null) {
            return bankItemType;
        }
        if (interfaceType != null && isBank(interfaceType)) {
            return BANK_ITEM_TYPES.get(interfaceType);
        }
        return null;
    }


    /**
     * WTF! cleanup confilicted or deprecated or blacklisted methods
     * @param interfaceType
     * @return the list of methods
     */
    public static List<Method> getCleanMethods(Class<?> interfaceType) {
        boolean fixRemoteControlsPage = RemoteControlsPage.class.isAssignableFrom(interfaceType)
            && ParameterBank.class.isAssignableFrom(interfaceType);
        Stream<Method> methodsStream = Arrays.asList(interfaceType.getMethods())
            .stream()
            .filter(m -> !isDeprecated(m))
            .filter(m -> !isDeprecated(m.getReturnType()))
            .filter(m -> !hasAnyBitwigObjectParameter(m))
            .filter(m -> !hasAnyCallbackParameter(m))
            .filter(m -> !isModuleFactory(m));

        // markInterested is probably same as Subscribable#subscibe()
        if (Value.class.isAssignableFrom(interfaceType)) {
            methodsStream = methodsStream
                .filter(m -> !("markInterested".equals(m.getName())));
        }


        // WTF! fixing one by one

        // StringArrayValue has two get() methods.  Object[] get()/String[] get()
        if (StringArrayValue.class.isAssignableFrom(interfaceType)) {
            methodsStream = methodsStream
                .filter(m -> !("get".equals(m.getName())
                               && Object[].class.equals(m.getReturnType())));
        }
        // RemoteControlsPage/ParameterBank duplicated getParameter method
        if (RemoteControlsPage.class.isAssignableFrom(interfaceType)
            && ParameterBank.class.isAssignableFrom(interfaceType)) {
            methodsStream = methodsStream
                .filter(m -> !("getParameter".equals(m.getName())
                               && Parameter.class.equals(m.getReturnType())));
        }
        // RemoteContrrol has two name() methods. returnType: StringValue/SettableStringValue
        if (RemoteControl.class.isAssignableFrom(interfaceType)) {
            methodsStream = methodsStream
                .filter(m -> !("name".equals(m.getName())
                               && StringValue.class.equals(m.getReturnType())));
        }
        // MasterTrack#sendBank() is useless, and host throw exception.
        if (MasterTrack.class.isAssignableFrom(interfaceType)) {
            methodsStream = methodsStream
                .filter(m -> !"sendBank".equals(m.getName()));
        }



        List<Method> methods = methodsStream.collect(Collectors.toList());
        // for debug
        if (Logger.isDebugEnabled()) {
            methods.stream().forEach(m0 -> {
                    methods.stream().forEach(m1 -> {
                            if (m0 != m1) {
                                MethodIdentifier m0id = new MethodIdentifier(m0.getName(), m0.getGenericParameterTypes());
                                MethodIdentifier m1id = new MethodIdentifier(m1.getName(), m1.getGenericParameterTypes());
                                if (m0id.equals(m1id)) {
                                    LOG.debug("these two methods are identical." +
                                              "\nmethod0:" + interfaceType.getSimpleName() + "#" + m0.getName() + " returnType:" + m0.getReturnType().getSimpleName() +
                                              "\nmethod1:" + interfaceType.getSimpleName() + "#" + m1.getName() + " returnType:" + m1.getReturnType().getSimpleName());
                                }
                            }
                        });
                });
        }
        return methods;
    }
}
