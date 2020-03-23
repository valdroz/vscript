/*
 * Copyright 2019 Valerijus Drozdovas
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Valerijus Drozdovas
 * Created on 12/16/19
 */
public abstract class AbstractFunction {

    private final String name;
    private final List<String> parameterNames = new ArrayList<>();

    /**
     * Custom function.
     *
     * @param signature Function signature as `name(paramName1, paramName2)` e.g. `max(input1, input2)`
     */
    public AbstractFunction(String signature) {
        String[] tokens = signature.trim().split("[()]");
        if (tokens.length == 0) {
            throw new EvaluationException("Invalid function signature \"" + signature + "\"");
        }
        name = tokens[0];
        if (tokens.length > 1) {
            tokens = tokens[1].trim().split(",");
            for (String pn : tokens) {
                parameterNames.add(pn.trim());
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterNames() {
        return Collections.unmodifiableList(parameterNames);
    }

    public abstract Variant execute(VariantContainer variantContainer);
}
