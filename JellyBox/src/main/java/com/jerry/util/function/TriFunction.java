package com.jerry.util.function;

/**
 * Created by jerryDev on 2017. 1. 7..
 */
@FunctionalInterface
public interface TriFunction<T,U,V,R> {
    R apply(T arg1,U arg2,V arg3);
}
