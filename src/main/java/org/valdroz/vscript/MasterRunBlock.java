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

import java.util.*;

/**
 * RunBlock container and executor.
 *
 * @author Valerijus Drozdovas
 */
public class MasterRunBlock implements RunBlock {
    private List<RunBlock> runBlocks = new ArrayList<>();
    private MasterRunBlock masterRunBlock;
    private Map<String, FunctionStatement> functions = new HashMap<>();

    /**
     * Class default constructor
     */
    public MasterRunBlock() {
    }

    /**
     * Class default constructor
     */
    public MasterRunBlock(MasterRunBlock parentBlock) {
        this.masterRunBlock = parentBlock;
    }


    public MasterRunBlock withRunBlock(RunBlock codeItem) {
        codeItem.setMasterRunBlock(this);
        runBlocks.add(codeItem);
        return this;
    }

    @Override
    public void run(VariantContainer variantContainer) {
        for (RunBlock runBlock : runBlocks) {
            runBlock.setMasterRunBlock(this);
            runBlock.run(variantContainer);
        }
    }

    @Override
    public void setMasterRunBlock(MasterRunBlock masterRunBlock) {
        this.masterRunBlock = masterRunBlock;
    }


    Variant callFunction(String funcName, List<Variant> parameters, VariantContainer variantContainer) {
        if (masterRunBlock != null) {
            return masterRunBlock.callFunction(funcName, parameters, variantContainer);
        }

        if (functions != null) {
            FunctionStatement func = functions.get(funcName);
            if (func != null) {
                func.setMasterRunBlock(this);
                func.setParameterValues(parameters);
                return func.execute(variantContainer);
            }
        }

        return null;
    }

    /**
     * Add prepared function to runtime block.
     */
    public void addFunctionStatement(FunctionStatement func) {
        functions.put(func.getName(), func);
    }

    /**
     * Add externally prepared functions to this runtime block.
     */
    public void copyFunctionsFrom(MasterRunBlock rs) {
        if (rs.functions != null) {
            Iterator it = rs.functions.values().iterator();
            for (FunctionStatement func: rs.functions.values()) {
                addFunctionStatement(func);
            }
        }
    }


}
