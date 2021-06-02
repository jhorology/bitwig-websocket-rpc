/*
 * Copyright (c) 2020 Masafumi Fujimaru
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

import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.Action;
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.Value;
import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.ext.ExtApiFactory;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.websocket.protocol.ProtocolHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An interface that defines registry node.
 */
abstract class RegistryNode {

  private static final Logger LOG = LoggerFactory.getLogger(RegistryNode.class);
  protected static final Object[] EMPTY_PARAMS = {};
  protected static final Type[] EMPTY_PARAM_TYPES = {};
  protected static final RpcParamType[] EMPTY_RPC_PARAM_TYPES = {};
  private static final int[] EMPTY_DIMENNSION = {};

  /**
   *  the name of this node.
   */
  protected final Config config;

  /**
   *  the name of this node.
   */
  protected final String nodeName;

  /**
   * the type of this node.
   */
  protected final Class<?> nodeType;

  /**
   * the type of managed instance.
   */
  protected final RpcParamType rpcNodeType;

  /**
   * the parent node of this node.
   */
  protected final RegistryNode parentNode;

  /**
   * the bank items size.
   * e.g)
   * createTrack with numSends = 4;
   * If method (that is managed by this node) is SendBank#getItemAt, this value is 4(numSends).
   */
  protected final int bankItemCount;

  /**
   * the absolute node name
   */
  protected final String absoluteName;

  /**
   * the dimension of bank items.
   * <pre>
   * e.g)
   * create trackBank with numTracks:8, numSends:4
   * dimension: []   [8]            [8]      [8]        [8,4]          [8,4]
   *       trackBank.getItemAt(int).foobar().sendBank().getItemAd(int).foobar()....
   * </pre>
   */
  protected final int[] bankDimension;

  /**
   * Constructor.
   * @param config
   * @param nodeName
   * @param nodeType
   * @param parentNode
   * @param bankItemCount
   */
  protected RegistryNode(
    Config config,
    String nodeName,
    Class<?> nodeType,
    RegistryNode parentNode,
    int bankItemCount
  ) {
    this.config = config;
    this.nodeName = nodeName;
    this.nodeType = nodeType;
    this.parentNode = parentNode;
    this.bankItemCount = bankItemCount;
    this.absoluteName =
      parentNode != null
        ? parentNode.absoluteName +
        ReflectionRegistry.NODE_DELIMITER +
        this.nodeName
        : this.nodeName;
    this.rpcNodeType = RpcParamType.of(nodeType, true);
    int[] dim = parentNode != null
      ? parentNode.bankDimension
      : EMPTY_DIMENNSION;
    this.bankDimension =
      bankItemCount > 0 ? ArrayUtils.add(dim, bankItemCount) : dim;
  }

  /**
   * Returns this node name.
   * @return
   */
  String getNodeName() {
    return nodeName;
  }

  /**
   * Returns this node name.
   * @return
   */
  String getAbsoluteName() {
    return absoluteName;
  }

  /**
   * Returns a type of managed instance.
   * @return
   */
  Class<?> getNodeType() {
    return nodeType;
  }

  /**
   * Returns a parent node.
   * @return
   */
  RegistryNode getParentNode() {
    return parentNode;
  }

  /**
   * Clear this instance;
   */
  abstract void clear();

  /**
   * Get a stream of member methods.
   * @param protocl
   * @return the stream of methods
   */
  Stream<Method> getMethodStream(ProtocolHandler protocol) {
    Stream<Method> stream = getMethodStream(nodeType, protocol);
    Class<?> extApi = ExtApiFactory.getExtApiInterface(config, nodeType);
    if (extApi != null) {
      stream = Stream.concat(stream, getMethodStream(extApi, protocol));
    }
    return stream;
  }

  private Stream<Method> getMethodStream(
    Class<?> api,
    ProtocolHandler protocol
  ) {
    // may be a proxy class that implements extended API.
    Stream<Method> methodStream = Stream
      .of(api.getMethods())
      .filter(m -> !ReflectUtils.isDeprecated(m))
      .filter(m -> !ReflectUtils.isDeprecated(m.getReturnType()))
      .filter(m -> !ReflectUtils.hasAnyBitwigObjectParameter(m))
      .filter(m -> !ReflectUtils.hasAnyCallbackParameter(m))
      .filter(m -> !ReflectUtils.isModuleFactory(m))
      .filter(
        m -> {
          if (LOG.isDebugEnabled() && m.isBridge()) {
            LOG.debug(
              "node[{}] member method[{}] is bridge method",
              absoluteName,
              ReflectUtils.javaExpression(m)
            );
          }
          return !m.isBridge();
        }
      );

    // markInterested is probably same as Subscribable#subscibe()
    if (Value.class.isAssignableFrom(nodeType)) {
      methodStream =
        methodStream.filter(m -> !("markInterested".equals(m.getName())));
    }

    // TODO other Host methods ?
    if (ControllerHost.class.isAssignableFrom(nodeType)) {
      methodStream =
        methodStream.filter(
          m ->
            "getNotificationSettings".equals(m.getName()) ||
            "showPopupNotification".equals(m.getName())
        );
    }

    // MasterTrack#sendBank() is useless, and host throw exception.
    if (MasterTrack.class.isAssignableFrom(nodeType)) {
      methodStream = methodStream.filter(m -> !"sendBank".equals(m.getName()));
    }

    // SceneBank is implemented Bank<Scene>, should use Bank#getItemAt(int) instead of SceneBank#getScene(int)
    if (SceneBank.class.isAssignableFrom(nodeType)) {
      methodStream = methodStream.filter(m -> !"getScene".equals(m.getName()));
    }
    // DeviceBank is implemented Bank<Device>, should use Bank#getItemAt(int) instead of DeviceBank#getDevice(int)
    if (DeviceBank.class.isAssignableFrom(nodeType)) {
      methodStream = methodStream.filter(m -> !"getDevice".equals(m.getName()));
    }
    // Parameter#setLabel(String) can only be called during driver initialization
    if (Parameter.class.isAssignableFrom(nodeType)) {
      methodStream = methodStream.filter(m -> !"setLabel".equals(m.getName()));
    }

    // since bitwig 3.1

    //#if bitwig.extension.api.version >= 10
    methodStream =
      methodStream.filter(
        m -> !ReflectUtils.isPureHardwareBindable(m.getReturnType(), protocol)
      );
    //#endif

    // TODO since bitwig 3.1, API methods are annotated with @OscMethod, @OscNode
    // Will OSC server be released soon ?

    // Color class is difficult to use via remote. ColorValue class is enough.
    methodStream =
      methodStream.filter(m -> !Color.class.equals(m.getReturnType()));

    // move to ApplicationExt#observerdAction
    if (Action.class.isAssignableFrom(nodeType)) {
      methodStream = methodStream.filter(m -> !"isEnabled".equals(m.getName()));
    }
    return methodStream;
  }
}
