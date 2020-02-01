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
    private final VariantContainer delegate;

    public LocalVariantContainer(VariantContainer delegate) {
        this.delegate = delegate;
    }

    @Override
    public Variant getVariant(String name) {
        if (super.contains(name)) {
            return super.getVariant(name);
        }
        return Variant.sanitize(delegate.getVariant(name));
    }

    @Override
    public Variant getVariant(String name, int index) {
        if (super.contains(name)) {
            return super.getVariant(name, index);
        }
        return Variant.sanitize(delegate.getVariant(name, index));
    }

    @Override
    public boolean contains(String varName) {
        return super.contains(varName) || delegate.contains(varName);
    }
}
