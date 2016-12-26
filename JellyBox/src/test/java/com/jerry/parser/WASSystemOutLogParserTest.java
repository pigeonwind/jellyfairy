package com.jerry.parser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jerry.pattern.RegexParser;
import com.jerry.pattern.StringExtractor;

public class WASSystemOutLogParserTest {
	private String logLine,logLine2,logLine3;
	private String testFilePath;
	RegexParser regexParser,fixedLengthParser;
	StringExtractor extractor;
	UnaryOperator<String> noActionOperator,removeSpaceChar;
	@Before
	public void setUp() throws Exception {
		testFilePath= System.getProperty("user.dir")+"/testResource/SystemOut.log";
		{
			logLine="[16. 12. 5   15:26:42:279 KST] 00000113 DataSourceCon E   DSRA8040I: Failed to connect to the DataSource \"\".  Encountered java.sql.SQLException: IO 오류: The Network Adapter could not establish the connection DSRA0010E: SQL 상태 = 08006, 오류 코드 = 17,002";
			logLine2="	at oracle.jdbc.driver.T4CConnection.logon(T4CConnection.java:673)";
			logLine3="[16. 12. 13   16:38:12:755 KST] 0000003b TransportAdap W   DCSV1117W: DCS Stack DefaultCoreGroup at Member AODECell\\AODmgr\\dmgr: The stream from Member AODECell\\AONode\\nodeagent has closed. The channnel is View|Ptp.";
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
				System.out.printf("line [%s] regex [%s] result [%s]\n",line,columnRegex,result);
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
				System.out.printf("line [%s] regex [%s] result [%s]\n",line,length,result);
				return result;
			};
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void parseTest() {
		System.out.printf("=================== %s START ===================\n", "parseTest");
		// given
		Object expected = getParsedLogLineMap(logLine);
		WASSystemOutLogParser  parser= new WASSystemOutLogParser(logLine);
		// when
		Object actual = parser.parse();
		// then
		assertThat(actual, is(expected));
	}
	String bracketPattern = "\\[(.*?)\\]";
	String leftBracketSpaceRightSpacePattern = "\\] (.*?) ";
	String processComponentPattern = leftBracketSpaceRightSpacePattern+"(.*?) ";
	String logLevelPattern = " ([W|I|E].) ";
	String codePattern = "([A-Z]{4}|[A-Z]{5})([0-9]{4}|[0-9]{5})([W|I|E].)";
	String logHeaderPattern =bracketPattern+"(.*?)"+logLevelPattern;
	private Object getParsedLogLineMap(String logLine) {
		Map<String, String> logLineMap = new HashMap<>();
		{
			RegexParser parser = fixedLengthParser;
			int fixedLengthDatePattern=10;
			UnaryOperator<String> preProcessor = (String line) -> ((String) regexParser.parse(line, bracketPattern)).replace("[", "");
			UnaryOperator<String> mainProcessor = (	String line) -> (String) parser.parse(line, fixedLengthDatePattern);
			UnaryOperator<String> postProcessor =removeSpaceChar;
			logLineMap.put("date", extractor.extract(logLine,preProcessor,mainProcessor,postProcessor));
		}
		{
			RegexParser parser = regexParser;
			String timestampPattern="([0-9]{2}):([0-9]{2}):([0-9]{2}):([0-9]{3})";
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
			String pattern=" ([W|I|E].) ";
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

}
