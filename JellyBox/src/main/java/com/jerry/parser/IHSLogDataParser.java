package com.jerry.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jerry.pattern.Parser;
import com.jerry.pattern.RegexParser;
import com.jerry.pattern.StringExtractor;

public class IHSLogDataParser implements Parser {
	private String target;
	private Map<String, Object> resultMap;
	// extractor define
	private static final StringExtractor STRING_EXTRACTOR = (String targetLine, UnaryOperator<String> preProcessor,
			UnaryOperator<String> mainProcessor, UnaryOperator<String> postProcessor) -> (String) preProcessor
					.andThen(mainProcessor).andThen(postProcessor).apply(targetLine);
	// default operator define
	private static final UnaryOperator<String> NO_ACTION_OPERATOR = (String line) -> line;
	// parser define
	private static final RegexParser REGEX_PARSER = (String line, Object columnRegex) -> {
		Pattern pattern = Pattern.compile((String) columnRegex);
		Matcher matcher = pattern.matcher(line);
		String result;
		if (matcher.find()) {
			int beginOffset = matcher.start();
			int endOffset = matcher.end();
			result = line.substring(beginOffset, endOffset);
		} else {
			result = "null";
		}
		return result;
	};
	private static final RegexParser FIXED_LENGTH_PARSER = (String line, Object length) -> {
		String result;
		int beginOffset = 0;
		int endOffset = beginOffset + (Integer) length;
		if (endOffset < 1) {
			result = "null";
		}
		result = line.substring(beginOffset, endOffset);
		return result;
	};
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
//		System.out.printf("parsed result : %s\n",resultMap);
		return resultMap;
	}
	
	private static final String LEFT_QUOTATION_SPACE_RIGHT_SPACE_PATTERN ="\" (.*?) ";
	private Object extractStatus(String target) {
		UnaryOperator<String> preProcessor = NO_ACTION_OPERATOR;
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, LEFT_QUOTATION_SPACE_RIGHT_SPACE_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(2,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}

	private final static String SPACE_PATTERN = " (.*?) ";
	private Object extractRequestLine(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse( line, QUOTATION_PATTERN));
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, SPACE_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(1,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}

	private final static String QUOTATION_PATTERN = "\"(.*?)\"";
	private final static String LEFT_QUOTATION_RIGHT_SPACE_PATTERN ="\"(.*?) ";
	private Object extractRequestMethod(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse( line, QUOTATION_PATTERN));
		UnaryOperator<String> mainProcessor = (String line) -> (String) REGEX_PARSER.parse(line, LEFT_QUOTATION_RIGHT_SPACE_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(1,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}

	private static final String LEFT_COLLON_RIGHT_SPACE_PATTERN = ":(.*?) ";
	private Object extractRequestCompleteTime(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse( line,BRACKET_PATTERN ));
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSER.parse(line, LEFT_COLLON_RIGHT_SPACE_PATTERN);
		UnaryOperator<String> postProcessor = offsetIgnoreSubsting(1,1);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, postProcessor);
	}
	
	private UnaryOperator<String> offsetIgnoreSubsting(int beginIgnoreOffset, int endIgnoreOffset) {
		return (String line)->line.substring(beginIgnoreOffset, line.length()-endIgnoreOffset);
	}
	
	private static final String BRACKET_PATTERN = "\\[(.*?)\\]";
	private static int FIXED_LENCGH_PATTERN_FOR_DATE = 11;
	private Object extractRequestCompleteDate(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSER.parse(line, BRACKET_PATTERN)).replace("[", "");
		UnaryOperator<String> mainProcessor = (	String line) -> (String) FIXED_LENGTH_PARSER.parse(line, FIXED_LENCGH_PATTERN_FOR_DATE);
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
