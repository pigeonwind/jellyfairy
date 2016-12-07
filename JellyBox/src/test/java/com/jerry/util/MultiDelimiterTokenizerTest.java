package com.jerry.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MultiDelimiterTokenizerTest {
	String targetData;
	@Before
	public void setUp() throws Exception {
		targetData="127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] \"GET /apache_pb.gif HTTP/1.0\" 200 2326 \"http://www.example.com/start.html\" \"Mozilla/4.08 [en] (Win98; I ;Nav)\"";
	}

	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void popByRegex() throws Exception {
		System.out.printf("=================== %s START ===================\n", "popByRegex");
		// given
		String expected="10/Oct/2000:13:55:36 -0700";
		MultiDelimiterTokenizer tokenizer = new MultiDelimiterTokenizer(targetData);
		// when
		String actual = tokenizer.pop("[","]");
		// then
		assertThat(actual, is(expected));
	}
	@Test
	public void hasNextTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "hasNextTest");
		// given
		String expected="127.0.0.1";
		MultiDelimiterTokenizer tokenizer = new MultiDelimiterTokenizer(targetData);
		// when
		String actual = tokenizer.hasNext(" ");
		// then
		assertThat(actual, is(expected));
	}
}

