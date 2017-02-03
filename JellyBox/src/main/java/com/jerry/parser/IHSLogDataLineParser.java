package com.jerry.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.jerry.util.RegexMatcher;

public class IHSLogDataLineParser extends DefaultLineParser {
	private String category;
	private Function<String, Object> requestLineFunction, statusFunction, requestMethodFunction, clientIpFunction,
			requestCompleteDateFunctoin,requestCompleteTimeFunction,moduleFunction,interfaceFunction;
	public IHSLogDataLineParser(String fileName) {
		initCategory(fileName);
		initFunctions();
	}

	private static final String STATUS_PATTERN = QUOTATION + SPACE + ANY + SPACE;
	private final static String QUOTATION_PATTERN = QUOTATION + ANY + QUOTATION;
	private final static String REQUEST_METHOD_PATTERN = QUOTATION + ANY + SPACE;
	private static final String REQUEST_COMPLETE_TIME_PATTERN = COLLON + ANY + SPACE;
	private static final String MODULE_PATERN = SLASH+ANY+SLASH;
	private static final String INTERFACE_HEADER = "sca"+SLASH;
	private static final String INTERFACE_PATTERN = INTERFACE_HEADER+ANY+PTTERN_END;
	static final String IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_TIME_FORMATTER_PATTERN = "HH:mm:ss";
	private static int REQUEST_COMPLETE_DATE_PATTERN = 11;
	static final String IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_DATE_FORMATTER_PATTERN = "dd/MMM/yyyy";
	private static final String IP_ADDRESS_PATTERN = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private void initFunctions() {
		// requestLine
		{
			UnaryOperator<String> preProcessor = (String line) -> ((String) RegexMatcher.REGEX_PARSE_OPERATOR.parse( line, QUOTATION_PATTERN));
			UnaryOperator<String> mainProcessor = (String line) ->(String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, SPACE_PATTERN);
			requestLineFunction =  preProcessor.andThen(mainProcessor).andThen(offsetIgnoreSubsting( 1,1 ));
		}
		//status
		{
			UnaryOperator<String> mainProcessor = (String line) ->(String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, STATUS_PATTERN);
			statusFunction = mainProcessor.andThen(offsetIgnoreSubsting( 2,1 ));
		}
		//requestMethod
		{
			UnaryOperator<String> preProcessor = (String line) -> ((String) RegexMatcher.REGEX_PARSE_OPERATOR.parse( line, QUOTATION_PATTERN));
			UnaryOperator<String> mainProcessor = (String line) -> (String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, REQUEST_METHOD_PATTERN);
			requestMethodFunction = preProcessor.andThen(mainProcessor).andThen(offsetIgnoreSubsting( 1,1 ));
		}
		//clientIp
		{
			clientIpFunction = (String line) -> (String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, IP_ADDRESS_PATTERN);
		}
		//requestCompleteDate
		{
			UnaryOperator<String> preProcessor = (String line) -> ((String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, BRACKET_PATTERN)).replace("[", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) FIXED_LENGTH_PARSE_OPERATOR.parse(line, REQUEST_COMPLETE_DATE_PATTERN);
			requestCompleteDateFunctoin = (String line)->  LocalDate.parse(preProcessor.andThen(mainProcessor).apply(line), DateTimeFormatter.ofPattern(IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_DATE_FORMATTER_PATTERN,Locale.US));
		}
		//requestCompleteTime
		{
			UnaryOperator<String> preProcessor = (
					String line) -> ((String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, BRACKET_PATTERN));
			UnaryOperator<String> mainProcessor = (
					String line) -> (String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, REQUEST_COMPLETE_TIME_PATTERN);
			requestCompleteTimeFunction= (String line)->  LocalTime.parse(preProcessor.andThen(mainProcessor).andThen(offsetIgnoreSubsting(1, 1)).apply(line), DateTimeFormatter.ofPattern(IHS_LOG_DATA_PARSER_REQUEST_COMPLETE_TIME_FORMATTER_PATTERN,Locale.US));
		}
		//module
		{
			UnaryOperator<String> preProcessor = (String line)-> (String) requestLineFunction.apply(line);
			UnaryOperator<String> mainProcessor = (String line) ->(String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, MODULE_PATERN);
			UnaryOperator<String> postProcessor =(String line) ->line.replaceAll(SLASH,"");
			moduleFunction = preProcessor.andThen(mainProcessor).andThen(postProcessor);
		}
		//interface
		{
			UnaryOperator<String> preProcessor = (String line)-> (String) requestLineFunction.apply(line);
			UnaryOperator<String> mainProcessor = (String line) ->(String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, INTERFACE_PATTERN);
			UnaryOperator<String> postProcessor =(String line) ->line.replaceAll(INTERFACE_HEADER,"");
			interfaceFunction = preProcessor.andThen(mainProcessor).andThen(postProcessor);
		}
	}

	private final static String IHSLOGDATA_CATEGORY_PATTERN = DASH + ANY + DASH;

	private void initCategory(String fileName) {
		UnaryOperator<String> mainProcessor = (
				String line) -> (String) RegexMatcher.REGEX_PARSE_OPERATOR.parse(line, IHSLOGDATA_CATEGORY_PATTERN);
		UnaryOperator<String> postProcessor = (String line) -> line.replace(DASH, "");
		category = STRING_EXTRACTOR.extract(fileName, NO_ACTION_OPERATOR, mainProcessor, postProcessor);
	}

	@Override
	public Object parse(String line) {
		HashMap<String, Object> result = new HashMap<>();
		result.put("clientIp", clientIpFunction.apply(line));
		result.put("requestCompleteDate", requestCompleteDateFunctoin.apply(line));
		result.put("requestCompleteTime", requestCompleteTimeFunction.apply(line));
		result.put("requestMethod", requestMethodFunction.apply(line));
		result.put("requestLine", requestLineFunction.apply(line));
		result.put("status", statusFunction.apply(line));
		result.put("category", category);
		result.put("module", moduleFunction.apply(line));
		result.put("interface", interfaceFunction.apply(line));
		return result;
	}

	private UnaryOperator<String> offsetIgnoreSubsting(int beginIgnoreOffset, int endIgnoreOffset) {
		return (String line) -> line.substring(beginIgnoreOffset, line.length() - endIgnoreOffset);
	}

}
