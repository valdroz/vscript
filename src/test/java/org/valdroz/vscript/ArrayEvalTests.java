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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ArrayEvalTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testEquationEvaluator() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.fromString("a"));
        container.setVariant("arr", Variant.fromArray(variants));

        Variant var = new EquationEval("arr + 2 + 3").eval(container);

        assertThat(var.size(), is(3));
        assertThat(Variant.getArrayItem(var,0), is(Variant.fromString("a")));
        assertThat(Variant.getArrayItem(var,1), is(Variant.fromInt(2)));
        assertThat(Variant.getArrayItem(var,2), is(Variant.fromInt(3)));

    }

    @Test
    public void testArrayEquals() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.fromString("a"));
        variants.add(Variant.fromString("b"));
        variants.add(Variant.fromString("c"));
        container.setVariant("arr", Variant.fromArray(variants));
        container.setVariant("arr2", Variant.fromArray(variants));

        Variant var = new EquationEval("arr == \"a\"").eval(container);

        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        var = new EquationEval("arr == \"b\"").eval(container);

        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        var = new EquationEval("arr == \"c\"").eval(container);

        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        var = new EquationEval("arr == \"d\"").eval(container);

        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(false));

        var = new EquationEval("size(arr)").eval(container);

        assertThat(var.isNumeric(), is(true));
        assertThat(var.asNumeric().intValue(), is(3));

        var = new EquationEval("arr + \"e\"").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(4));

        var = container.getVariant("arr");
        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(3));

        var = new EquationEval("arr - \"b\"").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(2));

        var = new EquationEval("arr - arr2").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(0));

        var = container.getVariant("arr");
        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(3));

        var = new EquationEval("arr = arr - arr2").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(0));

        var = container.getVariant("arr");
        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(0));

        var = container.getVariant("arr2");
        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(3));

        var = new EquationEval("arr2 != \"a\"").eval(container);

        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(false));

        var = new EquationEval("arr = arr2 - \"a\"").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(2));

        var = new EquationEval("arr != \"a\" && arr == \"b\" && arr == \"c\" ").eval(container);

        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        var = new EquationEval("arr[0] = \"a\"").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(2));

        var = new EquationEval("arr[1] = \"a\"").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(2));

        var = new EquationEval("arr - arr2").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(0));

        var = new EquationEval("arr - \"a\"").eval(container);

        assertThat(var.isArray(), is(true));
        assertThat(var.size(), is(0));

    }


}
