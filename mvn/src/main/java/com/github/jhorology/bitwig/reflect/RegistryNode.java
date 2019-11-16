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
import com.bitwig.extension.api.Color;
import com.bitwig.extension.controller.api.Action;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// bitwig API
import com.bitwig.extension.controller.api.ControllerHost;
import com.bitwig.extension.controller.api.DeviceBank;
import com.bitwig.extension.controller.api.EnumValue;
import com.bitwig.extension.controller.api.MasterTrack;
import com.bitwig.extension.controller.api.ObjectArrayValue;
import com.bitwig.extension.controller.api.Parameter;
import com.bitwig.extension.controller.api.ParameterBank;
import com.bitwig.extension.controller.api.RemoteControl;
import com.bitwig.extension.controller.api.RemoteControlsPage;
import com.bitwig.extension.controller.api.Scene;
import com.bitwig.extension.controller.api.SceneBank;
import com.bitwig.extension.controller.api.StringArrayValue;
import com.bitwig.extension.controller.api.StringValue;
import com.bitwig.extension.controller.api.TimeSignatureValue;
import com.bitwig.extension.controller.api.Value;

// provided dependencies
import org.apache.commons.lang3.ArrayUtils;


// dependencies
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// source
import com.github.jhorology.bitwig.Config;
import com.github.jhorology.bitwig.ext.ExtApiFactory;
import com.github.jhorology.bitwig.rpc.RpcParamType;

/**
 * An interface that defines registry node.
 */
abstract class RegistryNode {
    private static final Logger LOG = LoggerFactory.getLogger(RegistryNode.class);
    protected static final Object[] EMPTY_PARAMS = {};
    protected static final Type[] EMPTY_PARAM_TYPES = {};
    protected static final RpcParamType[] EMPTY_RPC_PARAM_TYPES = {};
    private final static int[] EMPTY_DIMENNSION = {};

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
    protected RegistryNode(Config config, String nodeName, Class<?> nodeType, RegistryNode parentNode, int bankItemCount) {
        this.config = config;
        this.nodeName = config.useAbbreviatedMethodNames() ? AbbrevDict.abbrev(nodeName) : nodeName;
        this.nodeType = nodeType;
        this.parentNode = parentNode;
        this.bankItemCount = bankItemCount;
        this.absoluteName = parentNode != null
            ? parentNode.absoluteName + ReflectionRegistry.NODE_DELIMITER + this.nodeName
            : this.nodeName;
        this.rpcNodeType = RpcParamType.of(nodeType);
        int[] dim = parentNode != null
            ? parentNode.bankDimension
            : EMPTY_DIMENNSION;
        this.bankDimension =  bankItemCount > 0
            ? ArrayUtils.add(dim, bankItemCount)
            : dim;
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
     * Clear this instance;
     */
    abstract void clear();

    /**
     * Get a list of member methods.
     * @return the list of methods
     */
    List<Method> getMethods() {
        List<Method> list = getMethods(nodeType);
        Class<?> extApi = ExtApiFactory.getExtApiInterface(config, nodeType);
        if (extApi != null) {
            list.addAll(getMethods(extApi));
        }
        return list;
    }
    
    private List<Method> getMethods(Class<?> api) {
        // may be a proxy class that implements extended API.
        Stream<Method> methodsStream = Arrays.asList(api.getMethods())
            .stream()
            .filter(m -> !ReflectUtils.isDeprecated(m))
            .filter(m -> !ReflectUtils.isDeprecated(m.getReturnType()))
            .filter(m -> !ReflectUtils.hasAnyBitwigObjectParameter(m))
            .filter(m -> !ReflectUtils.hasAnyCallbackParameter(m))
            .filter(m -> !ReflectUtils.isModuleFactory(m));

        // markInterested is probably same as Subscribable#subscibe()
        if (Value.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("markInterested".equals(m.getName())));
        }

        // TODO limit host methods
        if (ControllerHost.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> "getNotificationSettings".equals(m.getName()) ||
                        "showPopupNotification".equals(m.getName()));
        }


        // WTF! fixing one by one

        // StringArrayValue has two get() methods.  Object[] get()/String[] get()
        if (StringArrayValue.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("get".equals(m.getName())
                               && Object[].class.equals(m.getReturnType())));
        }
        // RemoteControlsPage/ParameterBank has duplicated getParameter method
        // RemoteControl getParameter(int)
        // Paramater getParameter(int)
        if (RemoteControlsPage.class.isAssignableFrom(nodeType)
            && ParameterBank.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("getParameter".equals(m.getName())
                               && Parameter.class.equals(m.getReturnType())));
        }
        // RemoteContrrol has two name() methods. returnType: StringValue/SettableStringValue
        if (RemoteControl.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("name".equals(m.getName())
                               && StringValue.class.equals(m.getReturnType())));
        }
        // MasterTrack#sendBank() is useless, and host throw exception.
        if (MasterTrack.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !"sendBank".equals(m.getName()));
        }

        // Scene has two name() methods. returnType: StringValue/SettableStringValue
        if (Scene.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("name".equals(m.getName())
                               && StringValue.class.equals(m.getReturnType())));
        }
        // SceneBank is implemented Bank<Scene>, should use Bank#getItemAt(int) instead of SceneBank#getScene(int)
        if (SceneBank.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !"getScene".equals(m.getName()));
        }
        // DeviceBank is implemented Bank<Device>, should use Bank#getItemAt(int) instead of DeviceBank#getDevice(int)
        if (DeviceBank.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !"getDevice".equals(m.getName()));
        }
        
        // since bitwig 3.1
        if (StringValue.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("get".equals(m.getName())
                               && Object.class.equals(m.getReturnType())));
        }
        if (EnumValue.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("get".equals(m.getName())
                               && Object.class.equals(m.getReturnType())));
        }
        if (ObjectArrayValue.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("get".equals(m.getName())
                               && Object.class.equals(m.getReturnType())));
        }
        if (TimeSignatureValue.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !("get".equals(m.getName())
                               && Object.class.equals(m.getReturnType())));
        }

        
        // Color class is difficult to use via remote. ColorValue class is enough. 
        methodsStream = methodsStream
            .filter(m -> !Color.class.equals(m.getReturnType()));

        // TODO since API10 uhmmmm..., but I need this.
        if (Action.class.isAssignableFrom(nodeType)) {
            methodsStream = methodsStream
                .filter(m -> !"isEnabled".equals(m.getName()));
        }
        
        List<Method> methods = methodsStream.collect(Collectors.toList());
        // for debug
        if (LOG.isDebugEnabled()) {
            methods.stream().forEach(m0 -> {
                    methods.stream().forEach(m1 -> {
                            if (m0 != m1) {
                                MethodIdentifier m0id = new MethodIdentifier(m0.getName(), m0.getGenericParameterTypes());
                                MethodIdentifier m1id = new MethodIdentifier(m1.getName(), m1.getGenericParameterTypes());
                                if (m0id.equals(m1id)) {
                                    LOG.debug("node:" + absoluteName + " has identical methods." +
                                              "\n0: " + m0.getReturnType().getSimpleName() + " " + m0.getDeclaringClass().getSimpleName() + "#" + m0.getName() +
                                              "\n1: " + m1.getReturnType().getSimpleName() + " " + m1.getDeclaringClass().getSimpleName() + "#" + m1.getName());
                                }
                            }
                        });
                });
        }
        return methods;
    }
}
