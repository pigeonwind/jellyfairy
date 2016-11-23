package com.jerry.validation;

/**
 * Created by jerryDev on 2016. 11. 23..
 */
public class Validator {
    private final ValidationStrategy strategy;
    public Validator(ValidationStrategy strategy) {
        this.strategy = strategy;
    }
    public boolean validate(Object target){return strategy.excute( target );}
}
