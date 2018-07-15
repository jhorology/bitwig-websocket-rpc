package com.github.jhorology.bitwig.reflect;

import com.google.gson.annotations.Expose;

/**
 * Generic POJO class for testing JSON deserialization
 */
public class GenericPair<L, R> {
    @Expose
    private L left;
    @Expose
    private R right;

    public GenericPair() {
    }

    public GenericPair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public L getLeft() {
        return left;
    }
    
    public void setLeft(L left) {
        this.left = left;
    }
    
    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }
}
