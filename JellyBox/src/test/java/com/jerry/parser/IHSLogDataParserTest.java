package com.jerry.parser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

import com.jerry.parser.function.*;
import com.jerry.util.function.*;
import org.junit.*;

public class IHSLogDataParserTest {
	private String logLine;
	private RegexParseOperator regexParseOperator, fixedLengthParser;
	private StringExtractor extractor;
	private UnaryOperator<String> noActionOperator;
	private String bracketPattern;
	private String leftCollonRightSpacePattern;
	private String quotationPattern;
	private String leftQuotationPattern;
	private String spacePattern;
	private String leftQutationSpaceRightSpacePattern;
	private String testFilePath;
	private StringParser parser;
	@Before
	public void setUp() throws Exception {
		testFilePath= System.getProperty("user.dir")+"/testResource/access-SEC-test2.log";
		parser = new IHSLogDataParser( testFilePath );
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
			regexParseOperator = (String line, Object columnRegex) -> {
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
		// when
		Object actual = parser.parseString( logLine );
		// then
		assertThat(actual, is(expected));
	}

	private Object getParsedLogLineMap(Object input) {
		String logLine=(String)input;
		Map<String, String> ihsLogLineMap = new HashMap<>();
		{
			String ipaddressPattern = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
			ihsLogLineMap.put("clientIp", extractor.extract(logLine, noActionOperator,
					(String line) -> (String) regexParseOperator.parse(line, ipaddressPattern), noActionOperator));
		}
		{
			int fixedLengthDatePattern = 11;
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParseOperator.parse(line, bracketPattern)).replace("[", "");
			RegexParseOperator parser = fixedLengthParser;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, fixedLengthDatePattern);
			ihsLogLineMap.put("requestCompleteDate",
					extractor.extract(logLine, preProcessor, mainProcessor, noActionOperator));
		}
		{
			Object regexPattern = leftCollonRightSpacePattern;
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParseOperator.parse( line,bracketPattern ));
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParseOperator.parse(line, regexPattern);
			int beginIgnoreOffset = 1;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("requestCompleteTime",extractor.extract(logLine, preProcessor, mainProcessor, postProcessor));
		}
		{
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParseOperator.parse( line, quotationPattern));
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParseOperator.parse(line, leftQuotationPattern);
			int beginIgnoreOffset = 1;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("requestMethod", extractor.extract(logLine, preProcessor, mainProcessor, postProcessor));
		}
		{
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParseOperator.parse( line, quotationPattern));
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParseOperator.parse(line, spacePattern);
			int beginIgnoreOffset = 1;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor = (String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("requestLine", extractor.extract(logLine, preProcessor, mainProcessor, postProcessor));
		}
		{
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParseOperator.parse(line, leftQutationSpaceRightSpacePattern);
			int beginIgnoreOffset = 2;
			int endIgnoreOffset=1;
			UnaryOperator<String> postProcessor =(String line) ->line.substring(beginIgnoreOffset,line.length()-endIgnoreOffset);
			ihsLogLineMap.put("status", extractor.extract(logLine, preProcessor, mainProcessor,postProcessor));
		}
		{
			String dashPattern ="-(.*?)-";
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (String line) ->(String) regexParseOperator.parse(line, dashPattern);
			UnaryOperator<String> postProcessor =(String line) -> line.replace( "-","" );
			ihsLogLineMap.put("category", extractor.extract(testFilePath, preProcessor, mainProcessor,postProcessor));
		}
		return ihsLogLineMap;
	}

	@Test
	public void multiLineParseTest() throws Exception {
		
		System.out.printf("=================== %s START ====================\n", "multiLineParseTest");
		List<String> lines =getDummyDatas();
		// given
		Object expected;
		System.out.println("================= given =================");
		{
			expected = lines.stream().parallel().map( this::getParsedLogLineMap ).peek( System.out::println).collect( Collectors.toList() );
		}

		// when
		System.out.println("================= when =================");
		Object actual;
		try(Stream<String> lineStream = Files.lines(Paths.get(testFilePath), Charset.defaultCharset())){
			actual =  lineStream.parallel().map(parser::parseString).peek( System.out::println).collect(Collectors.toList());
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
