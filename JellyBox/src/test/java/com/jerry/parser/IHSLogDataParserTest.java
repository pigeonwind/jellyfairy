package com.jerry.parser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jerry.pattern.RegexParser;
import com.jerry.pattern.StringExtractor;

public class IHSLogDataParserTest {
	String logLine;
	RegexParser regexParser, fixedLengthParser;
	StringExtractor extractor;
	UnaryOperator<String> noActionOperator;
	String bracketPattern;
	String leftCollonRightSpacePattern;
	String quotationPattern;
	String leftQuotationPattern;
	String spacePattern;
	String leftQutationSpaceRightSpacePattern;
	String testFilePath;
	@Before
	public void setUp() throws Exception {
		testFilePath= System.getProperty("user.dir")+"/testResource/access-SEC-test2.log";
		
		logLine = "/logs/ihs/access-SEC_log.20161006:127.0.0.1 - - [06/Oct/2016:14:32:55 +0900] \"GET /SEC01ModWeb/sca/LAY020278WS HTTP/1.1\" 404 244 0";
		extractor = (String targetLine, UnaryOperator<String> preProcessor,	UnaryOperator<String> mainProcessor, UnaryOperator<String> postProcessor) -> (String) preProcessor.andThen(mainProcessor).andThen(postProcessor).apply(targetLine);
		noActionOperator = (String line) -> line;
		bracketPattern = "\\[(.*?)\\]";
		leftCollonRightSpacePattern = ":(.*?) ";
		quotationPattern = "\"(.*?)\"";
		leftQuotationPattern ="\"(.*?) ";
		spacePattern = " (.*?) ";
		leftQutationSpaceRightSpacePattern ="\" (.*?) ";
		
		
		// parser setting
		{
			regexParser = (String line, Object columnRegex) -> {
				Pattern pattern = Pattern.compile((String) columnRegex);
				Matcher matcher = pattern.matcher(line);
				String result;
				if (matcher.find()) {
					int startOffset = matcher.start();
					int endOffset = matcher.end();
					result = line.substring(startOffset, endOffset);
				} else {
					result = "null";
				}
				return result;
			};
			fixedLengthParser = (String line, Object length) -> {
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
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void parseTest() throws Exception {

		System.out.printf("=================== %s START ===================\n", "parseTest");
		// given
		Object expected = getParsedLogLineMap(logLine);
		IHSLogDataParser  parser= new IHSLogDataParser(logLine);
		// when
		Object actual = parser.parse();
		// then
		assertThat(actual, is(expected));
	}

	private Object getParsedLogLineMap(String logLine) {
		Map<String, String> ihsLogLineMap = new HashMap<>();
		{
			String ipaddressPattern = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
			ihsLogLineMap.put("clientIp", extractor.extract(logLine, noActionOperator,
					(String line) -> (String) regexParser.parse(line, ipaddressPattern), noActionOperator));
		}
		{
			int fixedLengthDatePattern = 11;
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse(line, bracketPattern)).replace("[", "");
			RegexParser parser = fixedLengthParser;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, fixedLengthDatePattern);
			ihsLogLineMap.put("requestCompleteDate",
					extractor.extract(logLine, preProcessor, mainProcessor, noActionOperator));
		}
		{
			Object regexPattern = leftCollonRightSpacePattern;
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line,bracketPattern ));
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParser.parse(line, regexPattern);
			int beginIgnoreOffset = 1;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("requestCompleteTime",extractor.extract(logLine, preProcessor, mainProcessor, postProcessor));
		}
		{
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line, quotationPattern));
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParser.parse(line, leftQuotationPattern);
			int beginIgnoreOffset = 1;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("requestMethod", extractor.extract(logLine, preProcessor, mainProcessor, postProcessor));
		}
		{
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse( line, quotationPattern));
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParser.parse(line, spacePattern);
			int beginIgnoreOffset = 1;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("requestLine", extractor.extract(logLine, preProcessor, mainProcessor, postProcessor));
		}
		{
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParser.parse(line, leftQutationSpaceRightSpacePattern);
			int beginIgnoreOffset = 2;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor =(String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("status", extractor.extract(logLine, preProcessor, mainProcessor,postProcessor));
		}
//		System.out.printf("parsed result %s\n",ihsLogLineMap);
		return ihsLogLineMap;
	}
	@Test
	public void multiLineParseTest() throws Exception {
		
		System.out.printf("=================== %s START ====================\n", "multiLineParseTest");
		List<String> lines =getDummyDatas();
		System.out.println(lines);
		List<Object> parsedLines = new LinkedList<>();
		// given
		Object expected=null;
		System.out.println("================= given =================");
		{
			Iterator<String> iterator = lines.iterator();
			while (iterator.hasNext()) {
				String line = (String) iterator.next();
				parsedLines.add(getParsedLogLineMap(line));
			}
			expected=parsedLines;
		}
		System.out.println("================= when =================");
		// when
		Object actual=null;
		try(Stream<String> lineStream = Files.lines(Paths.get(testFilePath), Charset.defaultCharset())){
			List<Object> actualParsedLines = lineStream.parallel().map(IHSLogDataParser::new).map(IHSLogDataParser::parse).collect(Collectors.toList());
			actual = actualParsedLines;
		}catch (IOException e) {
			e.printStackTrace();
		}
		// then
		assertThat(actual, is(expected));
	}

	private List<String> getDummyDatas() {
		List<String> result=new LinkedList<>();
		try(Stream<String> lines = Files.lines(Paths.get(testFilePath),Charset.defaultCharset())){
			result = lines.parallel().collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
