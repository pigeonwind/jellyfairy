package com.jerry.parser;

import com.jerry.parser.function.RegexParseOperator;
import com.jerry.parser.function.StringExtractor;
import com.jerry.util.function.StringParser;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class DefaultParser implements StringParser {
	// pattern
	static final String LEFTBRACKET="\\[";
	static final String RIGHTBRACKET="\\]";
	static final String SPACE=" ";
	static final String ANY="(.*?)";
	static final String PTTERN_END="$";
	static final String QUOTATION="\"";
	static final String COLLON=":";
	static final String SLASH="/";
	static final String UNDERBAR = "_";
	static final String DASH="-";
	static final String DOT="\\.";
	static final String CHAR_NUMBER="[a-zA-Z0-9]*";

	static final String BRACKET_PATTERN = LEFTBRACKET+ANY+RIGHTBRACKET;
	static final String SPACE_PATTERN = SPACE+ANY+SPACE;
	// extractor define
	static final StringExtractor STRING_EXTRACTOR = (String targetLine, UnaryOperator<String> preProcessor,
													 UnaryOperator<String> mainProcessor, UnaryOperator<String> postProcessor) -> (String) preProcessor
					.andThen(mainProcessor).andThen(postProcessor).apply(targetLine);

	// default operator define
	static final UnaryOperator<String> NO_ACTION_OPERATOR = (String line) -> line;
	// parser define
	static final RegexParseOperator REGEX_PARSE_OPERATOR = (String line, Object columnRegex) -> {
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
	final static RegexParseOperator EXCLUDE_PARSE_OPERATOR = (String line, Object columnRegex) -> {
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

	static final RegexParseOperator FIXED_LENGTH_PARSE_OPERATOR = (String line, Object length) -> {
		String result;
		int beginOffset = 0;
		int endOffset = beginOffset + (Integer) length;
		result = line.substring(beginOffset, endOffset);
		return result;
	};
}
