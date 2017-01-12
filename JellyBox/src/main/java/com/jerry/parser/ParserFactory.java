package com.jerry.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.jerry.util.function.Parser;

public class ParserFactory {
	final static Map<String, Function<String, Parser>> supplierMap = new HashMap<>();
	public static final String PARSERNAME_IHS_LOG = "IHS_LOG";
	public static final String PARSERNAME_IHS_PLUGIN = "IHS_PLUGIN";
	public static final String PARSERNAME_WAS_LOG = "WAS_LOG";
	static {
		supplierMap.put( PARSERNAME_IHS_LOG, IHSLogDataLineParser::new);
		supplierMap.put( PARSERNAME_WAS_LOG, WASSystemOutLogLineParser::new);
		supplierMap.put( PARSERNAME_IHS_PLUGIN, IHSPlugInFileParser::new);
	}
	public static Parser create(String parserName, String filePath){
		Function<String, Parser> parserCreateFunction = supplierMap.get(parserName);
		if(parserCreateFunction!=null) return parserCreateFunction.apply(filePath);
		throw new IllegalArgumentException("No such parser ".concat(parserName));
	}
}
