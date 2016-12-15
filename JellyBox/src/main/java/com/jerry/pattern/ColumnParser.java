package com.jerry.pattern;

public interface ColumnParser {
	public Object parse(String line, Object columnRegex);
}
