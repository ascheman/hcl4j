package com.bertramlabs.plugins.hcl4j.RuntimeSymbols;

public class Function extends EvalSymbol{
	public Function(String name, Integer line, Integer column,Long position) {
		super(name,line,column,position);
	}

	public String getSymbolName() {
		return "Function";
	}
}
