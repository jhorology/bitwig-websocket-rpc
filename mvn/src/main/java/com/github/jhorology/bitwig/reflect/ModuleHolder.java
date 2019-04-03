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
import java.util.HashMap;
import java.util.Map;

// dependencies
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ModuleHolder<T> extends RegistryNode<T> {
    // Logger
    private static final Logger LOG = LoggerFactory.getLogger(ModuleHolder.class);

    protected final ReflectionRegistry owner;
    

    /**
     * the instance of interface of this module.
     */
    protected T nodeInstance;

    protected final Map<Class<?>, Integer> bankItemCounts;
    
    /**
     * Constructor
     * @param owner
     * @param moduleName
     * @param interfaceType
     * @param moduleInstance
     */
    ModuleHolder(ReflectionRegistry owner, String nodeName,
                 Class<T> nodeType, T nodeInstance) {
        super(nodeName, nodeType, EMPTY_PARAM_TYPES, null, 0);
        this.owner = owner;
        this.bankItemCounts = new HashMap<>();
        this.nodeInstance = nodeInstance;
    }


    /**
     * Register item count of specified Bank class.
     * @param bankType the type of bank.
     * @param count the count of bank items.
     * @return this instance.
     */
    public ModuleHolder<T> registerBankItemCount(Class<?> bankType, int count) {
        bankItemCounts.put(bankType, count);
        return this;
    }

    /**
     * Returns a bank item conut of specified bank item class.
     * @param bankItemType
     * @return 
     */
    int getBankItemCount(Class<?> bankType) {
        Integer count = bankItemCounts.get(bankType);
        if (count != null) {
            return count;
        }
        count = bankItemCounts.keySet().stream()
                .filter(c -> c.isAssignableFrom(bankType))
                .map(c -> bankItemCounts.get(c))
                .findFirst().orElse(null);
        if (count != null) {
            return count;
        }
        return 0;
    }
    
    /**
     * Returns an cached instance.<br>
     * can be nullable,
     * @param params
     * @return
     */
    @Override
    protected T getNodeInstance(Object[] params) {
        return nodeInstance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void clear() {
        bankItemCounts.clear();
    }
}
