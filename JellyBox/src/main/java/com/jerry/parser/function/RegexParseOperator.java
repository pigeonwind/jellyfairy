package com.jerry.parser.function;

/**
 * Created by jerryDev on 2016. 12. 28..
 */
@FunctionalInterface
public interface RegexParseOperator {
    Object parse(String target, Object regex);
}
