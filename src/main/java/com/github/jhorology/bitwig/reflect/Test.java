package com.github.jhorology.bitwig.reflect;

import org.apache.commons.lang3.tuple.ImmutablePair;

public interface Test {
    public static Test create() {
        return new TestImpl();
    }
    public void doSomething();
    public void doSomething(int a);
    public void doSomething(int a, int b);
    public int sum(ImmutablePair<Integer, Integer> pair);
    public int sum(int a, int b);
    public int sum(int a, int b, int c);
    public String concat(ImmutablePair<String, String> pair);
    public String concat(String a, String b);
    public String repeat(ImmutablePair<String, Integer> pair);
    public String repeat(String s, int count);
}
