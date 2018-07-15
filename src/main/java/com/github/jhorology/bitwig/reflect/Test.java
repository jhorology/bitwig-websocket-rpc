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
    public void notifies();
    public void notifies(int a);
    public void notifiesWithIntPair(IntPair pair);
    public void notifiesWithGenericPair(GenericPair<Integer, Integer> pair);
    public int sum(IntPair pair);
    public int sum(int a, int b);
    public int sum(int a, int b, int c);
    public String concat(GenericPair<String, String> pair);
    public String concat(String a, String b);
    public String repeat(GenericPair<String, Integer> pair);
    public String repeat(String s, int count);

    public static String hello() {
        return "hello";
    }
    
    public static void staticNotifies() {
        Logger.getLogger(Test.class).info("staticNotifies()");
    }
    
    public static void staticNotifies(int a) {
        Logger.getLogger(Test.class).info("staticNotifiesWithInt(" + a + ")");
    }
    
    public static String staticRepeat(String s, int count) {
        Logger.getLogger(Test.class).info("staticRepeat(String s=" + s + ", int count=" + count + ")");
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
