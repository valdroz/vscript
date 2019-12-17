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
    private final Map<String, Variant> variantMap;

    public DefaultVariantContainer() {
        variantMap = new HashMap<>();
    }

    @Override
    public void setVariant(String varName, Variant varValue) {
        variantMap.put(varName, varValue);
    }

    @Override
    public Variant getVariant(String name) {
        Variant value = variantMap.get(name);
        return Variant.sanitize(value);
    }

    @Override
    public void setVariant(String name, int index, Variant varValue) {
        Variant variant = Variant.sanitize(variantMap.get(name));
        if (variant.isNull()) {
            variantMap.put(name, variant);
        }
        Variant.setArrayItem(variant, index, varValue);
    }

    @Override
    public Variant getVariant(String name, int index) {
        return Variant.getArrayItem(variantMap.get(name), index);
    }

    @Override
    public boolean contains(String varName) {
        return variantMap.containsKey(varName);
    }

}
