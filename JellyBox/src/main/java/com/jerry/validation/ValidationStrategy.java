package com.jerry.validation;

/**
 * Created by jerryDev on 2016. 11. 23..
 */
@FunctionalInterface
public interface ValidationStrategy {
    boolean excute(Object object);
}
