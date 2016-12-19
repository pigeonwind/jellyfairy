package com.jerry.pattern;

import java.util.function.UnaryOperator;

/**
 * Created by jerryDev on 2016. 12. 15..
 */

public interface StringExtractor {
    public String extract(String targetString , UnaryOperator<String> preProcessor, UnaryOperator<String> mainProcesssor, UnaryOperator<String> postProcessor);
}
