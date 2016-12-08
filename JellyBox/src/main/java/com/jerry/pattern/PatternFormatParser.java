package com.jerry.pattern;

import java.util.LinkedList;

import com.jerry.util.MultiDelimiterTokenizer;

public class PatternFormatParser {
	LinkedList<String> patternFormats;
	MultiDelimiterTokenizer tokenizer;
	
	public PatternFormatParser(LinkedList<String> patternFormats) {
		this.patternFormats=patternFormats;
	}

	public Object parse(String targetLogline) {
		tokenizer = new MultiDelimiterTokenizer(targetLogline);
		String pattern;
		while(!patternFormats.isEmpty()){
			pattern = patternFormats.pop();
			System.out.println(pattern);
		}
		
		return null;
	}
}