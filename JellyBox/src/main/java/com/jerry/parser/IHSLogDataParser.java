package com.jerry.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class IHSLogDataParser extends DefaultParser{
	private String category;

	public IHSLogDataParser(String fileName) {
		initCategory(fileName);
	}

	private final static String IHSLOGDATA_CATEGORY_PATTERN=DASH+ANY+DASH;
	private void initCategory(String fileName) {
		UnaryOperator<String> mainProcessor = (String line) -> (String) REGEX_PARSE_OPERATOR.parse(line, IHSLOGDATA_CATEGORY_PATTERN);
		UnaryOperator<String> postProcessor = (String line) -> line.replace( DASH, "" );
		category =  STRING_EXTRACTOR.extract(fileName, NO_ACTION_OPERATOR, mainProcessor, postProcessor);
	}
	@Override
	public Object parseString(String line) {
		HashMap<String,Object> result = new HashMap<>(  );
		result.put("clientIp", extractClientIp(line));
		result.put("requestCompleteDate", extractRequestCompleteDate(line));
		result.put("requestCompleteTime", extractRequestCompleteTime(line));
		result.put("requestMethod", extractRequestMethod(line));
		result.put("requestLine", extractRequestLine(line));
		result.put("status", extractStatus(line));
		result.put("category", category);
		return result;
	}

	private static final String STATUS_PATTERN =QUOTATION+SPACE+ANY+SPACE;
	private Object extractStatus(String target) {
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSE_OPERATOR.parse(line, STATUS_PATTERN);
		return STRING_EXTRACTOR.extract(target, NO_ACTION_OPERATOR, mainProcessor, offsetIgnoreSubsting( 2,1 ));
	}
	private Object extractRequestLine(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSE_OPERATOR.parse( line, QUOTATION_PATTERN));
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSE_OPERATOR.parse(line, SPACE_PATTERN);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, offsetIgnoreSubsting( 1,1 ));
	}
	private final static String QUOTATION_PATTERN = QUOTATION+ANY+QUOTATION;
	private final static String REQUEST_METHOD_PATTERN =QUOTATION+ANY+SPACE;
	private Object extractRequestMethod(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSE_OPERATOR.parse( line, QUOTATION_PATTERN));
		UnaryOperator<String> mainProcessor = (String line) -> (String) REGEX_PARSE_OPERATOR.parse(line, REQUEST_METHOD_PATTERN);
		return STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, offsetIgnoreSubsting( 1,1 ));
	}

	private static final String REQUEST_COMPLETE_TIME_PATTERN =COLLON+ANY+SPACE;
	static final String IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_TIME_FORMATTER_PATTERN = "HH:mm:ss";

	private Object extractRequestCompleteTime(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSE_OPERATOR.parse( line,BRACKET_PATTERN ));
		UnaryOperator<String> mainProcessor = (String line) ->(String) REGEX_PARSE_OPERATOR.parse(line, REQUEST_COMPLETE_TIME_PATTERN);
		String timeString = STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, offsetIgnoreSubsting( 1,1 ));
		return LocalTime.parse(timeString,DateTimeFormatter.ofPattern(IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_TIME_FORMATTER_PATTERN));
	}
	private UnaryOperator<String> offsetIgnoreSubsting(int beginIgnoreOffset, int endIgnoreOffset) {
		return (String line)->line.substring(beginIgnoreOffset, line.length()- endIgnoreOffset);
	}
	private static int REQUEST_COMPLETE_DATE_PATTERN = 11;
	
	static final String IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_DATE_FORMATTER_PATTERN = "dd/MMM/yyyy";
	private Object extractRequestCompleteDate(String target) {
		UnaryOperator<String> preProcessor = (String line) -> ((String) REGEX_PARSE_OPERATOR.parse(line, BRACKET_PATTERN)).replace("[", "");
		UnaryOperator<String> mainProcessor = (	String line) -> (String) FIXED_LENGTH_PARSE_OPERATOR.parse(line, REQUEST_COMPLETE_DATE_PATTERN);
		String dateString = STRING_EXTRACTOR.extract(target, preProcessor, mainProcessor, NO_ACTION_OPERATOR );
		return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_DATE_FORMATTER_PATTERN,Locale.US));
	}

	private static final String IP_ADDRESS_PATTERN = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";

	private Object extractClientIp(String target) {
		UnaryOperator<String> mainProcessor = (String line) -> (String) REGEX_PARSE_OPERATOR.parse(line, IP_ADDRESS_PATTERN);
		return STRING_EXTRACTOR.extract(target, NO_ACTION_OPERATOR, mainProcessor, NO_ACTION_OPERATOR );
	}
}
