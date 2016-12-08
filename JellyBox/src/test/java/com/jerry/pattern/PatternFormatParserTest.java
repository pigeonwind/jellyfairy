package com.jerry.pattern;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PatternFormatParserTest {
	String patternFormat;
	@Before
	public void setUp() throws Exception {
		patternFormat = "%h %l %u %t \"%r\" %>s %b \"%{Referer}i\" \"%{User-agent}i\"";
	}
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testParsing() throws Exception {
		
		System.out.printf("=================== %s START ===================\n", "testParsing");
		// given
		String[] delimiters = {" "};
		PatternDefine pattern = new PatternDefine("%h",delimiters,"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		pattern.getDelimiters();
		// when
		// then
		fail("Not yet implements");
	}
}
