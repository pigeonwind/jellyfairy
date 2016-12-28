package com.jerry.parser;

import java.util.HashMap;
import java.util.function.UnaryOperator;

public class WASSystemOutLogParser extends DefaultParser{

	private String serverName;
	private UnaryOperator<String> REMOVE_SPACECHAR_OPERATOR=(String line) -> line.replace(" ", "");

	public WASSystemOutLogParser(String fileName) {
	    initServerName(fileName);
	}
	private static final String SERVERNAME_PATTERN=CHAR_NUMBER+DOT+CHAR_NUMBER;
	private void initServerName(String fileName) {
		serverName =STRING_EXTRACTOR.extract( fileName , NO_ACTION_OPERATOR,
				(String line) ->(String) REGEX_PARSE_OPERATOR.parse(line, SERVERNAME_PATTERN),
				NO_ACTION_OPERATOR);
	}

	@Override
	public Object parseString(String line) {
		HashMap<String, Object> result = new HashMap<>(  );
		result.put("date", extractDate(line));
		result.put("time", extractTime(line));
		result.put("threadName", extractThreadName(line));
		result.put("processComponent", extractProcessComponent(line));
		result.put("level", extractLevel(line));
		result.put("code", extractCode(line));
		result.put("content", extractContent(line));
		result.put("serverName", serverName);
		return result;
	}
	private static final String LEVEL="([W|I|E|A]{1})";
	private static final String LEVEL_PATTERN=SPACE+LEVEL+SPACE;
	private static final String EXCLUDELINE_PATTERN=BRACKET_PATTERN+ANY+LEVEL_PATTERN;
	private Object extractContent(String target) {
		return STRING_EXTRACTOR.extract(target, NO_ACTION_OPERATOR,
				(String line) ->(String) EXCLUDE_PARSE_OPERATOR.parse(line, EXCLUDELINE_PATTERN),
				NO_ACTION_OPERATOR );
	}
	private static final String CODE_PATTERN="([A-Z]{4}|[A-Z]{5})([0-9]{4}|[0-9]{5})"+LEVEL;
	private Object extractCode(String target) {
		return STRING_EXTRACTOR.extract(target, NO_ACTION_OPERATOR,
				(String line) -> ((String) REGEX_PARSE_OPERATOR.parse(line, CODE_PATTERN)),
				(String line)->line.replace(":", ""));
	}
	private Object extractLevel(String target) {
		return STRING_EXTRACTOR.extract(target, NO_ACTION_OPERATOR,
				(String line) ->(String) REGEX_PARSE_OPERATOR.parse(line, LEVEL_PATTERN),
				REMOVE_SPACECHAR_OPERATOR);
	}
	private static final String THREAD_NAME_PATTERN = RIGHTBRACKET+SPACE+ANY+SPACE;
	private static final String PRE_PROCESS_COMPONENT_PATTERN=THREAD_NAME_PATTERN+ANY+SPACE;
	private static final String PROCESS_COMPONENT_PATTERN =SPACE+ANY+SPACE;
	private Object extractProcessComponent(String target) {
		return STRING_EXTRACTOR.extract(target, (String line) -> ((String) REGEX_PARSE_OPERATOR.parse(line, PRE_PROCESS_COMPONENT_PATTERN)).replace("] ", ""),
				(String line) ->(String) REGEX_PARSE_OPERATOR.parse(line, PROCESS_COMPONENT_PATTERN),
				REMOVE_SPACECHAR_OPERATOR);
	}
	private Object extractThreadName(String target) {
		return STRING_EXTRACTOR.extract(target, NO_ACTION_OPERATOR,
				(String line) -> ((String) REGEX_PARSE_OPERATOR.parse(line, THREAD_NAME_PATTERN)),
				(String line)-> line.replace("] ", ""));
	}
	private static final String WASSYSTEMOUTLOG_TIMESTAMP_PATTERN="([0-9]{1}|[0-9]{2}):([0-9]{2}):([0-9]{2}):([0-9]{3})";
	private Object extractTime(String target) {
		return STRING_EXTRACTOR.extract(target, (String line) -> ((String) REGEX_PARSE_OPERATOR.parse(line, BRACKET_PATTERN)).replace("[", ""),
				(String line) -> ((String) REGEX_PARSE_OPERATOR.parse(line, WASSYSTEMOUTLOG_TIMESTAMP_PATTERN)),
				NO_ACTION_OPERATOR );
	}
	private String WASSYSTEMOUTLOG_FIXED_LENGTH_PATTERN="(([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2}) )";
	private Object extractDate(String target) {
		return STRING_EXTRACTOR.extract(target, (String line) -> ((String) REGEX_PARSE_OPERATOR.parse(line, BRACKET_PATTERN)).replace("[", ""),
				(String line) ->(String) REGEX_PARSE_OPERATOR.parse(line, WASSYSTEMOUTLOG_FIXED_LENGTH_PATTERN),
				REMOVE_SPACECHAR_OPERATOR);
	}
}
