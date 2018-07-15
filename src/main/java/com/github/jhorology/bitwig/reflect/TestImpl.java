package com.github.jhorology.bitwig.reflect;

import com.github.jhorology.bitwig.extension.Logger;

public class TestImpl implements Test {
    private static final Logger log = Logger.getLogger(TestImpl.class);
    
    @Override
    public String echo(String message) {
        log.info("echo(String message=" + message + ")");
        return message;
    }

    @Override
    public void doSomething() {
        log.info("doSomething()");
    }

    @Override
    public void doSomethingWithInt(int a) {
        log.info("doSomethingWithInt(" + a + ")");
    }

    @Override
    public void doSomethingWithIntPair(IntPair pair) {
        log.info("doSomethingWithIntPair({left:" + pair.getLeft() + ", right:" + pair.getRight() + "})");
    }
    
    @Override
    public void doSomethingWithGenericPair(GenericPair<Integer, Integer> pair) {
        log.info("doSomethingWithGenericPair({left:" + pair.getLeft() + ", right:" + pair.getRight() + "})");
    }
    
    @Override
    public int sum(IntPair pair) {
        log.info("sum({left:" + pair.getLeft() + ", right:" + pair.getRight() + "})");
        return sum(pair.getLeft(), pair.getRight());
    }
    
    @Override
    public int sum(int a, int b) {
        log.info("sum(String a=" + a + ", String b=" + b + ")");
        return a + b;
    }

    @Override
    public int sum(int a, int b, int c) {
        log.info("sum(String a=" + a + ", String b=" + b + ", String c=" + c + ")");
        return a + b + c;
    }
    
    @Override
    public String concat(GenericPair<String, String> pair) {
        log.info("concat({left:" + pair.getLeft() + ", right:" + pair.getRight() + "})");
        return concat(pair.getLeft(), pair.getRight());
    }
    
    @Override
    public String concat(String a, String b) {
        log.info("concat(String a=" + a + ", String b=" + b + ")");
        return a + b;
    }

    @Override
    public String repeat(GenericPair<String, Integer> pair) {
        log.info("repeat{left:" + pair.getLeft() + ", right:" + pair.getRight() + "})");
        return repeat(pair.getLeft(), pair.getRight());
    }
    
    @Override
    public String repeat(String s, int count) {
        log.info("repat(String s=" + s + ", int count=" + count + ")");
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
