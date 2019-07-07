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


import java.util.HashMap;
import java.util.Map;

/**
 * Default Variant Container implementation.
 *
 * @author Valerijus Drozdovas
 */
public class DefaultVariantContainer implements VariantContainer {
    private Map<String, Variant> variantMap;

    public DefaultVariantContainer() {
        variantMap = new HashMap<>();
    }

    @Override
    public void setVariant(String varName, Variant varValue) {
        variantMap.put(varName, varValue);
    }

    @Override
    public Variant getVariant(String varName) {
        Variant value = variantMap.get(varName);
        if (value == null) value = new Variant();
        return value;
    }

    @Override
    public void setVariant(String varName, int index, Variant varValue) {
        Variant value = variantMap.get(varName);
        if (value == null) value = new Variant();
        value.setArrayItem(index, varValue);
        variantMap.put(varName, value);
    }

    @Override
    public Variant getVariant(String varName, int index) {
        Variant value = variantMap.get(varName);
        if (value == null) return new Variant();

        return value.getArrayItem(index);
    }

    @Override
    public boolean contains(String varName) {
        return variantMap.containsKey(varName);
    }

}
