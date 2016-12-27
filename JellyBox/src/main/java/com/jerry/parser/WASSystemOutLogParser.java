package com.jerry.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public class WASSystemOutLogParser extends DefaultParser{
	private String target;
	private Map<String, Object> resultMap;
	private UnaryOperator<String> REMOVE_SPACECHAR_OPERATOR=(String line) -> line.replace(" ", "");
	
	public WASSystemOutLogParser(String target) {
		this.target = target;
		this.resultMap = new HashMap<>();
	}
	@Override
	public Object parse() {
		resultMap.put("date", extractDate(target));
		resultMap.put("time", extractTime(target));
		resultMap.put("threadName", extractThreadName(target));
		resultMap.put("processComponent", extractProcessComponent(target));
		resultMap.put("level", extractLevel(target));
		resultMap.put("code", extractCode(target));
		resultMap.put("content", extractContent(target));
		return resultMap;
	}
	private final String LEVEL="([W|I|E|A]{1})";
	private final String LEVEL_PATTERN=SPACE+LEVEL+SPACE;
	private final String EXCLUDELINE_PATTERN=BRACKET_PATTERN+ANY+LEVEL_PATTERN;
	private Object extractContent(String target) {
		UnaryOperator<String> preProcessor = NO_ACTION_OPERATOR;
		UnaryOperator<String> mainProcessor = (String line) ->(String) EXCLUDE_PARSER.parse(line, EXCLUDELINE_PATTERN);
		UnaryOperator<String> postProcessor = NO_ACTION_OPERATOR;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private final String CODE_PATTERN="([A-Z]{4}|[A-Z]{5})([0-9]{4}|[0-9]{5})"+LEVEL;
	private Object extractCode(String target) {
		UnaryOperator<String> preProcessor = NO_ACTION_OPERATOR;
		UnaryOperator<String> mainProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, CODE_PATTERN));
		UnaryOperator<String> postProcessor = (String line)->line.replace(":", "");;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private Object extractLevel(String target) {
		UnaryOperator<String> preProcessor = NO_ACTION_OPERATOR;
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, LEVEL_PATTERN);
		UnaryOperator<String> postProcessor = REMOVE_SPACECHAR_OPERATOR;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private final String PRE_PROCESS_COMPONENT_PATTERN=THREAD_NAME_PATTERN+ANY+SPACE;
	private final String PROCESS_COMPONENT_PATTERN =SPACE+ANY+SPACE; 
	private Object extractProcessComponent(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, PRE_PROCESS_COMPONENT_PATTERN)).replace("] ", "");;
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, PROCESS_COMPONENT_PATTERN);
		UnaryOperator<String> postProcessor =REMOVE_SPACECHAR_OPERATOR;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	
	final static String THREAD_NAME_PATTERN = RIGHTBRACKET+SPACE+ANY+SPACE;
	private Object extractThreadName(String target) {
		UnaryOperator<String> preProcessor = NO_ACTION_OPERATOR;
		UnaryOperator<String> mainProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, THREAD_NAME_PATTERN));
		UnaryOperator<String> postProcessor = (String line)-> line.replace("] ", "");;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private static final String WASSYSTEMOUTLOG_TIMESTAMP_PATTERN="([0-9]{1}|[0-9]{2}):([0-9]{2}):([0-9]{2}):([0-9]{3})";
	private Object extractTime(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, BRACKET_PATTERN)).replace("[", "");
		UnaryOperator<String> mainProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, WASSYSTEMOUTLOG_TIMESTAMP_PATTERN));
		UnaryOperator<String> postProcessor = NO_ACTION_OPERATOR;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private String WASSYSTEMOUTLOG_FIXED_LENGTH_PATTERN="(([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2}) )";
	private Object extractDate(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, BRACKET_PATTERN)).replace("[", "");
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, WASSYSTEMOUTLOG_FIXED_LENGTH_PATTERN);
		UnaryOperator<String> postProcessor = REMOVE_SPACECHAR_OPERATOR;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	
}
