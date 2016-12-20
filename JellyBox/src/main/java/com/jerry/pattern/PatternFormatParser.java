package com.jerry.pattern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.jerry.util.MultiDelimiterTokenizer;

public class PatternFormatParser {
	LinkedList<String> patternFormats;
	MultiDelimiterTokenizer tokenizer;
	
	public PatternFormatParser(LinkedList<String> patternFormats) {
		this.patternFormats=patternFormats;
	}

	public Object parse(String targetLogline) {
		tokenizer = new MultiDelimiterTokenizer(targetLogline);
		Map<String,String> lineMap=new HashMap<>();
		String pattern;
		Map<String,RegexParser> parserMap = new HashMap<>();
		
		String matchedString;
		while(!patternFormats.isEmpty()){
			pattern = patternFormats.pop();
		}
		return lineMap;
	}
}