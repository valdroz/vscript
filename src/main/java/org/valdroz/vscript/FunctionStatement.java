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
 * Runnable function block.
 *
 * @author Valerijus Drozdovas
 */
public class FunctionStatement extends MasterRunBlock {
    private final String name;
    private List<String> parameterNames = new ArrayList<>();
    private List<Variant> parameterValues;

    public FunctionStatement(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public FunctionStatement withParameterName(String paramVarName) {
        parameterNames.add(paramVarName);
        return this;
    }

    void setParameterValues(List<Variant> paramValuesArray) {
        if (paramValuesArray != null) {
            parameterValues = paramValuesArray;
        } else {
            parameterValues = new ArrayList<>();
        }
    }

    @Override
    public void run(VariantContainer variantContainer) {
        execute(variantContainer);
    }

    public Variant getParamValue(int index) {
        if ( index >= this.parameterValues.size() ) {
            return this.parameterValues.get(index);
        }
        return new Variant();
    }

    public Variant getParamValueByName(String paramName) {
        for (int i=0; i<this.parameterNames.size(); ++i) {
            if (paramName.equalsIgnoreCase(this.parameterNames.get(i))) {
                return getParamValue(i);
            }
        }
        return new Variant();
    }

    public FunctionStatement withBody(RunBlock body) {
        withRunBlock(body);
        return this;
    }

    /**
     * Execute and get value
     * @param variantContainer
     * @return
     */
    public Variant execute(VariantContainer variantContainer) {
        LocalVariantContainer local = new LocalVariantContainer(variantContainer);
        local.setVariant(getName(), new Variant());

        for (int i=0; i<this.parameterNames.size(); ++i) {
            String pn = this.parameterNames.get(i);
            Variant pv;
            if ( i < this.parameterValues.size()) {
                pv = this.parameterValues.get(i);
            } else {
                pv = new Variant();
            }
            local.setVariant(pn, pv);
        }
        super.run(local);
        return local.getVariant(getName());
    }


}
