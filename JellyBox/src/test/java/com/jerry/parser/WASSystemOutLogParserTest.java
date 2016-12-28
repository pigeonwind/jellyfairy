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
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jerry.parser.function.RegexParseOperator;
import com.jerry.parser.function.StringExtractor;
import com.jerry.util.function.StringParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WASSystemOutLogParserTest {
	private String logLine,logLine2,logLine3;
	private String logLine4;
	private String logLine5;
	private String testFilePath;
	private RegexParseOperator regexParseOperator;
	private StringExtractor extractor;
	private UnaryOperator<String> noActionOperator,removeSpaceChar;
	private String testFilePath2;

	private StringParser parser;
	//2s 912ms
	@Before
	public void setUp() throws Exception {
		testFilePath= System.getProperty("user.dir")+"/testResource/SystemOut.log";
		testFilePath2= System.getProperty("user.dir")+"/testResource/MES2.App02_SystemOut.log";
		parser = new WASSystemOutLogParser( testFilePath2 );
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
//				out.printf("line [%s] regex [%s] result [%s]\n",line,columnRegex,result);
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
		out.println( expected );
		// when
		Object actual = parser.parseString( target );

		// then
		assertThat(actual, is(expected));
	}
	@Test
	public final void parse3Test() {
		out.printf("=================== %s START ===================\n", "parse3Test");
		// given
		String target=logLine3;
		Object expected = getParsedLogLineMap(target);
		out.println( expected );
		// when
		Object actual = parser.parseString( target );

		// then
		assertThat(actual, is(expected));
	}
	@Test
	public final void parse4Test() {
		out.printf("=================== %s START ===================\n", "parse4Test");
		// given
		String target=logLine4;
		Object expected = getParsedLogLineMap(target);
		out.println( expected );
		// when
		Object actual = parser.parseString( target );

		// then
		assertThat(actual, is(expected));
	}
	@Test
	public final void parse5Test() {
		out.printf( "=================== %s START ===================\n", "parse5Test" );
		// given
		String target = logLine5;
		Object expected = getParsedLogLineMap( target );
		out.println( expected );
		// when
		Object actual = parser.parseString( target );

		// then
		assertThat( actual, is( expected ) );
	}
	private String bracketPattern = "\\[(.*?)\\]";

	private String leftBracketSpaceRightSpacePattern = "\\] (.*?) ";
	private String processComponentPattern = leftBracketSpaceRightSpacePattern+"(.*?) ";
	private String logLevelPattern = " ([W|I|E|A]{1}) ";
	private String logHeaderPattern =bracketPattern+"(.*?)"+logLevelPattern;
	private Object getParsedLogLineMap(String logLine) {
		Map<String, String> logLineMap = new HashMap<>();
		{
			RegexParseOperator parser = regexParseOperator;
			String datePattern = "(([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2})((\\. )|(/))([0-9]{1}|[0-9]{2}) )";
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParseOperator.parse(line, bracketPattern)).replace("[", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, datePattern);
			UnaryOperator<String> postProcessor =removeSpaceChar;
			logLineMap.put("date", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParseOperator parser = regexParseOperator;
			String timestampPattern="([0-9]{1}|[0-9]{2}):([0-9]{2}):([0-9]{2}):([0-9]{3})";
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParseOperator.parse(line, bracketPattern)).replace("[", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, timestampPattern);
			UnaryOperator<String> postProcessor =noActionOperator;
			logLineMap.put("time", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParseOperator parser = regexParseOperator;
			String pattern=leftBracketSpaceRightSpacePattern;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =(String line)-> line.replace("] ", "");
			logLineMap.put("threadName", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParseOperator parser = regexParseOperator;
			String pattern=" (.*?) ";
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParseOperator.parse(line, processComponentPattern)).replace("] ", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =removeSpaceChar;
			logLineMap.put("processComponent", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParseOperator parser = regexParseOperator;
			String pattern=logLevelPattern;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =removeSpaceChar;
			logLineMap.put("level", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParseOperator parser = regexParseOperator;
			String pattern= "([A-Z]{4}|[A-Z]{5})([0-9]{4}|[0-9]{5})([W|I|E|A]{1})";
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, pattern);
			UnaryOperator<String> postProcessor =(String line)->line.replace(":", "");
			logLineMap.put("code", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParseOperator parser = regexParseOperator;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor =(String line) -> line.replace((String) parser.parse(line,logHeaderPattern), "");
			UnaryOperator<String> postProcessor =noActionOperator;
			logLineMap.put("content", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
	String serverNamePattern =  "([A-Z0-9]+)\\.(.*?)_";
			RegexParseOperator parser = regexParseOperator;
			UnaryOperator<String> preProcessor = noActionOperator;
			UnaryOperator<String> mainProcessor =(String line) -> (String) parser.parse(line,serverNamePattern);
			UnaryOperator<String> postProcessor = (String line )-> line.replace( "_","" );
			logLineMap.put("serverName", extractor.extract(testFilePath2,preProcessor,mainProcessor,postProcessor));
		}
		return logLineMap;
	}
	@Test
	public void multiLineParseTest() throws Exception {
		out.printf( "========= %sTest() START =========\n", "multiLineParse" );
		// given
		String filePath=testFilePath;
		List<String> lines =getDummyDatas(filePath,(String line)-> line.regionMatches( 0,"[",0,1 ) );
//		System.out.println(lines);
		Object expected;
		System.out.println("================= given =================");
		{
			expected=lines.parallelStream().map( this::getParsedLogLineMap ).collect( Collectors.toList() );
		}
		System.out.println("================= when =================");
		// when
		Object actual=null;
		try(Stream<String> lineStream = Files.lines(Paths.get(filePath), Charset.defaultCharset())){
			actual = lineStream.filter( (String line)-> line.regionMatches( 0,"[",0,1 )  ).map( parser::parseString ).peek( out::println ).collect( Collectors.toList() );
		}catch (IOException e) {
			e.printStackTrace();
		}

		// then
		assertThat( actual, is( expected ) );
	}

	private List<String> getDummyDatas(String testFilePath,Predicate<String> predicate) {
		List<String> result=new LinkedList<>();
		try(Stream<String> lines = Files.lines( Paths.get(testFilePath), Charset.defaultCharset())){
			result = lines.parallel().filter( predicate ).collect( Collectors.toList());
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
		List<String> lines =getDummyDatas(filePath,(String line)-> line.regionMatches( 0,"/",0,1 ) );
		Object expected;
		System.out.println("================= given =================");
		{
			expected=lines.parallelStream().map( this::getParsedLogLineMap ).collect( Collectors.toList() );
		}
		System.out.println("================= when =================");
		// when
		Object actual = null;
		try(Stream<String> lineStream = Files.lines(Paths.get(filePath), Charset.defaultCharset())){
			actual = lineStream.parallel().filter( (String line)-> line.regionMatches( 0,"/",0,1 )  ).map( parser::parseString ).peek( out::println ).collect( Collectors.toList() );
		}catch (IOException e) {
			e.printStackTrace();
		}
		// then
		assertThat( actual, is( expected ) );
	}

}
