/*
* Copyright 2014 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.bertramlabs.plugins.hcl4j

import com.bertramlabs.plugins.hcl4j.symbols.Symbol
import spock.lang.Specification

/**
 * @author David Estes
 */
class HCLLexerSpec extends Specification {

	void "should generate symbols from hcl"() {
		given:
		def hcl = '''
variables {
  test = "test value"
}

service "my-service" {
  description = "my description"
  info {
    name = "my name"
    maxMemory = 1024
    priority = 0.1
    enabled = true
    positive = 1
    // TODO HCL does not have negative numbers by default
    negative = -1
    values = [ "hi", "mid", false, 1234, "lo", true]
    nil = null
    assignedVariable = my-service.info.maxMemory
    simpleConditional = enabled ? "yes" : "no"
    expr = 2345 + 321
    exprWithVar = 57 + negative * 5432
    exprWithParen = (-2109 + 3012)
    exprWithNegativeStart = -987 / 19
    isItTrueOrFalse = expr >= 5000
    disabled = !enabled
    negativePositive = -positive
    
    // Make sure this remains the last block for some time being (cf. README.md::KnownBugs)
    singleLineBlock { some = "value" }

//    exp = 1.5e-3
//    # The conditional is split to multiple lines on purpose!
//    conditional = enabled 
//    ? "yes" : "no"
//    multiLineName = "my
//    multiLine
//    Name"
  }
}
'''
		StringReader reader = new StringReader(hcl)
		HCLLexer lexer = new HCLLexer(reader)
		when:
		lexer.yylex()
		List<Symbol> rootBlocks = lexer.elementStack

		println rootBlocks?.collect{[it.getSymbolName(),it.getName()]}
		then:
		rootBlocks.size() == 2
		def variables = rootBlocks[0]
		variables.children.size() == 1
		def test = variables.children[0]
		test.children[0].value == "test value"

		rootBlocks[1].children.size() == 2
		def serviceBlock = rootBlocks[1]
		serviceBlock.name == "service"
		serviceBlock.blockNames == ["service", "my-service"]
		def description = serviceBlock.children[0]
		description.name == "description"
		description.children[0].value == "my description"

		def infoBlock = serviceBlock.children[1]
		infoBlock.name == "info"
		infoBlock.children.size() == 18
		def name = infoBlock.children[0]
		name.name == "name"
		name.children[0].value == "my name"
		def maxMemory = infoBlock.children[1]
		maxMemory.name == "maxMemory"
		maxMemory.children[0].value == "1024"
		def priority = infoBlock.children[2]
		priority.name == "priority"
		priority.children[0].value == "0.1"
		def enabled = infoBlock.children[3]
		enabled.name == "enabled"
		enabled.children[0].value == "true"
		def positive = infoBlock.children[4]
		positive.name == "positive"
		positive.children[0].value == "1"
		def negative = infoBlock.children[5]
		negative.name == "negative"
		negative.children[0].value == "1"
		def values = infoBlock.children[6]
		values.name == "values"
		values.children[0].children.size() == 6
		values.children[0].children*.value == ["hi", "mid", "false", "1234", "lo", "true"]
		def nil = infoBlock.children[7]
		nil.name == "nil"
		nil.children[0].value == null
		def assignedVariable = infoBlock.children[8]
		assignedVariable.name == "assignedVariable"
		assignedVariable.children[0].name == "my-service.info.maxMemory"
		def simpleConditional = infoBlock.children[9]
		simpleConditional.name == "simpleConditional"
		simpleConditional.children.size() == 3
		def expr = infoBlock.children[10]
		expr.name == "expr"
		expr.children.size() == 2
		def exprWithVar = infoBlock.children[11]
		exprWithVar.name == "exprWithVar"
		exprWithVar.children.size() == 3
		exprWithVar.children[1].name == "negative"
		def exprWithParen = infoBlock.children[12]
		exprWithParen.name == "exprWithParen"
		exprWithParen.children.size() == 2
		def exprWithNegativeStart = infoBlock.children[13]
		exprWithNegativeStart.name == "exprWithNegativeStart"
		exprWithNegativeStart.children.size() == 2
		def isItTrueOrFalse = infoBlock.children[14]
		isItTrueOrFalse.name == "isItTrueOrFalse"
		isItTrueOrFalse.children.size() == 2
		def disabled = infoBlock.children[15]
		disabled.name == "disabled"
		disabled.children.size() == 1
		def negativePositive = infoBlock.children[16]
		negativePositive.name == "negativePositive"
		negativePositive.children.size() == 1


		def singleLineBlock = infoBlock.children[-1]
		singleLineBlock.name == "singleLineBlock"
		singleLineBlock.children.size() == 1
	}

}
