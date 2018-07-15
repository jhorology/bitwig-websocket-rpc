package com.github.jhorology.bitwig.reflect;

import com.github.jhorology.bitwig.extension.Logger;

/**
 * RPC methods interfaceSimple POJO class for testing JSON deserialization
 */
public interface Test {
    public static Test create() {
        return new TestImpl();
    }
    public String echo(String message);
    public void doSomething();
    public void doSomethingWithInt(int a);
    public void doSomethingWithIntPair(IntPair pair);
    public void doSomethingWithGenericPair(GenericPair<Integer, Integer> pair);
    public int sum(IntPair pair);
    public int sum(int a, int b);
    public int sum(int a, int b, int c);
    public String concat(GenericPair<String, String> pair);
    public String concat(String a, String b);
    public String repeat(GenericPair<String, Integer> pair);
    public String repeat(String s, int count);

    public static void doSomethingStatic() {
        Logger.getLogger(Test.class).info("doSomethingStatic()");
    }
    
    public static void doSomethingStaticWithInt(int a) {
        Logger.getLogger(Test.class).info("doSomethingWithInt(" + a + ")");
    }
    
    public static String repeatStatic(String s, int count) {
        Logger.getLogger(Test.class).info("repatStatic(String s=" + s + ", int count=" + count + ")");
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
