package com.jerry.validation;

/**
 * Created by jerryDev on 2016. 11. 23..
 */
public class IsInteger implements ValidationStrategy {
    @Override
    public boolean excute(Object object) {
        return object instanceof Integer;
    }
}
