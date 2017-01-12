package com.jerry.util.function;

/**
 * Created by jerryDev on 2016. 12. 28..
 */
public interface Parser {
    public Object parse(String target); // for lineParser
    public Object parse();              // for Parser
}
