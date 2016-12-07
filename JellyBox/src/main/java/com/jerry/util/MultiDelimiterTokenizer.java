package com.jerry.util;

import java.util.StringTokenizer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author jerry
 *
 */
public class MultiDelimiterTokenizer {
	private int tokenCount;
	private String surplusString;
	BiFunction <String,Character,Integer> getIndexOfFromZero;
	
	public MultiDelimiterTokenizer(String surplusString) {
		this.surplusString = surplusString;
		this.tokenCount=0;
		getIndexOfFromZero = (String target, Character character) -> target.indexOf(character );
	}

	public String pop(String startDelimiter, String endDelimter) {
		String targetString,head,result,tail;
		targetString =  surplusString;
		int startDelimiterOffset,endDelimiterOffset;
			startDelimiterOffset =  targetString.indexOf(startDelimiter.charAt(0));
			endDelimiterOffset = targetString.indexOf(endDelimter.charAt(0));

			result = targetString.substring(startDelimiterOffset+1, endDelimiterOffset);
			head = targetString.substring( 0,startDelimiterOffset );
			tail = targetString.substring( endDelimiterOffset+1, targetString.length() );
			surplusString =  head.concat( tail );
			tokenCount++;
			System.out.printf("remain [%s], return[%s], tokenCount[%d]\n",surplusString,result,tokenCount);
		return result;
	}
	public String hasNext(String delimter){
		String targetString,result,tail;
		targetString =  surplusString;
		int delimitterOffset;
			delimitterOffset = targetString.indexOf(delimter.charAt(0));
			result = targetString.substring(0 ,delimitterOffset );
		    tail = targetString.substring( delimitterOffset+1,targetString.length()  );
		    surplusString = tail;
			tokenCount++;
			System.out.printf("remain [%s], return[%s], tokenCount[%d]\n",surplusString,result,tokenCount);
		return result;
	}
	
}
