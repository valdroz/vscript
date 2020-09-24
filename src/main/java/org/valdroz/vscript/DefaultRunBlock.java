/*
 * Copyright 2000 Valerijus Drozdovas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.valdroz.vscript;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * Master Run Block container and executor.
 *
 * @author Valerijus Drozdovas
 */
public class DefaultRunBlock implements RunBlock {
    private final List<RunBlock> runtimeBlocks = Lists.newArrayList();
    private RunBlock parent;
    private final Map<String, AbstractFunction> functions = Maps.newHashMap();

    public DefaultRunBlock() {
    }

    public DefaultRunBlock withRunBlock(RunBlock runtimeBlock) {
        runtimeBlock.setParentRunBlock(this);
        runtimeBlocks.add(runtimeBlock);
        return this;
    }

    @Override
    public Variant execute(VariantContainer variantContainer) {
        Variant lastResult = Variant.nullVariant();
        for (RunBlock runBlock : runtimeBlocks) {
            runBlock.setParentRunBlock(this);
            lastResult = runBlock.execute(variantContainer);
        }
        return lastResult;
    }

    @Override
    public void setParentRunBlock(RunBlock runBlock) {
        this.parent = runBlock;
    }


    @Override
    public AbstractFunction resolveFunction(String name) {
        if (parent != null && !functions.containsKey(name)) {
            return parent.resolveFunction(name);
        }
        return functions.get(name);
    }

    /**
     * Register function with this runtime block.
     * @param func function instance.
     */
    public void registerFunction(AbstractFunction func) {
        functions.put(func.getName(), func);
    }

}
