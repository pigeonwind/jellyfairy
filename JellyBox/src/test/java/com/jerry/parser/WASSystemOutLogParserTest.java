package com.jerry.parser;

import static java.lang.System.out;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jerry.pattern.RegexParser;
import com.jerry.pattern.StringExtractor;

public class WASSystemOutLogParserTest {
	private String logLine,logLine2,logLine3;
	private String logLine4;
	private String logLine5;
	private String testFilePath;
	RegexParser regexParser,fixedLengthParser;
	StringExtractor extractor;
	UnaryOperator<String> noActionOperator,removeSpaceChar;
	private String expectResultFilePath;
	private String actulResultFilePath;
	private String testFilePath2;

	@Before
	public void setUp() throws Exception {
		testFilePath= System.getProperty("user.dir")+"/testResource/SystemOut.log";
		testFilePath2= System.getProperty("user.dir")+"/testResource/MES2.App02_SystemOut.log";
		expectResultFilePath=System.getProperty("user.dir")+"/testResource/expected.result";
		actulResultFilePath=System.getProperty("user.dir")+"/testResource/actual.result";
		{
			logLine="[16. 12. 5   15:26:42:279 KST] 00000113 DataSourceCon E   DSRA8040I: Failed to connect to the DataSource \"\".  Encountered java.sql.SQLException: IO 오류: The Network Adapter could not establish the connection DSRA0010E: SQL 상태 = 08006, 오류 코드 = 17,002";
			logLine2="	at oracle.jdbc.driver.T4CConnection.logon(T4CConnection.java:673)";
			logLine3="[16. 12. 13   16:38:12:755 KST] 0000003b TransportAdap W   DCSV1117W: DCS Stack DefaultCoreGroup at Member AODECell\\AODmgr\\dmgr: The stream from Member AODECell\\AONode\\nodeagent has closed. The channnel is View|Ptp.";
			logLine4="[16. 12. 13   14:58:24:405 KST] 000000ee ServletWrappe I com.ibm.ws.webcontainer.servlet.ServletWrapper init SRVE0242I: [isclite] [/ibm/console] [/secure/layouts/collectionTableLayout.jsp]: Initialization successful.\n";
			logLine5="/logs/bpm/SEC/MES2/MES2.App02_SystemOut_16.10.16_23.00.00.log:[10/16/16 0:31:27:261 KST] 00007bbf LTPAServerObj E   SECJ0369E: Authentication failed when using LTPA. The exception is com.ibm.websphere.wim.exception.PasswordCheckFailedException: CWWIM4537E  No principal is found from the 'SST0004DEF' principal name..";
		}
		{
			extractor = (String targetLine, UnaryOperator<String> preProcessor,	UnaryOperator<String> mainProcessor, UnaryOperator<String> postProcessor) -> (String) preProcessor.andThen(mainProcessor).andThen(postProcessor).apply(targetLine);
			noActionOperator = (String line) -> line; 
			removeSpaceChar = (String line) -> line.replace(" ",""); 
		}
		{
			regexParser= (String line, Object columnRegex) -> {
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
//				out.printf("line [%s] regex [%s] result [%s]\n",line,columnRegex,result);
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
				out.printf("line [%s] regex [%s] result [%s]\n",line,length,result);
				return result;
			};
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void parseTest() {
		out.printf("=================== %s START ===================\n", "parseTest");
		// given
		String target=logLine;
		Object expected = getParsedLogLineMap(target);
		WASSystemOutLogParser  parser= new WASSystemOutLogParser(target);
		out.println( expected );
		// when
		Object actual = parser.parse();

		// then
		assertThat(actual, is(expected));
	}
	@Test
	public final void parse3Test() {
		out.printf("=================== %s START ===================\n", "parse3Test");
		// given
		String target=logLine3;
		Object expected = getParsedLogLineMap(target);
		WASSystemOutLogParser  parser= new WASSystemOutLogParser(target);
		out.println( expected );
		// when
		Object actual = parser.parse();

		// then
		assertThat(actual, is(expected));
	}
	@Test
	public final void parse4Test() {
		out.printf("=================== %s START ===================\n", "parse4Test");
		// given
		String target=logLine4;
		Object expected = getParsedLogLineMap(target);
		WASSystemOutLogParser  parser= new WASSystemOutLogParser(target);
		out.println( expected );
		// when
		Object actual = parser.parse();

		// then
		assertThat(actual, is(expected));
	}
	@Test
	public final void parse5Test() {
		out.printf( "=================== %s START ===================\n", "parse5Test" );
		// given
		String target = logLine5;
		Object expected = getParsedLogLineMap( target );
		WASSystemOutLogParser parser = new WASSystemOutLogParser( target );
		out.println( expected );
		// when
		Object actual = parser.parse();

		// then
		assertThat( actual, is( expected ) );
	}
		private String bracketPattern = "\\[(.*?)\\]";

	private String leftBracketSpaceRightSpacePattern = "\\] (.*?) ";
	private String processComponentPattern = leftBracketSpaceRightSpacePattern+"(.*?) ";
	private String logLevelPattern = " ([W|I|E|A]{1}) ";
	private String codePattern = "([A-Z]{4}|[A-Z]{5})([0-9]{4}|[0-9]{5})([W|I|E|A]{1})";
	private String logHeaderPattern =bracketPattern+"(.*?)"+logLevelPattern;
	private Object getParsedLogLineMap(String logLine) {
		Map<String, String> logLineMap = new HashMap<>();
		{
			RegexParser parser = regexParser;
			String datePattern = "(([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2}) )";
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse(line, bracketPattern)).replace("[", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, datePattern);
			UnaryOperator<String> postProcessor =removeSpaceChar;
			logLineMap.put("date", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParser parser = regexParser;
			String timestampPattern="([0-9]{1}|[0-9]{2}):([0-9]{2}):([0-9]{2}):([0-9]{3})";
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse(line, bracketPattern)).replace("[", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, timestampPattern);
			UnaryOperator<String> postProcessor =noActionOperator;
			logLineMap.put("time", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParser parser = regexParser;
			String pattern=leftBracketSpaceRightSpacePattern;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =(String line)-> line.replace("] ", "");
			logLineMap.put("threadName", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParser parser = regexParser;
			String pattern=" (.*?) ";
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse(line, processComponentPattern)).replace("] ", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =removeSpaceChar;
			logLineMap.put("processComponent", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParser parser = regexParser;
			String pattern=logLevelPattern;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =removeSpaceChar;
			logLineMap.put("level", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParser parser = regexParser;
			String pattern=codePattern;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =(String line)->line.replace(":", "");
			logLineMap.put("code", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParser parser = regexParser;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor =(String line) -> line.replace((String) parser.parse(line,logHeaderPattern), "");
			UnaryOperator<String> postProcessor =noActionOperator;
			logLineMap.put("content", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		return logLineMap;
	}
	@Test
	public void multiLineParseTest() throws Exception {
		out.printf( "========= %sTest() START =========\n", "multiLineParse" );
		// given
		String filePath=testFilePath;
		List<String> lines =getDummyDatas(filePath);
//		System.out.println(lines);
		Object expected = null;
		System.out.println("================= given =================");
		{
			List<Object> parsedLines = new LinkedList<>();
			Object parsedLine;
			Iterator<String> iterator = lines.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				parsedLine =getParsedLogLineMap(line);
				parsedLines.add(parsedLine);
				out.println( parsedLine );
			}
			expected=parsedLines;
		}
		writeFile(expectResultFilePath,expected);
		System.out.println("================= when =================");
		// when
		Object actual = null;
		try(Stream<String> lineStream = Files.lines(Paths.get(filePath), Charset.defaultCharset())){
			List<Object> actualParsedLines = lineStream.parallel().filter( (String line)-> line.regionMatches( 0,"[",0,1 )  ).map(WASSystemOutLogParser::new).map(WASSystemOutLogParser::parse).peek( out::println ).collect(Collectors.toList());
			actual = actualParsedLines;
		}catch (IOException e) {
			e.printStackTrace();
		}
		writeFile(actulResultFilePath,actual);

		// then
		assertThat( actual, is( expected ) );
	}

	private void writeFile(String filePath, Object contents) {
		List<Map> targetList = (List<Map>) contents;
		try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(
				Paths.get(filePath))))
		{
			targetList.stream().forEach( pw::println );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> getDummyDatas(String testFilePath) {
		List<String> result=new LinkedList<>();
		try(Stream<String> lines = Files.lines( Paths.get(testFilePath), Charset.defaultCharset())){
			result = lines.parallel().filter( (String line)-> line.regionMatches( 0,"[",0,1 )  ).collect( Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Test
	public void multiLineParse2Test() throws Exception {
		out.printf( "========= %sTest() START =========\n", "multiLineParse2" );
		// given
		String filePath=testFilePath2;
		List<String> lines =getDummyDatas(filePath);
		Object expected = null;
		System.out.println("================= given =================");
		{
			List<Object> parsedLines = new LinkedList<>();
			Object parsedLine;
			Iterator<String> iterator = lines.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				parsedLine =getParsedLogLineMap(line);
				parsedLines.add(parsedLine);
				out.println( parsedLine );
			}
			expected=parsedLines;
		}
		writeFile(expectResultFilePath,expected);
		System.out.println("================= when =================");
		// when
		Object actual = null;
		try(Stream<String> lineStream = Files.lines(Paths.get(filePath), Charset.defaultCharset())){
			List<Object> actualParsedLines = lineStream.parallel().filter( (String line)-> line.regionMatches( 0,"/",0,1 )  ).map(WASSystemOutLogParser::new).map(WASSystemOutLogParser::parse).peek( out::println ).collect(Collectors.toList());
			actual = actualParsedLines;
		}catch (IOException e) {
			e.printStackTrace();
		}
		writeFile(actulResultFilePath,actual);

		// then
		assertThat( actual, is( expected ) );
	}

}
