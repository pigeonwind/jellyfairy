package com.jerry.pattern;

import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ColumnParserTest {
	String targetLogline;
	String ipaddressPattern, userIDPattern,bracketPattern,quotationPattern,spacePattern;//,
	String leftCollonRightSpacePattern,leftQuotationPattern,leftQutationSpaceRightSpacePattern,leftSpaceRightQuotatoinPattern;
	int fixedLengthDatePattern;
	ColumnParser regexParser, fixedLengthParser;
	StringExtractor extractor;
	UnaryOperator<String> originalReturer;

	@Before
	public void setUp() throws Exception {
		targetLogline = "127.0.0.1 - frank [05/Oct/2016:10:16:44 +0900] \"GET /apache_pb.gif HTTP/1.0\" 200 2326 \"http://www.example.com/start.html\" \"Mozilla/4.08 [en] (Win98; I ;Nav)\"";
		ipaddressPattern = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		userIDPattern = " (.*?) ";//"[\\u0020]"
		bracketPattern = "\\[(.*?)\\]";
		quotationPattern = "\"(.*?)\"";
		spacePattern = " (.*?) ";

		leftCollonRightSpacePattern = ":(.*?) ";
		leftQuotationPattern ="\"(.*?) ";
		leftSpaceRightQuotatoinPattern = " (.*?)\"";
		leftQutationSpaceRightSpacePattern ="\" (.*?) ";
		fixedLengthDatePattern=11;


		extractor = (String targetLine, UnaryOperator<String> preProcessor, UnaryOperator<String> mainProcessor, UnaryOperator<String> postProcessor) ->
				(String)preProcessor.andThen(mainProcessor).andThen( postProcessor).apply( targetLine );

		originalReturer = (String line)->line;
		regexParser = (String line, Object columnRegex) -> {
			Pattern pattern = Pattern.compile((String)columnRegex);
			Matcher matcher = pattern.matcher(line);
			String result;
			if (matcher.find()) {
				int startOffset = matcher.start();
				int endOffset = matcher.end();
				result=line.substring(startOffset, endOffset);
			}else{
				result="null";
			}
			out.printf("line : %s\n" ,line );
			out.printf("reegex : %s\n", columnRegex );
			out.printf("result : %s\n", result );
			return result;
		};

		fixedLengthParser = (String line, Object length) -> {
			String result;
			int beginOffset =0;
			int endOffset = beginOffset+ (Integer)length;
			if(endOffset<1){
				result = "null";
			}
			result= line.substring(beginOffset, endOffset);
			out.printf("line : %s\n" ,line );
			out.printf("reegex : %d\n", endOffset );
			out.printf("result : %s\n", result );
			return result;
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void matchIpTest() throws Exception {
		out.printf("=================== %s START ===================\n", "matchIpTest");
		// given

		Object regexPattern = ipaddressPattern;
		String surplusString = targetLogline;
		String expect = "127.0.0.1";
		// when
		String actual = (String) extractor.extract(surplusString,originalReturer,(String line)-> (String)regexParser.parse(line,regexPattern),originalReturer);
//		String actual = (String) regexParser.parse(surplusString, regexPattern);
		// then
		assertThat(actual, is(expect));
	}

	@Test
	public void matchUserIdTest() throws Exception {
		out.printf("=================== %s START ===================\n", "matchUserIdTest");
		// given
		Object regexPattern = userIDPattern;
		String expect = "-";
		// when
		String surplusString = targetLogline;
		surplusString = surplusString.replace((String) regexParser.parse(surplusString, ipaddressPattern), "");
		out.println( surplusString );
		ColumnParser parser = regexParser;
		UnaryOperator<String> mainProcessor = (String line) -> (String) parser.parse( line, userIDPattern );
		String actual = (String) extractor.extract(surplusString, originalReturer, mainProcessor,(String line)-> line.replace( " ","" ));
		out.println( actual );
		// then
		assertThat(actual, is(expect));
	}
	@Test
	public void matchRequestCompleteDateTest() throws Exception {
		out.printf("=================== %s START ===================\n", "matchRequestCompleteDateTest");
		// given
		Object regexPattern = fixedLengthDatePattern;
		String expect = "05/Oct/2016";
		// when
		String surplusString = targetLogline;

		out.println(surplusString);
		UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line,bracketPattern )).replace( "[","" );
		ColumnParser parser = fixedLengthParser;
		UnaryOperator<String> mainProcessor = (String line) ->(String) parser.parse(line, regexPattern);
		String actual = (String) extractor.extract(surplusString, preProcessor, mainProcessor,originalReturer);
		// then
		assertThat(actual, is(expect));
	}

	@Test
	public void matchRequestCompleteTimeTest() throws Exception {
		out.printf("=================== %s START ===================\n", "matchRequestCompleteTimeTest");
		// given
		Object regexPattern = leftCollonRightSpacePattern;
		String expect = "10:16:44";
		String surplusString = targetLogline;
		out.println(surplusString);
		ColumnParser parser = regexParser;
		UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line,bracketPattern ));
		UnaryOperator<String> mainProcessor = (String line) ->(String) parser.parse(line, regexPattern);
		int beginIgnoreOffset = 1;
		int endIgnoreOffset=1;
		UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
		// when
		String actual = (String) extractor.extract(surplusString, preProcessor, mainProcessor,postProcessor);
		// then
		assertThat(actual, is(expect));
	}

	/**
	 * parser 에는 전처리 후처리 있어야할듯
	 * @throws Exception
	 */
	@Test
	public void matchRequestMethodTest() throws Exception {
		out.printf("=================== %s START ===================\n", "matchRequestMethodTest");
		// given
		Object regexPattern = leftQuotationPattern;
		String expect = "GET";
		String surplusString = targetLogline;

		ColumnParser parser = regexParser;
		//전처리
		UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line, quotationPattern));
		//본작업
		UnaryOperator<String> mainProcessor = (String line) ->(String) parser.parse(line, regexPattern);
		//후처리
		int beginIgnoreOffset = 1;
		int endIgnoreOffset=1;
		UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
		// when
		String actual = (String) extractor.extract(surplusString, preProcessor, mainProcessor,postProcessor);
		// then
		assertThat(actual, is(expect));
	}

	//https://docs.logentries.com/docs/regex 참고
	@Test
	public void matchRequestLineTest() throws Exception {
		out.printf("=================== %s START ===================\n", "matchRequestLineTest");
		// given
		Object regexPattern = spacePattern;
		String expect = "/apache_pb.gif";
		String surplusString = targetLogline;
		ColumnParser parser = regexParser;
		//전처리
		UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line, quotationPattern));
		//본작업
		UnaryOperator<String> mainProcessor = (String line) ->(String) parser.parse(line, regexPattern);
		//후처리
		int beginIgnoreOffset = 1;
		int endIgnoreOffset=1;
		UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
		// when
		String actual = (String) extractor.extract(surplusString, preProcessor, mainProcessor,postProcessor);
		// then
		assertThat(actual, is(expect));
	}

	@Test
	public void matchRequestProtocolTest() throws Exception {
		out.printf( "========= %sTest() START =========\n", "matchRequestProtocol" );
		// given
		Object regexPattern = leftSpaceRightQuotatoinPattern;
		String expect = "HTTP/1.0";
		String surplusString = targetLogline;
		ColumnParser parser = regexParser;
		//전처리
		UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line, quotationPattern));
		//본작업
		UnaryOperator<String> mainProcessor = (String line) ->(String) parser.parse(line, regexPattern);
		//후처리
		UnaryOperator<String> postProcessor = (String line) -> line.split( " " )[2].replace( "\"","" );
		// when
		String actual = (String) extractor.extract(surplusString, preProcessor, mainProcessor,postProcessor);
		// then
		assertThat(actual, is(expect));
	}

	@Test
	public void matchResponseStatusTest() throws Exception {
		out.printf( "========= %sTest() START =========\n", "matchResponseStatus" );
		// given
		Object regexPattern = leftQutationSpaceRightSpacePattern;
		String expect = "200";
		String surplusString = targetLogline;
		ColumnParser parser = regexParser;
		//전처리
		UnaryOperator<String> preProcessor = originalReturer;
//		UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line, quotationPattern));
		//본작업
		UnaryOperator<String> mainProcessor = (String line) ->(String) parser.parse(line, regexPattern);
		//후처리
		int beginIgnoreOffset = 2;
		int endIgnoreOffset=1;
		UnaryOperator<String> postProcessor =(String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
		// when
		String actual = (String) extractor.extract(surplusString, preProcessor, mainProcessor,postProcessor);
		// then
		assertThat(actual, is(expect));
	}

	@Test
	public void matchResponseSizeExcludedHedersizeTest() throws Exception {
		out.printf( "========= %sTest() START =========\n", "matchResponseSizeExcludedHedersizeT" );
		// given
		Object expected = null;
		// when
		Object actual = null;
		// then
		assertThat( actual, is( expected ) );
	}
}