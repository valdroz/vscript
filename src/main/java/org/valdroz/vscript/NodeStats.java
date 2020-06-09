/*
 * Copyright 2020 Valerijus Drozdovas
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Valerijus Drozdovas
 * Created on 4/26/20
 */
public class NodeStats {
    private final Set<String> variables = new HashSet<>();
    private final Set<String> functions = new HashSet<>();

    public Collection<String> referencedVariables() {
        return Collections.unmodifiableSet(variables);
    }

    public Collection<String> referencedExtFunctions() {
        return Collections.unmodifiableSet(functions);
    }

    void referencedVariable(String variableName) {
        this.variables.add(variableName);
    }

    void referencedExtFunction(String functionName) {
        this.functions.add(functionName);
    }
}
