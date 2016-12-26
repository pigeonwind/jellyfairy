package com.jerry.parser;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jerry.pattern.Parser;
import com.jerry.pattern.RegexParser;
import com.jerry.pattern.StringExtractor;

abstract public class DefaultParser implements Parser {
	// pattern 
	static final String LEFTBRACKET="\\[";
	static final String RIGHTBRACKET="\\]";
	static final String SPACE=" ";
	static final String ANY="(.*?)";
	static final String QUOTATION="\"";
	static final String COLLON=":";
	
	static final String BRACKET_PATTERN = LEFTBRACKET+ANY+RIGHTBRACKET;
	static final String SPACE_PATTERN = SPACE+ANY+SPACE;
	// extractor define
	static final StringExtractor STRING_EXTRACTOR = (String targetLine, UnaryOperator<String> preProcessor,
			UnaryOperator<String> mainProcessor, UnaryOperator<String> postProcessor) -> (String) preProcessor
					.andThen(mainProcessor).andThen(postProcessor).apply(targetLine);
	// default operator define
	static final UnaryOperator<String> NO_ACTION_OPERATOR = (String line) -> line;

	// parser define
	static final RegexParser REGEX_PARSER = (String line, Object columnRegex) -> {
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
	final static RegexParser EXCLUDE_PARSER= (String line, Object columnRegex) -> {
		Pattern pattern = Pattern.compile((String) columnRegex);
		Matcher matcher = pattern.matcher(line);
		String result,excludeStr;
		if (matcher.find()) {
			int beginOffset = matcher.start();
			int endOffset = matcher.end();
			excludeStr = line.substring(beginOffset, endOffset);
		} else {
			excludeStr = "null";
		}
		result = line.replace(excludeStr, "");
		return result;
	};
	static final RegexParser FIXED_LENGTH_PARSER = (String line, Object length) -> {
		String result;
		int beginOffset = 0;
		int endOffset = beginOffset + (Integer) length;
		if (endOffset < 1) {
			result = "null";
		}
		result = line.substring(beginOffset, endOffset);
		return result;
	};

}
