package com.github.jhorology.bitwig.reflect;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.github.jhorology.bitwig.extension.Logger;

public class TestImpl implements Test {
    private static final Logger log = Logger.getLogger(TestImpl.class);
    
    @Override
    public void doSomething() {
        log.info("doSomething()");
    }

    @Override
    public void doSomething(int a) {
        log.info("doSomething(int a=" + a + ")");
    }

    @Override
    public void doSomething(int a, int b) {
        log.info("doSomething(int a=" + a + ", b=" + b + ")");
    }
    
    @Override
    public int sum(ImmutablePair<Integer, Integer> pair) {
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
    public String concat(ImmutablePair<String, String> pair) {
        log.info("concat({left:" + pair.getLeft() + ", right:" + pair.getRight() + "})");
        return concat(pair.getLeft(), pair.getRight());
    }
    
    @Override
    public String concat(String a, String b) {
        log.info("concat(String a=" + a + ", String b=" + b + ")");
        return a + b;
    }

    @Override
    public String repeat(ImmutablePair<String, Integer> pair) {
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
