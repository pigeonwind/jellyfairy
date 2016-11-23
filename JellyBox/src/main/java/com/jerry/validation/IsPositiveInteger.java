package com.jerry.validation;

/**
 * Created by jerryDev on 2016. 11. 23..
 */
public class IsPositiveInteger implements ValidationStrategy {
    @Override
    public boolean excute(Object object) {
        return (Integer)object>=0;
    }
}
