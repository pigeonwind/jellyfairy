package com.jerry.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.jerry.util.function.StringParser;

public class ParserFactory {
	final static Map<String, Function<String, StringParser>> supplierMap = new HashMap<>();
	static {
		supplierMap.put("IHS", IHSLogDataParser::new);
		supplierMap.put("WAS", WASSystemOutLogParser::new);
	}
	
	public static StringParser create(String parserName,String filePath){
		Function<String, StringParser> parserCreateFunction = supplierMap.get(parserName);
		if(parserCreateFunction!=null) return parserCreateFunction.apply(filePath);
		throw new IllegalArgumentException("No such parser ".concat(parserName));
	}
}
