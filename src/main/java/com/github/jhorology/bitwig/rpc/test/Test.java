package com.github.jhorology.bitwig.rpc.test;

import com.github.jhorology.bitwig.extension.Logger;

/**
 * RPC methods interfaceSimple POJO class for testing JSON deserialization
 */
public interface Test {
    
    static String hello() { return "hello"; }
    static String echo(String msg) { return msg; }
    
    void nop();
    void consume(int a);
    void consume(double a, double b);
    void consume(Number a, Number b, Number c);
    void consume(Number... args);

    int sum(int a, int b);
    int sum(int a, int b, int c);
    int sum(IntPair pair);
    int sum(int... args);
    
    String concat(GenericPair<String, String> pair);
    String concat(String a, String b);
    String concat(String... args);
    
    String repeat(String s, int count);
    String repeat(GenericPair<String, Integer> pair);
}
