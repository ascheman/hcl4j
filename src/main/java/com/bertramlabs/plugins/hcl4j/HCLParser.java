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
package com.bertramlabs.plugins.hcl4j;

import com.bertramlabs.plugins.hcl4j.RuntimeSymbols.EvalSymbol;
import com.bertramlabs.plugins.hcl4j.RuntimeSymbols.PrimitiveType;
import com.bertramlabs.plugins.hcl4j.RuntimeSymbols.Variable;
import com.bertramlabs.plugins.hcl4j.symbols.HCLArray;
import com.bertramlabs.plugins.hcl4j.symbols.HCLAttribute;
import com.bertramlabs.plugins.hcl4j.symbols.HCLBlock;
import com.bertramlabs.plugins.hcl4j.symbols.HCLMap;
import com.bertramlabs.plugins.hcl4j.symbols.HCLValue;
import com.bertramlabs.plugins.hcl4j.symbols.Symbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the Hashicorp Configuration Language (HCL). This is the primary endpoint and converts the HCL syntax into a {@link Map}.
 * This parser utilizes a lexer to generate symbols based on the HCL spec. String interpolation is not evaluated at this point in the parsing.
 *
 * <p>
 *     Below is an example of how HCL might be parsed.
 * </p>
 * <pre>
 *     {@code
 *     import com.bertramlabs.plugins.hcl4j.HCLParser;
 *
 *     File terraformFile = new File("terraform.tf");
 *
 *     Map results = new HCLParser().parse(terraformFile);
 *     }
 * </pre>
 * @author David Estes
 */
public class HCLParser {


	public HCLParser() {
		// TODO document why this constructor is empty
	}

	/**
	 * Parses terraform configuration language from a String
	 * @param input String input containing HCL syntax
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(String input) throws HCLParserException, IOException {
		return parse(input,false);
	}

	/**
	 * Parses terraform configuration language from a String
	 * @param input String input containing HCL syntax
	 * @param ignoreParserExceptions if set to true, we ignore any parse exceptions and still return the symbol map
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(String input, Boolean ignoreParserExceptions) throws HCLParserException, IOException {
		StringReader reader = new StringReader(input);
		return parse(reader,ignoreParserExceptions);
	}


	/**
	 * Parses terraform syntax as it comes from a File.
	 * @param input A source file to process with a default charset of UTF-8
	 * @param ignoreParserExceptions if set to true, we ignore any parse exceptions and still return the symbol map
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(File input, Boolean ignoreParserExceptions) throws HCLParserException, IOException {
		return parse(input, StandardCharsets.UTF_8.toString(),ignoreParserExceptions);
	}

	/**
	 * Parses terraform syntax as it comes from a File.
	 * @param input A source file to process with a default charset of UTF-8
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(File input) throws HCLParserException, IOException {
		return parse(input, StandardCharsets.UTF_8.toString(),false);
	}


	/**
	 * Parses terraform syntax as it comes from a File.
	 * closed at the end of the parse operation (commonly via wrapping in a finally block)
	 * @param input A source file to process
	 * @param cs A charset
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(File input, Charset cs) throws HCLParserException, IOException{
		try (InputStream is = new FileInputStream(input)) {
			return parse(is, cs);
		}
	}


	/**
	 * Parses terraform syntax as it comes from a File.
	 * @param input A source file to process
	 * @param charsetName The name of a supported charset
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 * @throws UnsupportedEncodingException If the charset ( UTF-8 by default if unspecified) encoding is not supported
	 */
	public Map<String,Object> parse(File input, String charsetName) throws HCLParserException, IOException {
		return parse(input,charsetName,false);
	}

	/**
	 * Parses terraform syntax as it comes from a File.
	 * @param input A source file to process
	 * @param charsetName The name of a supported charset
	 * @param ignoreParserExceptions if set to true, we ignore any parse exceptions and still return the symbol map
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 * @throws UnsupportedEncodingException If the charset ( UTF-8 by default if unspecified) encoding is not supported
	 */
	public Map<String,Object> parse(File input, String charsetName, Boolean ignoreParserExceptions) throws HCLParserException, IOException {
		try (InputStream is = new FileInputStream(input)) {
			return parse(is, charsetName, ignoreParserExceptions);
		}
	}



	/**
	 * Parses terraform syntax as it comes from an input stream. The end user is responsible for ensuring the stream is
	 * closed at the end of the parse operation (commonly via wrapping in a finally block)
	 * @param input Streamable input of text going to the lexer
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(InputStream input) throws HCLParserException, IOException {
		return parse(input,"UTF-8");
	}

	/**
	 * Parses terraform syntax as it comes from an input stream. The end user is responsible for ensuring the stream is
	 * closed at the end of the parse operation (commonly via wrapping in a finally block)
	 * @param input Streamable input of text going to the lexer
	 * @param cs CharSet with which to read the contents of the stream   (default UTF-8)
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(InputStream input, Charset cs) throws HCLParserException, IOException {

		InputStreamReader reader;
		if(cs != null) {
			reader = new InputStreamReader(input,cs);
		} else {
			reader = new InputStreamReader(input, StandardCharsets.UTF_8);
		}
		return parse(reader);
	}


	/**
	 * Parses terraform syntax as it comes from an input stream. The end user is responsible for ensuring the stream is
	 * closed at the end of the parse operation (commonly via wrapping in a finally block)
	 * @param input Streamable input of text going to the lexer
	 * @param charsetName String lookup of the character set this stream is providing (default UTF-8)
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 * @throws UnsupportedEncodingException If the charset ( UTF-8 by default if unspecified) encoding is not supported.
	 */
	public Map<String,Object> parse(InputStream input, String charsetName) throws HCLParserException, IOException {
		return parse(input,charsetName,false);
	}

	/**
	 * Parses terraform syntax as it comes from an input stream. The end user is responsible for ensuring the stream is
	 * closed at the end of the parse operation (commonly via wrapping in a finally block)
	 * @param input Streamable input of text going to the lexer
	 * @param charsetName String lookup of the character set this stream is providing (default UTF-8)
	 * @param ignoreParserExceptions if set to true, we ignore any parse exceptions and still return the symbol map
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 * @throws UnsupportedEncodingException If the charset ( UTF-8 by default if unspecified) encoding is not supported.
	 */
	public Map<String,Object> parse(InputStream input, String charsetName, Boolean ignoreParserExceptions) throws HCLParserException, IOException {

		InputStreamReader reader;
		if(charsetName != null) {
			reader = new InputStreamReader(input,charsetName);
		} else {
			reader = new InputStreamReader(input,StandardCharsets.UTF_8.toString());
		}
		return parse(reader,ignoreParserExceptions);
	}

	/**
	 * Parses terraform configuration language from a Reader
	 * @param reader A reader object used for absorbing various streams or String variables containing the hcl code
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(Reader reader) throws HCLParserException, IOException {
		return parse(reader,false);
	}

	/**
	 * Parses terraform configuration language from a Reader
	 * @param reader A reader object used for absorbing various streams or String variables containing the hcl code
	 * @param ignoreParserExceptions if set to true, we ignore any parse exceptions and still return the symbol map
	 * @return Mapped result of object tree coming from HCL (values of keys can be variable).
	 * @throws HCLParserException Any type of parsing errors are returned as this exception if the syntax is invalid.
	 * @throws IOException In the event the reader is unable to pull from the input source this exception is thrown.
	 */
	public Map<String,Object> parse(Reader reader, Boolean ignoreParserExceptions) throws HCLParserException, IOException {
		List<Symbol> rootBlocks = getRootBlocks(reader, ignoreParserExceptions);

		//Time to parse the AST Tree into a Map
		Map<String,Object> result = new LinkedHashMap<>();

		for(Symbol currentElement : rootBlocks) {
			processSymbol(currentElement, result);

		}
		return result;
	}

	public HCLConfiguration parseConfiguration(File input) throws HCLParserException, IOException {
		return parseConfiguration(input, "UTF-8");
	}

	public HCLConfiguration parseConfiguration(File input, String charsetName) throws HCLParserException, IOException {
		return parseConfiguration(input, Charset.forName(charsetName));
	}

	public HCLConfiguration parseConfiguration(File input, Charset cs) throws HCLParserException, IOException{
		try (InputStream is = new FileInputStream(input)) {
			return parseConfiguration(is, cs);
		}
	}

	public HCLConfiguration parseConfiguration(InputStream input, Charset cs) throws HCLParserException, IOException {

		InputStreamReader reader;
		if(cs != null) {
			reader = new InputStreamReader(input,cs);
		} else {
			reader = new InputStreamReader(input,StandardCharsets.UTF_8.toString());
		}
		return parseConfiguration(reader);
	}

	public HCLConfiguration parseConfiguration(Reader reader) throws HCLParserException,
			IOException {
		List<HCLBlock> blocks = new ArrayList<>();
		final List<HCLAttribute> attributes = new ArrayList<>();

		List<Symbol> rootBlocks = getRootBlocks(reader, false);
		for (Symbol symbol : rootBlocks) {
			if (symbol instanceof HCLAttribute) {
				attributes.add((HCLAttribute) symbol);
			} else if (symbol instanceof HCLBlock) {
				blocks.add((HCLBlock) symbol);
			} else {
				throw new HCLParserException("The root blocks must only consist of Blocks and Attributes");
			}
		}

		return new HCLConfiguration(blocks, attributes);
	}

	private List<Symbol> getRootBlocks(Reader reader, boolean ignoreParserExceptions) throws IOException,
			HCLParserException {
		HCLLexer lexer = new HCLLexer(reader);
		List<Symbol> rootBlocks;

		if(ignoreParserExceptions) {
			try {
				lexer.yylex();
			} catch(Exception ex) {
				//TODO: Log the exception
			}
		} else {
			lexer.yylex();
		}


		rootBlocks = lexer.elementStack;
		return rootBlocks;
	}


	private Object processSymbol(Symbol symbol, Map<String,Object> mapPosition) throws HCLParserException {

		if(symbol instanceof HCLBlock) {
			HCLBlock block = (HCLBlock)symbol;
			for(int counter = 0 ; counter < block.blockNames.size() ; counter++) {
				String blockName = block.blockNames.get(counter);
				if(mapPosition.containsKey(blockName)) {
					if(counter == block.blockNames.size() - 1 && mapPosition.get(blockName) instanceof Map) {
						List<Map<String,Object>> objectList = new ArrayList<>();
						Map<String,Object> addedObject = new LinkedHashMap<>();
						objectList.add((Map<String, Object>)mapPosition.get(blockName));
						objectList.add(addedObject);
						mapPosition.put(blockName,objectList);
						mapPosition = addedObject;
					} else if(mapPosition.get(blockName) instanceof Map) {
						mapPosition = (Map<String,Object>) mapPosition.get(blockName);
					} else if(counter == block.blockNames.size() - 1 && mapPosition.get(blockName) instanceof List) {
						Map<String,Object> addedObject = new LinkedHashMap<>();
						((List<Map<String,Object>>)mapPosition.get(blockName)).add(addedObject);
						mapPosition = addedObject;
					} else {
						if(mapPosition.get(blockName) instanceof List) {
							throw new HCLParserException("HCL Block expression scope traverses an object array");
						} else {
							throw new HCLParserException("HCL Block expression scope traverses an object value");
						}
					}
				} else {
					mapPosition.put(blockName,new LinkedHashMap<String,Object>());
					mapPosition = (Map<String,Object>) mapPosition.get(blockName);
				}
			}
			if(symbol.getChildren() != null) {
				for(Symbol child : block.getChildren()) {
					processSymbol(child,mapPosition);
				}
			}
			return mapPosition;
		} else if(symbol instanceof HCLMap) {
			Map<String,Object> nestedMap = new LinkedHashMap<>();
			if(symbol.getChildren() != null) {
				for(Symbol child : symbol.getChildren()) {
					processSymbol(child, nestedMap);
				}
			}
			return nestedMap;
		} else if(symbol instanceof HCLArray) {
			if(symbol.getChildren() != null) {
				List<Object> objectList = new ArrayList<>();
				for(Symbol child : symbol.getChildren()) {
					Map<String,Object> nestedMap = new LinkedHashMap<>();
					Object result = processSymbol(child,nestedMap);
					objectList.add(result);
				}
				return objectList;
			} else {
				return null;
			}
		} else if(symbol instanceof HCLValue) {
			return processValue((HCLValue) symbol);
		} else if(symbol instanceof PrimitiveType) {
			return symbol;
		} else if(symbol instanceof EvalSymbol) {
			return processEvaluation((EvalSymbol) symbol);
		} else if(symbol instanceof HCLAttribute) {
			Map<String,Object> nestedMap = new LinkedHashMap<>();
			if(symbol.getChildren().size() > 0) {
				Object results = processSymbol(symbol.getChildren().get(0),nestedMap);
				mapPosition.put(symbol.getName(),results);
			} else {
				mapPosition.put(symbol.getName(),null);
			}

			return mapPosition;
		}
		return null;
	}


	protected Object processValue(HCLValue value) throws HCLParserException {
		if(value.getType().equals("string")) {
			return value.getValue();
		} else if (value.getType().equals("boolean")) {
			if(value.getValue().equals("true")) {
				return Boolean.TRUE;
			} else {
				return Boolean.FALSE;
			}
		} else if (value.getType().equals("null")) {
			return null;
		} else if (value.getType().equals("number")) {
			try {
				return Double.parseDouble((String) (value.getValue()));
			} catch(NumberFormatException ex) {
				throw new HCLParserException("Error Parsing Numerical Value in HCL Attribute ", ex);
			}
		} else {
			throw new HCLParserException("HCL Attribute value not recognized by parser (not implemented yet).");
		}
	}


	protected Object processEvaluation(EvalSymbol evalSymbol) {
		if(evalSymbol instanceof Variable) {
			return evalSymbol;
		} else {
			return null;
		}
	}
}
