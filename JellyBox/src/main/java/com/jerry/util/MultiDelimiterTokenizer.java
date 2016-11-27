package com.jerry.util;

import java.util.StringTokenizer;

/**
 * @author jerry
 *
 */
public class MultiDelimiterTokenizer {
	private int tokenCount;
	private String surplusString;
	
	public MultiDelimiterTokenizer(String surplusString) {
		this.surplusString = surplusString;
		this.tokenCount=0;
	}
	public String pop(String startDelimiter, String endDelimter) {
		String targetString,result;
		targetString =  surplusString;
		{
			int startDelimiterOffset = targetString.indexOf(startDelimiter.charAt(0));
			int endelimiterOffset = targetString.indexOf(endDelimter.charAt(0));
			result = targetString.substring(startDelimiterOffset+1, endelimiterOffset);
		}
		targetString = targetString.replace(result, "");
		System.out.println(targetString);
		targetString=targetString.replaceFirst("\\[", "");
		System.out.println(targetString);
		targetString=targetString.replaceFirst("\\]", "");
		System.out.println(targetString);
		surplusString =  targetString;
		{
			tokenCount++;
			System.out.printf("remain [%s], return[%s], tokenCount[%d]\n",surplusString,result,tokenCount);
		}
		return result;
	}
	public String hasNext(String delimter){
		String targetString,result;
		targetString =  surplusString;
		{
			result = targetString.split(delimter)[0];
		}
		surplusString =  targetString.replaceFirst(result, "").replaceFirst(delimter, "");
		{
			tokenCount++;
			System.out.printf("remain [%s], return[%s], tokenCount[%d]\n",surplusString,result,tokenCount);
		}
		return result;
	}
	
}
