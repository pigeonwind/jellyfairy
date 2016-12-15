package com.jerry.pattern;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ColumnParserTest {
	String targetLogline;
	String ipaddressPattern, userIDPattern,bracketPattern,leftCollonPattern,leftQuotationPattern;
	int fixedLengthDatePattern,fixedLengthTimePattern;
	ColumnParser regexParser, whiteSpaceDelimeterFrontSideParser,fixedLengthParser;

	@Before
	public void setUp() throws Exception {
		targetLogline = "127.0.0.1 - frank [05/Oct/2016:10:16:44 +0900] \"GET /apache_pb.gif HTTP/1.0\" 200 2326 \"http://www.example.com/start.html\" \"Mozilla/4.08 [en] (Win98; I ;Nav)\"";
		ipaddressPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
		userIDPattern = " ";//"[\\u0020]"
		bracketPattern = "\\[(.*?)\\]";
		leftCollonPattern ="^:";
		leftQuotationPattern ="\"(.*?) ";
		fixedLengthDatePattern=11;
		fixedLengthTimePattern=8;
		regexParser = (String line, Object columnRegex) -> {
			Pattern pattern = Pattern.compile((String)columnRegex);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				int startOffset = matcher.start();
				int endOffset = matcher.end();
				return line.substring(startOffset, endOffset);
			}else{
				return "null";
			}
		};
		whiteSpaceDelimeterFrontSideParser=  (String line, Object columnRegex) -> {
			String[] splitedLine = line.split((String)columnRegex);
			if(splitedLine == null){
				return "null";
			}else if(1 >= splitedLine.length){
				return "null";
			}
			return splitedLine[1];
		};
		fixedLengthParser = (String line, Object length) -> {
			int beginOffset =0;
			int endOffset = beginOffset+ (Integer)length;
			if(endOffset<1){
				return "null";
			}
			return line.substring(beginOffset, endOffset);
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void matchIpTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "matchIpTest");
		// given
		Object regexPattern = ipaddressPattern;
		String expect = "127.0.0.1";
		// when
		String actual = (String) regexParser.parse(targetLogline, regexPattern);
		// then
		assertThat(actual, is(expect));
	}

	@Test
	public void matchUserIdTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "matchUserIdTest");
		// given
		Object regexPattern = userIDPattern;
		String expect = "-";
		// when
		String surplusString = targetLogline;
		surplusString = surplusString.replace((String) regexParser.parse(surplusString, ipaddressPattern), "");
		
		ColumnParser parser = whiteSpaceDelimeterFrontSideParser;
		String actual = (String) parser.parse(surplusString, regexPattern);
		// then
		assertThat(actual, is(expect));
	}
	@Test
	public void matchRequestCompleteDateTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "matchRequestCompleteDateTest");
		// given
		Object regexPattern = fixedLengthDatePattern;
		String expect = "05/Oct/2016";
		// when
		String surplusString = targetLogline;
		surplusString = surplusString.replace((String) regexParser.parse(surplusString, ipaddressPattern), "");
		surplusString = surplusString.replace((String) whiteSpaceDelimeterFrontSideParser.parse(surplusString, userIDPattern), "");
		surplusString = (String)regexParser.parse(surplusString, bracketPattern);
		surplusString = surplusString.replace("[", "");
		System.out.println(surplusString);

		ColumnParser parser = fixedLengthParser;
		String actual = (String) parser.parse(surplusString, regexPattern);
		// then
		assertThat(actual, is(expect));
	}
	@Test
	public void matchRequestCompleteTimeTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "matchRequestCompleteTimeTest");
		// given
		Object regexPattern = fixedLengthTimePattern;
		String expect = "10:16:44";
		// when
		String surplusString = targetLogline;
		surplusString = surplusString.replace((String) regexParser.parse(surplusString, ipaddressPattern), ""); // consume
		surplusString = surplusString.replace((String) whiteSpaceDelimeterFrontSideParser.parse(surplusString, userIDPattern), ""); //consume
		surplusString = (String)regexParser.parse(surplusString, bracketPattern);
		surplusString = surplusString.replace("[", ""); // ignore
		surplusString = surplusString.replace((String) fixedLengthParser.parse(surplusString, fixedLengthDatePattern), "");
		surplusString = surplusString.replaceFirst(":", ""); // ignore
		System.out.println(surplusString);
		ColumnParser parser = fixedLengthParser;
		String actual = (String) parser.parse(surplusString, regexPattern);
		// then
		assertThat(actual, is(expect));
	}
	/**
	 * parser 에는 전처리 후처리 있어야할듯
	 * @throws Exception
	 */
	@Test
	public void matchRequestMethodTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "matchRequestMethodTest");
		// given
		Object regexPattern = leftQuotationPattern;
		String expect = "GET";
		String surplusString = targetLogline;
		surplusString = surplusString.replace((String) regexParser.parse(surplusString, ipaddressPattern), ""); // consume
		surplusString = surplusString.replace((String) whiteSpaceDelimeterFrontSideParser.parse(surplusString, userIDPattern), ""); //consume
		System.out.println(surplusString);
		surplusString = surplusString.replace((String)regexParser.parse(surplusString, bracketPattern), "");
		System.out.println(surplusString);
		// when
		ColumnParser parser = regexParser;
		String actual = (String) parser.parse(surplusString, regexPattern);
		//후처리
		actual = actual.replace("\"", "").replace(" ","");
		// then
		assertThat(actual, is(expect));
	}
	//https://docs.logentries.com/docs/regex 참고
	@Test
	public void matchRequestLineTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "matchRequestLineTest");
		// given
		Object regexPattern = leftQuotationPattern;
		String expect = "/apache_pb.gif";
		String surplusString = targetLogline;
		surplusString = surplusString.replace((String) regexParser.parse(surplusString, ipaddressPattern), ""); // consume
		surplusString = surplusString.replace((String) whiteSpaceDelimeterFrontSideParser.parse(surplusString, userIDPattern), ""); //consume
		System.out.println(surplusString);
		surplusString = surplusString.replace((String)regexParser.parse(surplusString, bracketPattern), "");
		System.out.println(surplusString);
		// when
		ColumnParser parser = whiteSpaceDelimeterFrontSideParser;
		String actual = (String) parser.parse(surplusString, regexPattern);
		//후처리
		actual = actual.replace("\"", "").replace(" ","");
		// then
		assertThat(actual, is(expect));
	}
}
