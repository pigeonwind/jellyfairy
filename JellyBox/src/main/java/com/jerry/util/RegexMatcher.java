package com.jerry.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jerry.parser.function.RegexParseOperator;

public class RegexMatcher {
	public static final RegexParseOperator REGEX_PARSE_OPERATOR = (String line, Object columnRegex) -> {
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
}
