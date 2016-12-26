package com.jerry.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jerry.pattern.Parser;
import com.jerry.pattern.RegexParser;
import com.jerry.pattern.StringExtractor;

public class IHSLogDataParser extends DefaultParser{
	private String target;
	private Map<String, Object> resultMap;
	
	//pattern define
	public IHSLogDataParser(String target) {
		this.target = target;
		this.resultMap = new HashMap<>();
	}
	
	@Override
	public Object parse() {
		resultMap.put("clientIp", extractClientIp(target));
		resultMap.put("requestCompleteDate", extractRequestCompleteDate(target));
		resultMap.put("requestCompleteTime", extractRequestCompleteTime(target));
		resultMap.put("requestMethod", extractRequestMethod(target));
		resultMap.put("requestLine", extractRequestLine(target));
		resultMap.put("status", extractStatus(target));
		return resultMap;
	}
	
	private static final String STATUS_PATTERN =QUOTATION+SPACE+ANY+SPACE;
	private Object extractStatus(String target) {
		UnaryOperator<String> preProcessor = NO_ACTION_OPERATOR;
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, STATUS_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(2,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}

	private Object extractRequestLine(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse( line, QUOTATION_PATTERN));
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, SPACE_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(1,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private final static String QUOTATION_PATTERN = QUOTATION+ANY+QUOTATION;
	private final static String REQUEST_METHOD_PATTERN =QUOTATION+ANY+SPACE;
	private Object extractRequestMethod(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse( line, QUOTATION_PATTERN));
		UnaryOperator<String> mainProcessor = (String line) -> (String) REGEX_PARSER.parse(line, REQUEST_METHOD_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(1,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private static final String REQUEST_COMPLETE_TIME_PATTERN =COLLON+ANY+SPACE;
	private Object extractRequestCompleteTime(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse( line,BRACKET_PATTERN ));
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, REQUEST_COMPLETE_TIME_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(1,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	
	private UnaryOperator<String> offsetIgnoreSubsting(int beginIgnoreOffset, int endIgnoreOffset) {
		return (String line)->line.substring(beginIgnoreOffset, line.length()-endIgnoreOffset);
	}
	
	private static int REQUEST_COMPLETE_DATE_PATTERN = 11;
	private Object extractRequestCompleteDate(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, BRACKET_PATTERN)).replace("[", "");
		UnaryOperator<String> mainProcessor = (	String line) -> (String) FIXED_LENGTH_PARSER.parse(line, REQUEST_COMPLETE_DATE_PATTERN);
		UnaryOperator<String> postProcessor = NO_ACTION_OPERATOR;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	private static final String IP_ADDRESS_PATTERN = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private Object extractClientIp(String target) {
		UnaryOperator<String> preProcessor = NO_ACTION_OPERATOR;
		UnaryOperator<String> mainProcessor = (String line) -> (String) REGEX_PARSER.parse(line, IP_ADDRESS_PATTERN);
		UnaryOperator<String> postProcessor = NO_ACTION_OPERATOR;
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
}
