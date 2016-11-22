package com.jerry.util;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

public class JPathUtilTest {

	@Test
	public void getStringTest() throws Exception {
		System.out.printf("=================== %s START ===================\n", "getStringTest");
		// given
		JSONObject root = (JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(System.getProperty("user.dir")+"/config/message.json")));
		System.out.println(root);
		String path = "message.header.name";
		LinkedList<String> elementNames = new LinkedList<>(Arrays.asList(path.split("\\.")));
		System.out.println(elementNames);
		String expected;
		Object container=root;
		for (String elementName : elementNames) {
			System.out.println(elementName);
			if(container instanceof JSONObject){
				System.out.println("is JSONOBJECT");
				container = ((JSONObject) container).get(elementName);
			}else if(container instanceof JSONArray){
				System.out.println("is JSONARRAY");
			}else if(container instanceof String){
				System.out.println("is STRING");
			}else{
				System.out.println("what is this?");
			}
			System.out.printf("%s [%s]\n",elementName,container);
		}
		expected=(String) container;
		// when
		String actual=JPathUtil.getString(root,path);
		// then
		assertThat(actual, is(expected));
	}
}
