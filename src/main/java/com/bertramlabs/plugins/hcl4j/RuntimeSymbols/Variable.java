package com.bertramlabs.plugins.hcl4j.RuntimeSymbols;

public class Variable extends EvalSymbol {
	public Variable(String name, Integer line, Integer column,Long position) {
		super(name,line,column,position);
	}

	public String getSymbolName() {
		return "Variable";
	}

}
