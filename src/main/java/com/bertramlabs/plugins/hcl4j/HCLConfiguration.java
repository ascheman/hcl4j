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

import com.bertramlabs.plugins.hcl4j.symbols.HCLAttribute;
import com.bertramlabs.plugins.hcl4j.symbols.HCLBlock;

import java.util.List;

public class HCLConfiguration {
    private final List<HCLBlock> blocks;
    private final List<HCLAttribute> attributes;

    public HCLConfiguration(final List<HCLBlock> blocks, final List<HCLAttribute> attributes) {
        this.blocks = blocks;
        this.attributes = attributes;
    }

    public List<HCLBlock> getBlocks() {
        return blocks;
    }

    public List<HCLAttribute> getAttributes() {
        return attributes;
    }
}
