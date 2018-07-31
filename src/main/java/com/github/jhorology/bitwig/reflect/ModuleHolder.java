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

// jvm
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

// bitwig api
import com.bitwig.extension.controller.api.ControllerHost;

// dependencies
import org.java_websocket.WebSocket;

// source
import com.github.jhorology.bitwig.extension.Logger;
import com.github.jhorology.bitwig.rpc.RpcParamType;
import com.github.jhorology.bitwig.websocket.protocol.PushModel;

class ModuleHolder<T> {
    // Logger
    private static final Logger LOG = Logger.getLogger(ModuleHolder.class);

    protected final ReflectionRegistry owner;
    
    /**
     * the name of this module.
     */
    protected final String moduleName;
    
    /**
     * the interface typeof this module.
     */
    protected final Class<T> interfaceType;
    
    /**
     * the instance of interface of this module.
     */
    protected T moduleInstance;

    protected final Map<MethodIdentifier, MethodHolder> methods;
    protected final Map<String, EventHolder> events;
    
    /**
     * Constructor
     * @param owner
     * @param moduleName
     * @param interfaceType
     */
    ModuleHolder(ReflectionRegistry owner, String moduleName,
                 Class<T> interfaceType) {
        this(owner, moduleName, interfaceType, null);
    }
    
    /**
     * Constructor
     * @param owner
     * @param moduleName
     * @param interfaceType
     * @param moduleInstance
     */
    ModuleHolder(ReflectionRegistry owner, String moduleName,
                 Class<T> interfaceType, T moduleInstance) {
        this.owner = owner;
        this.moduleName = moduleName;
        this.interfaceType = interfaceType;
        this.events = new HashMap<>();
        this.methods = new HashMap<>();
        this.moduleInstance = moduleInstance;
    }

    /**
     * initialize this instance.
     */
    void init() {
        Arrays.stream(interfaceType.getMethods())
            .forEach(m -> registerMethod(m));
    }
    
    /**
     * cleanup this instance.
     */
    void clear() {
        events.values().stream().forEach(EventHolder::clear);
        events.clear();
        methods.values().stream().forEach(MethodHolder::clear);
        methods.clear();
    }

    
    String getModuleName() {
        return moduleName;
    }
    
    Map<MethodIdentifier, MethodHolder> getMethods() {
        return methods;
    }
    
    Map<String, EventHolder> getEvents() {
        return events;
    }

    MethodHolder getMethod(String name, RpcParamType[] paramTypes) {
        return methods.get(new MethodIdentifier(name, paramTypes));
    }

    EventHolder getEvent(String name) {
        return events.get(name);
    }
        
    Object getModuleInstance() {
        return moduleInstance;
    }
    
    void setModuleInstance(T moduleInstance) {
        this.moduleInstance = moduleInstance;
    }
    
    PushModel getPushModel() {
        return owner.getPushModel();
    }
    
    /**
     * Get an interface of ControllerHost.
     * @return
     */
    ControllerHost getHost() {
        return owner.getHost();
    }

    /**
     * clean up a client that has been disconnected.
     * @param client remote connextion.
     */
    void disconnect(WebSocket client) {
        events.values().stream()
            .forEach(e -> e.disconnect(client));
    }
    
    /**
     * create a report object for this class.
     * @return 
     */
    Object report() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("name", getModuleName());
        report.put("methods", reportMethods());
        report.put("events", reportEvents());
        return report;
    }
    
    /**
     * create a report object for list of methods of this class.
     * @return 
     */
    private List<Object> reportMethods() {
        List<Object> list = methods.keySet()
            .stream()
            .sorted()
            .map(key -> methods.get(key))
            .map(mh -> mh.reportRpcMethod())
            .collect(Collectors.toList());
       return list;
    }
    
    /**
     * create a report object for list of events of ModuleHolder.
     * @return 
     */
    private List<Object> reportEvents() {
        List<Object> list = events.keySet()
            .stream()
            .sorted()
            .map(key -> events.get(key))
            .map(eh -> eh.reportRpcEvent())
            .collect(Collectors.toList());
        return list;
    }
    
    protected void registerMethod(Method method) {
        registerMethod(method, "", null, 0);
    }
    
    private void registerMethod(Method method, String prefix, MethodHolder parentChain, int chainDepth) {
        // exclude method that is not usable for RPC
        if (!ReflectUtils.isUsableForRpcMethod(method)) {
            return;
        }
        if (chainDepth > 5) {
            LOG.error("\n##!!! Method chain depth are too long. Something is wrong!!"
                      + "\nmethod:" + prefix + "."+ method.getName());
            return;
        }
        // method's return type is inherited from Value interface.
        boolean isEvent = ReflectUtils.isUsableForRpcEvent(method);
        MethodHolder mh = isEvent
            ? new EventHolder(this, method, parentChain)
            : new MethodHolder(this, method, parentChain);
        String key = prefix + method.getName();
        methods.put(mh.getIdentifier(), mh);
        if (isEvent) {
            events.put(key, (EventHolder)mh);
        }
        // TODO  Protocol can serialize returnType or not?
        Class<?> returnType = method.getReturnType();
        // register method recursively
        if (ReflectUtils.isBitwigAPI(returnType) && !ReflectUtils.isDeprecated(returnType)) {
            Method[] methodsOfReturnType = returnType.getMethods();
            if (methodsOfReturnType.length == 0) {
                return;
            }
            // TODO
            if (ReflectUtils.hasAnyObjectOrArrayParameter(method)) {
                if (Logger.isWarnEnabled()) {
                    LOG.warn("[" + moduleName
                             + "." + key + "] has chain methods, However, it also has object or array parameters." );
                }
            }
            for(Method m : methodsOfReturnType) {
                registerMethod(m, key + ".", mh, chainDepth + 1);
            }
        }
    }
}
