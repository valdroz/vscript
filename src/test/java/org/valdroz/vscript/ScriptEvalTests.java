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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Valerijus Drozdovas
 */
public class ScriptEvalTests {

    @Test
    public void testCustomFunctionEvaluation() {

        VariantContainer variantContainer = new DefaultVariantContainer();

        DefaultRunBlock runBlock = new DefaultRunBlock();

        runBlock.registerFunction("multiply(first, second)",
                (lvc) -> {
                    Variant first = lvc.getVariant("first");
                    Variant second = lvc.getVariant("second");
                    return first.multiply(second);
                });

        Variant result = new EquationEval("2 + multiply(3, 4)")
                .withMasterBlock(runBlock)
                .eval(variantContainer);

        assertThat(result.asNumeric().doubleValue(), is(14.0));

    }


    @Test
    public void testCustomFunctionNullResponseSubstitution() {

        DefaultRunBlock masterRunBlock = new DefaultRunBlock();

        masterRunBlock.registerFunction("produce_null(first, second)",
                (lvc) -> Variant.nullVariant() );

        Variant result = new EquationEval("2 + produce_null(1, 2)?-10")
                .withMasterBlock(masterRunBlock)
                .eval();

        assertThat(result.asNumeric().intValue(), is(-8));

    }

}
