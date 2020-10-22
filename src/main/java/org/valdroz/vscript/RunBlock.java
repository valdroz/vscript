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
 * RunBlock interface.
 *
 * @author Valerijus Dorzdovas
 */
public interface RunBlock {

    /**
     * Set the reference to parent. Implementation of RunBlock should use provided reference for cascaded function
     * resolution.
     *
     * @param runBlock parent run block.
     */
    void setParentRunBlock(RunBlock runBlock);

    /**
     * Implementation of a RunBlock must return Variant instance reflecting the result.
     *
     * @param variantContainer Variant value container instance.
     * @return Resulting Variant instance. Cannot be `null`.
     */
    Variant execute(VariantContainer variantContainer);

    /**
     * Implementation must return an instance of AbstractFunction matching provided function name.
     * This method will be executed when equation is being evaluated.
     *
     * @param name Name of the function to resolve at runtime.
     *
     * @return Instance of a function matching the name. If no matching function is available `null` might be returned.
     */
    AbstractFunction resolveFunction(String name);

}
