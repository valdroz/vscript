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

/**
 * Locally scoped variant container.
 *
 * @author Valerijus Drozdovas
 */
public class LocalVariantContainer extends DefaultVariantContainer {
    private final VariantContainer global;

    public LocalVariantContainer(VariantContainer global) {
        this.global = global;
    }

    @Override
    public Variant getVariant(String varName) {
        if (contains(varName)) {
            return super.getVariant(varName);
        }
        return global.getVariant(varName);
    }

    @Override
    public Variant getVariant(String varName, int index) {
        if (contains(varName)) {
            return super.getVariant(varName, index);
        }
        return global.getVariant(varName, index);
    }

}
