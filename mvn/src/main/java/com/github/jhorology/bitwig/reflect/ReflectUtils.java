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

import com.bitwig.extension.callback.Callback;
import com.bitwig.extension.controller.api.Bank;
// import com.bitwig.extension.controller.api.BrowserFilterColumn;
// import com.bitwig.extension.controller.api.BrowserFilterColumnBank;
import com.bitwig.extension.controller.api.BrowserItem;
import com.bitwig.extension.controller.api.BrowserItemBank;
// import com.bitwig.extension.controller.api.BrowsingSessionBank;
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
//#if bitwig.extension.api.version >= 10
import com.bitwig.extension.controller.api.HardwareBindable;
//#endif
// import com.bitwig.extension.controller.api.GenericBrowsingSession;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.ParameterBank;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.RemoteControlsPage;
import com.bitwig.extension.controller.api.Scene;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.Send;
import com.bitwig.extension.controller.api.SendBank;
import com.bitwig.extension.controller.api.Track;
import com.bitwig.extension.controller.api.TrackBank;
import com.bitwig.extension.controller.api.Value;
import com.github.jhorology.bitwig.logging.LoggerFactory;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;

/**
 * utility class
 */
@SuppressWarnings("UseSpecificCatch")
public class ReflectUtils {

  private static final Logger LOG = LoggerFactory.getLogger(ReflectUtils.class);

  /**
   * empty array of Object.
   */
  public static final Object[] EMPTY_ARRAY = {};
  /**
   * empty array of Class<?>.
   */
  public static final Class<?>[] EMPTY_CLASS_ARRAY = {};
  public static final Class<?>[] BANK_METHOD_PARAM_TYPES = { int.class };
  private static final Map<Class<?>, Class<?>> BANK_ITEM_TYPES;
  private static final Map<Class<?>, Class<?>> SEMI_BANK_ITEM_TYPES;
  private static final Set<Method> BANK_METHODS;

  static {
    BANK_ITEM_TYPES = new LinkedHashMap<>();
    SEMI_BANK_ITEM_TYPES = new LinkedHashMap<>();
    BANK_METHODS = new LinkedHashSet<>();
    try {
      // All known sub-interfaces of Bank
      // ItemType can't be resolved at runtime.

      // public interface Bank<ItemType extends ObjectProxy> extends ObjectProxy, Scrollable
      // BANK_ITEM_TYPES.put(BrowsingSessionBank.class, GenericBrowsingSession.class);
      // BANK_ITEM_TYPES.put(BrowserFilterColumnBank.class, BrowserFilterColumn.class);
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
      BANK_ITEM_TYPES.put(
        ClipLauncherSlotOrSceneBank.class,
        ClipLauncherSlotOrScene.class
      );
      // not implemented Bank
      SEMI_BANK_ITEM_TYPES.put(RemoteControlsPage.class, RemoteControl.class);
      SEMI_BANK_ITEM_TYPES.put(ParameterBank.class, Parameter.class);

      // bank methods
      BANK_METHODS.add(
        Bank.class.getMethod("getItemAt", BANK_METHOD_PARAM_TYPES)
      );
      BANK_METHODS.add(
        RemoteControlsPage.class.getMethod(
            "getParameter",
            BANK_METHOD_PARAM_TYPES
          )
      );
      BANK_METHODS.add(
        ParameterBank.class.getMethod("getParameter", BANK_METHOD_PARAM_TYPES)
      );
      // TODO need more methods...

    } catch (Exception ex) {}
  }

  public static boolean isVarargs(Method method) {
    return isVarargs(method.getGenericParameterTypes());
  }

  public static boolean isVarargs(Type[] paramTypes) {
    if (paramTypes.length == 1) {
      Type t = paramTypes[0];
      return (
        (t instanceof Class && ((Class<?>) t).isArray()) ||
        t instanceof GenericArrayType
      );
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
      if (!expectedType.isArray()) {
        boolean allSameType = Stream
          .of(rpcParamTypes)
          .allMatch(t -> (t == expectedType));
        if (allSameType) {
          RpcParamType arrayType = expectedType.getArrayType();
          return new RpcParamType[] { arrayType };
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
   * return interface type is extended API or not.
   * @param interfaceType
   * @return
   */
  public static boolean isExtAPI(Class<?> interfaceType) {
    return interfaceType
      .getName()
      .startsWith("com.github.jhorology.bitwig.ext.api.");
  }

  /**
   * return interface type is Bitwig Controller API or not.
   * @param interfaceType
   * @return
   */
  public static boolean isBitwigControllerAPI(Class<?> interfaceType) {
    return interfaceType
      .getName()
      .startsWith("com.bitwig.extension.controller.api.");
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
    return Stream.of(types).anyMatch(Callback.class::isAssignableFrom);
  }

  /**
   * Returns specified method has any object parameter or not.
   * @param method
   * @return
   */
  public static boolean hasAnyBitwigObjectParameter(Method method) {
    Class<?>[] types = method.getParameterTypes();
    if (types.length == 0) return false;
    return Stream
      .of(types)
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
    Type[] types = method.getGenericParameterTypes();
    if (types.length == 0) return false;
    return Stream
      .of(types)
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
    // TODO need to investigate core modules that can be instantiated at only within init.
    // this is enough for now.
    boolean result =
      method.getName().startsWith("create") &&
      !isBitwigValue(method.getReturnType()) &&
      isBitwigAPI(method.getReturnType());
    if (result && LOG.isTraceEnabled()) {
      LOG.trace(
        "method[{}#{}] has been ignored as factory method for core module.",
        method.getDeclaringClass().getSimpleName(),
        method.getName()
      );
    }
    return result;
  }

  /**
   * Secified interfaceType is implemented Bank or not ?
   * @param interfaceType
   * @return
   */
  public static boolean isBank(Class<?> interfaceType) {
    return (
      Bank.class.isAssignableFrom(interfaceType) ||
      SEMI_BANK_ITEM_TYPES
        .keySet()
        .stream()
        .anyMatch(c -> c.isAssignableFrom(interfaceType))
    );
  }

  /**
   * Secified interfaceType is implemented Bank or not ?
   * @param method
   * @return
   */
  public static boolean isBankMethod(Method method) {
    return BANK_METHODS.contains(method);
  }

  /**
   * Returns a bank item type of specified bank type.
   * @param bankType the type of bank.
   * @return the type of bank item.
   */
  @SuppressWarnings("unchecked")
  public static Class<?> getBankItemType(Class<?> bankType) {
    Class<?> bankItemType;
    if (Bank.class.isAssignableFrom(bankType)) {
      bankItemType = BANK_ITEM_TYPES.get((Class<? extends Bank<?>>) bankType);
      if (bankItemType != null) {
        return bankItemType;
      }
    }
    bankItemType = SEMI_BANK_ITEM_TYPES.get(bankType);
    if (bankItemType != null) {
      return bankItemType;
    }
    bankItemType =
      BANK_ITEM_TYPES
        .keySet()
        .stream()
        .filter(c -> c.isAssignableFrom(bankType))
        .map(BANK_ITEM_TYPES::get)
        .findFirst()
        .orElse(null);
    if (bankItemType != null) {
      return bankItemType;
    }
    bankItemType =
      SEMI_BANK_ITEM_TYPES
        .keySet()
        .stream()
        .filter(c -> c.isAssignableFrom(bankType))
        .map(SEMI_BANK_ITEM_TYPES::get)
        .findFirst()
        .orElse(null);
    return bankItemType;
  }

  //#if bitwig.extension.api.version >= 10
  /**
   * Returns specified type is pure HardwareBindable class.or not
   * @param type
   * @param protocol
   * @return
   */
  public static boolean isPureHardwareBindable(
    Class<?> type,
    ProtocolHandler protocol
  ) {
    if (
      HardwareBindable.class.isAssignableFrom(type) &&
      !(
        isBitwigValue(type) ||
        isBank(type) ||
        protocol.isSerializableBitwigType(type)
      )
    ) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("class[{}] is Pure HardwareBindable.", type);
      }
      return true;
    }
    return false;
  }

  //#endif

  /**
   * Returns a java API expression of specified method.
   * @param method
   * @return
   */
  public static String javaExpression(Method method) {
    return javaExpression(method, true, true);
  }

  /**
   * Returns a java API expression of specified method.
   * @param method
   * @param includeReturnType
   * @param includeDeclaringClass
   * @return
   */
  public static String javaExpression(
    Method method,
    boolean includeReturnType,
    boolean includeDeclaringClass
  ) {
    StringBuilder sb = new StringBuilder();
    if (includeReturnType) {
      sb.append(method.getReturnType().getSimpleName());
      sb.append(" ");
    }
    if (includeDeclaringClass) {
      sb.append(method.getDeclaringClass().getSimpleName());
      sb.append("#");
    }
    sb.append(method.getName());
    sb.append("(");
    sb.append(
      Stream
        .of(method.getGenericParameterTypes())
        .map(t -> t.getTypeName())
        .collect(Collectors.joining(", "))
    );
    sb.append(")");
    return sb.toString();
  }
}
