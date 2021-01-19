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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.valdroz.vscript.VariantMatchers.*;
import static org.valdroz.vscript.Variant.*;
import static org.junit.Assert.assertThrows;

public class DecimalEvalTests {

    @Test
    public void testDivision() {
        int restore = Configuration.setDecimalScale(4);
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", fromInt(1));
        container.setVariant("d2", fromInt(3));
        container.setVariant("s1", fromString("10.1"));

        Variant var = new EquationEval("d1/d2").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("0.3333"));

        var = new EquationEval("d1 + s1").eval(container);

        assertThat(var, numericOf(BigDecimal.valueOf(11.1)));
        assertThat(var.asString(), is("11.1000"));

        Configuration.setDecimalScale(restore);
    }

    @Test
    public void testDivisionByZero() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", fromDouble(1000.0));
        container.setVariant("d2", fromDouble(0.0));

        assertThrows(EvaluationException.class, () -> new EquationEval("d1/d2").eval(container));
    }

    @Test
    public void testCasting() {

        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", fromInt(2));
        container.setVariant("d2", fromInt(3));
        container.setVariant("s1", fromString("10.1"));
        container.setVariant("s2", fromString("a10.1"));

        Variant var = new EquationEval("d1 + s1").eval(container);

        assertThat(var, numericOf(12.1));

        var = new EquationEval("s1 + d1").eval(container);

        assertThat(var, numericOf(12.1));

        var = new EquationEval("s2 + d1").eval(container);

        assertThat(var, stringOf("a10.12"));

        var = new EquationEval("s1 * d1").eval(container);

        assertThat(var, numericOf(20.2));

        var = new EquationEval("s1 / d1").eval(container);

        assertThat(var, numericOf(5.05));

        var = new EquationEval("s1 - d1").eval(container);

        assertThat(var, numericOf(8.1));

    }


    @Test
    public void testErrorOnSubtractStringArithmetic() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        assertThrows(EvaluationException.class, () -> new EquationEval("s2 - d1").eval(container));
    }

    @Test
    public void testErrorOnAddMultiplyStringArithmetic() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        assertThrows(EvaluationException.class, () -> new EquationEval("s2 * d1").eval(container));
    }

    @Test
    public void testErrorOnAddDivideStringArithmetic() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        assertThrows(EvaluationException.class, () -> new EquationEval("s2 / d1").eval(container));
    }

    @Test
    public void testErrorOnAddDivideStringArithmetic2() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        assertThrows(EvaluationException.class, () -> new EquationEval("d1 / s2").eval(container));
    }

}
