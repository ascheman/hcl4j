package com.bertramlabs.plugins.hcl4j.RuntimeSymbols;

public class StringPrimitiveType extends PrimitiveType {
	public StringPrimitiveType(Integer line, Integer column,Long position) {
		super("string",line,column,position);
	}

	public String getSymbolName() {
		return "StringPrimitiveType";
	}
}
