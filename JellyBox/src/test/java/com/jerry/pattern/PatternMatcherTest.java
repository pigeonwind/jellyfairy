package com.jerry.pattern;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PatternMatcherTest {
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/**
	 * "%h", "([0-9]{1,3}) \. ([0-9]{1,3}) \. ([0-9]{1,3}) \. ([0-9]{1,3})", "clientHostIp"
	 * "%l", "*", "clientIdentityRFC1413"
	 * "%u", "*", "requestUserid"
	 * "%t", \\[*\\], "requestCompleteTime" -> [day/month/year:hour:minuate:secaond zone] 
	 * "\"%r\"",, "clientRequestLine"  -> method requestResource protocol
	 * "%>s", ,"status"
	 * "%b", ,"responseSizeExcludedHedersize"
	 * @throws Exception
	 */
	@Test
	public void patternMatchSuccessTest() throws Exception {
		
		System.out.printf("=================== %s START ===================\n", "patternMatchSuccessTest");
		Map<String,String> expected=new LinkedHashMap<>();
		// given
		String targetLogline = "127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] \"GET /apache_pb.gif HTTP/1.0\" 200 2326 \"http://www.example.com/start.html\" \"Mozilla/4.08 [en] (Win98; I ;Nav)\"";
		String patternFormat = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\"";
		String delimeter = " ";
		LinkedList<String> patternFormats=new LinkedList<>(Arrays.asList(patternFormat.split(delimeter)));
		LinkedList<String> patternFormats2=(LinkedList<String>) patternFormats.clone();
		System.out.println(patternFormats);
		PatternFormatParser parser = new PatternFormatParser(patternFormats2);
		expected = getExpectedData(patternFormats);

		String remainingString = targetLogline;
		String splitTargetSting;
		String[] container=null;
		for (String patternFormatIdicator : patternFormats) {
			//패턴에 따라서 나눔
			splitTargetSting=remainingString;
			container =splitData(splitTargetSting,patternFormatIdicator);
			//결정된 문자열 저장
			expected.put(patternFormatIdicator,container[0]);
			//남은 문자열 저장
			remainingString=container[1];
		}
		// when

		Object actual=parser.parse(targetLogline);
		// then
		System.out.println(expected);
		assertThat(actual,is(expected));
	}

	private Map<String, String> getExpectedData(LinkedList<String> patternFormats) {
		Map<String,String> map = new LinkedHashMap<>();
		map.put(patternFormats.pop(), "127.0.0.1");
		map.put(patternFormats.pop(), "-");
		map.put(patternFormats.pop(), "frank");
		map.put(patternFormats.pop(), "[10/Oct/2000:13:55:36 -0700]");
		map.put(patternFormats.pop(), "\"GET /apache_pb.gif HTTP/1.0\"");
		map.put(patternFormats.pop(), "200");
		map.put(patternFormats.pop(), "2326");
		map.put(patternFormats.pop(), "\"http://www.example.com/start.html\"");
		map.put(patternFormats.pop(), "\"Mozilla/4.08 [en] (Win98; I ;Nav)\"");
		return map;
	}

	private String[] splitData(String splitTargetSting, String patternFormatIdicator) {
		// TODO Auto-generated method stub
		return null;
	}

}
