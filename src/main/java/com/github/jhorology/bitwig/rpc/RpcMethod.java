package com.github.jhorology.bitwig.rpc;

import java.lang.reflect.Type;

public interface RpcMethod {
    Type[] getParamTypes();
    Object invoke(Object[] params) throws Exception;
}
