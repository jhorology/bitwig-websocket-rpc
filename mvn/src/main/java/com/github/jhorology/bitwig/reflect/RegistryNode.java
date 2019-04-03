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
import java.lang.reflect.Type;
import java.util.stream.Stream;

// provided dependencies
import org.apache.commons.lang3.ArrayUtils;

// source
import com.github.jhorology.bitwig.rpc.RpcParamType;

/**
 * An interface that defines registry node.
 * @param <T>
 */
abstract class RegistryNode<T> {
    protected static final Object[] EMPTY_PARAMS = {};
    protected static final Type[] EMPTY_PARAM_TYPES = {};
    private final static int[] EMPTY_DIMENNSION = {};
    
    /**
     *  the name of this node.
     */
    protected final String nodeName;
    
    /**
     * the type of managed instance. 
     */
    protected final Class<T> nodeType;
    
    /**
     * the type of managed instance. 
     */
    protected final RpcParamType rpcNodeType;
    
    /**
     * the parameter types for managed method.
     */
    protected final Type[] nodeParamTypes;
    
    /**
     * the parent node of this node.
     */
    protected final RegistryNode<?> parentNode;
    
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
     * the RPC paramater types for managed method.
     */
    protected final RpcParamType[] nodeRpcParamTypes;
    
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
     * @param nodeName
     * @param nodeType
     * @param nodeParamTypes
     * @param parentNode 
     * @param bankItemCount
     */
    protected RegistryNode(String nodeName, Class<T> nodeType,  Type[] nodeParamTypes, RegistryNode<?> parentNode, int bankItemCount) {
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.parentNode = parentNode;
        this.nodeParamTypes = nodeParamTypes;
        this.bankItemCount = bankItemCount;
        this.absoluteName = parentNode != null
            ? parentNode.absoluteName + ReflectionRegistry.NODE_DELIMITER + nodeName
            : nodeName;
        this.nodeRpcParamTypes = Stream.of(nodeParamTypes)
            .map(RpcParamType::of)
            .toArray(size -> new RpcParamType[size]);
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
    Class<T> getNodeType() {
        return nodeType;
    }
    
    /**
     * 
     * @return 
     */
    RpcParamType[] getNodeRpcParamTypes() {
        return nodeRpcParamTypes;
    }
    
    /**
     * Returns an cached managed instance.<br>
     * @param params
     * @return 
     */
    protected abstract T getNodeInstance(Object[] params) throws Exception;

    /**
     * Clears this instance;
     */
    abstract void clear();
}
