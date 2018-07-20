package com.github.jhorology.bitwig.rpc.test;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.jhorology.bitwig.extension.Logger;

public class TestImpl implements Test {
    private static final Logger log = Logger.getLogger(TestImpl.class);

    @Override
    public void nop() {
        log.info("void nop()");
    }
    
    @Override
    public void consume(int a) {
        log.info("void consume(int a=" + a +")");
    }
    
    @Override
    public void consume(double a, double b) {
        log.info("void consume(double a=" + a +", double b=" + b + ")");
    }
    
    @Override
    public void consume(Number a, Number b, Number c) {
        log.info("void consume(Number a=" + a + ", Number b=" + b + ", Number c=" + c + ")");
    }
    
    @Override
    public void consume(Number... args) {
        String params = Stream.of(args).map(String::valueOf).collect(Collectors.joining(", "));
        log.info("void consume(" + params + ")");
    }

    @Override
    public int sum(int a, int b) {
        int result = a + b;
        log.info("int sum(String a=" + a + ", String b=" + b + ") = " + result);
        return result;
    }
    
    @Override
    public int sum(int a, int b, int c) {
        int result = a + b + c;
        log.info("int sum(String a=" + a + ", String b=" + b + ", String c=" + c + ") = " + result);
        return result;
    }
    
    @Override
    public int sum(IntPair pair) {
        log.info("sum({left:" + pair.getLeft() + ", right:" + pair.getRight() + "})");
        return sum(pair.getLeft(), pair.getRight());
    }
    
    @Override
    public int sum(int... args) {
        int result = Arrays.stream(args).sum();
        String params = Arrays.stream(args)
            .mapToObj(String::valueOf)
            .collect(Collectors.joining(", "));
        log.info("void consume(" + params + ") = " + result);
        return result;
    }
    
    @Override
    public String concat(String a, String b) {
        String result = a + b;
        log.info("String concat(String a=" + a + ", String b=" + b + ") = " + result);
        return result;
    }
    
    @Override
    public String concat(GenericPair<String, String> pair) {
        String result = pair.getLeft() + pair.getRight();
        log.info("String concat({left:" + pair.getLeft() + ", right:" + pair.getRight() + "}) = " + result);
        return result;
    }
    
    public String concat(String... args) {
        String result = Stream.of(args)
            .collect(Collectors.joining());
        String params = Stream.of(args).collect(Collectors.joining(", "));
        log.info("String concat(" + params + ") = " + result);
        return result;
    }
    
    @Override
    public String repeat(String s, int count) {
        String result = IntStream.range(0, count).mapToObj(String::valueOf).collect(Collectors.joining());
        log.info("String repat(String s=" + s + ", int count=" + count + ") = " + result);
        return result;
    }

    @Override
    public String repeat(GenericPair<String, Integer> pair) {
        String result = IntStream.range(0, pair.getRight())
            .mapToObj(i -> pair.getLeft())
            .collect(Collectors.joining(", "));
        log.info("String repeat{left:" + pair.getLeft() + ", right:" + pair.getRight() + "}) = " + result);
        return repeat(pair.getLeft(), pair.getRight());
    }
}
