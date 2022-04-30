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
package com.bertramlabs.plugins.hcl4j.symbols;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Class representation of an attributes value. This could be a generic type like a "string", "number", "boolean", "array" (see {@link HCLArray}, or "map" (see {@link HCLMap}).
 * This is an internal parser lexer class and should not be needed externally.
 * @author David Estes
 */
public class HCLValue extends GenericSymbol {
	public String type;
	public Object value;
	public HCLValue parent;
	static final Logger LOG = LoggerFactory.getLogger(HCLValue.class);

	public String getSymbolName() {
		return "Value";
	}


	public HCLValue(String type, Object value, Integer line, Integer column,Long position) {
		super("value",line,column,position);
		LOG.debug ("Symbol type: '{}' / Value: '{}'", type, value);

		this.type = type;
		this.value = value;
	}
}