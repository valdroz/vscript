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

/**
 * @author Valerijus Drozdovas
 * Created on 3/23/20
 */
public interface Node {

    /**
     * Set the reference to parent.
     * Parent run block is used to resolve externally defined function.
     *
     * @param runBlock parent run block.
     */
    void setParentRunBlock(RunBlock runBlock);

    /**
     * Evaluates and return node result.
     *
     * @param variantContainer variable variant container.
     * @return Resulting value
     */
    Variant execute(VariantContainer variantContainer);

    /**
     * If applicable node must report variable(s) and/or function name(s) it might be referencing.
     *
     * @param stats
     */
    void collectStats(NodeStats stats);

}
