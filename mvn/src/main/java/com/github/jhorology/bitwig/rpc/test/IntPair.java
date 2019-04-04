package com.github.jhorology.bitwig.rpc.test;

// dependencies
import com.google.gson.annotations.Expose;

/**
 * Simple POJO class for testing JSON deserialization
 */
public class IntPair {
    @Expose
    private int left;
    @Expose
    private int right;

    public IntPair() {
    }

    public IntPair(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }
    
    public void setLeft(int left) {
        this.left = left;
    }
    
    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }
}
