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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DecimalEvalTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testDivision() {
        Variant.setDecimalScale(4);
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(1));
        container.setVariant("d2", Variant.fromInt(3));
        container.setVariant("s1", Variant.fromString("10.1"));

        Variant var = new EquationEval("d1/d2").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("0.3333"));

        var = new EquationEval("d1 + s1").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("11.1"));

    }

    @Test
    public void testDivisionByZero() {
        exception.expectMessage("Division by zero");
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromDouble(1000.0));
        container.setVariant("d2", Variant.fromDouble(0.0));

        new EquationEval("d1/d2").eval(container);

    }

    @Test
    public void testCasting() {

        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("d2", Variant.fromInt(3));
        container.setVariant("s1", Variant.fromString("10.1"));
        container.setVariant("s2", Variant.fromString("a10.1"));

        Variant var = new EquationEval("d1 + s1").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("12.1"));

        var = new EquationEval("s1 + d1").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("12.1"));

        var = new EquationEval("s2 + d1").eval(container);

        assertThat(var.isString(), is(true));
        assertThat(var.asString(), is("a10.12"));

        var = new EquationEval("s1 * d1").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("20.2"));

        var = new EquationEval("s1 / d1").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("5.05"));

        var = new EquationEval("s1 - d1").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("8.1"));

    }


    @Test
    public void testErrorOnSubtractStringArithmetic() {
        exception.expectMessage("Invalid minus operator on [String Variant of \"a10.1\"]");

        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        new EquationEval("s2 - d1").eval(container);
    }

    @Test
    public void testErrorOnAddMultiplyStringArithmetic() {
        exception.expectMessage("Invalid multiply operator on [String Variant of \"a10.1\"]");

        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        new EquationEval("s2 * d1").eval(container);
    }

    @Test
    public void testErrorOnAddDivideStringArithmetic() {
        exception.expectMessage("Invalid divide operator on [String Variant of \"a10.1\"]");

        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        new EquationEval("s2 / d1").eval(container);
    }

    @Test
    public void testErrorOnAddDivideStringArithmetic2() {
        exception.expectMessage("Division by zero");

        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("d1", Variant.fromInt(2));
        container.setVariant("s2", Variant.fromString("a10.1"));

        new EquationEval("d1 / s2").eval(container);

    }

}
