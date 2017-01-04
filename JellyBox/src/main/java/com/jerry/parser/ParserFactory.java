package com.jerry.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.jerry.util.function.StringParser;

public class ParserFactory {
	final static Map<String, Function<String, StringParser>> supplierMap = new HashMap<>();
	public static final String PARSERNAME_IHS = "IHS";
	public static final String PARSERNAME_WAS = "WAS";
	static {
		supplierMap.put(PARSERNAME_IHS, IHSLogDataParser::new);
		supplierMap.put(PARSERNAME_WAS, WASSystemOutLogParser::new);
	}
	public static StringParser create(String parserName,String filePath){
		Function<String, StringParser> parserCreateFunction = supplierMap.get(parserName);
		if(parserCreateFunction!=null) return parserCreateFunction.apply(filePath);
		throw new IllegalArgumentException("No such parser ".concat(parserName));
	}
}
