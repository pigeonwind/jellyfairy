package com.jerry.util;

import java.util.function.BiFunction;

/**
 * @author jerry
 *
 */
public class MultiDelimiterTokenizer {
	private int tokenCount;
	private String surplusString;
	BiFunction<String, Character, Integer> getIndexOfFromZero;
	BiFunction<String,Integer,String> headSubString,tailSubString;
	public MultiDelimiterTokenizer(String surplusString) {
		this.surplusString = surplusString;
		this.tokenCount = 0;
		getIndexOfFromZero = (String target, Character character) -> target.indexOf(character);
		tailSubString = (String string, Integer startOffset)-> string.substring(startOffset, string.length());
		headSubString = (String string, Integer endOffset)-> string.substring(0, endOffset);
	}

	public String pop(String startDelimiter, String endDelimter) {
		String targetString = surplusString;
		int startDelimiterOffset, endDelimiterOffset;
		startDelimiterOffset = getOffset(surplusString, startDelimiter);
		endDelimiterOffset = getOffset(surplusString, endDelimter);
		
		setSurplusStringWithtokenCountUp(subString(targetString, startDelimiterOffset, headSubString)+subString(targetString,endDelimiterOffset + 1,tailSubString));
//		System.out.printf("remain [%s], return[%s], tokenCount[%d]\n", surplusString, result, tokenCount);
		return targetString.substring(startDelimiterOffset + 1, endDelimiterOffset);
	}
	public String hasNext(String delimter) {
		String targetString = surplusString;
		int delimitterOffset;
		delimitterOffset =getOffset(targetString, delimter);
		
		setSurplusStringWithtokenCountUp(subString(targetString, delimitterOffset + 1,tailSubString));
//		System.out.printf("remain [%s], return[%s], tokenCount[%d]\n", surplusString, result, tokenCount);
		return subString(targetString, delimitterOffset, headSubString);
	}
	private void setSurplusStringWithtokenCountUp(String surplusString) {
		this.surplusString = surplusString;
		tokenCount++;
	}
	private String subString(String targetString, int startOffset, BiFunction<String, Integer, String> biFunction) {
		return biFunction.apply(targetString, startOffset);
	}
	private int getOffset(String targetString, String delmiter) {
		return targetString.indexOf(delmiter.charAt(0));
	}
	public int getTokenCount(){
		return this.tokenCount;
	}


}
