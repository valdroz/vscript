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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArrayEvalTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testAddToArray() {
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


    @Test
    public void testArrayValueSubstitution() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.fromString("a"));
        container.setVariant("arr", Variant.fromArray(variants));

        Variant var = new EquationEval("arr[0] + arr[1]?\"b\"").eval(container);

        assertThat(var.isString(), is(true));
        assertThat(var.asString(), is("ab"));

    }

    @Test
    public void testArrayIndexValueReplacement() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        List<Variant> variants = new ArrayList<>();
        variants.add(Variant.fromString("a"));
        container.setVariant("arr", Variant.fromArray(variants));

        Variant var = new EquationEval("arr[0]=2; arr[1]=arr[0]; arr[0]==arr[1]").eval(container);
        assertThat("Last evaluation value `true`", var.asBoolean(), is(true));
    }

    @Test
    public void testToArrayFunction() {
        DefaultVariantContainer container = new DefaultVariantContainer();

        Variant var = new EquationEval("arr = to_array(1,2,\"3\",true); is_size_4 = size(arr); arr == 3").eval(container);

        assertThat("Last evaluation result should be off boolean type", var.isBoolean(), is(true));
        assertThat("Last evaluation value `true` as arr includes item matching \"3\" == 3", var.asBoolean(), is(true));
        assertThat("is_size_4 must be 4", container.getVariant("is_size_4").asNumeric(), is(BigDecimal.valueOf(4)));
        assertThat("arr[0] is 1", container.getVariant("arr", 0).asNumeric(), is(BigDecimal.valueOf(1)));
        assertThat("arr[1] is 2", container.getVariant("arr", 1).asNumeric(), is(BigDecimal.valueOf(2)));
        assertThat("arr[2] is \"3\"", container.getVariant("arr", 2).asString(), is("3"));
        assertThat("arr[3] is \"true\"", container.getVariant("arr", 3).asBoolean(), is(true));
    }


    @Test
    public void testArrayConcatenation() {
        Variant result = new EquationEval("a = to_array(1,2,3,4) + to_array(5,6,7,8); " +
                "size(a) == 8 && a[0]==1 && a[7]==8").eval();

        assertThat(result.isBoolean(), is(true));
        assertThat(result.asBoolean(), is(true));
    }

    @Test
    public void testArraySubtraction() {
        Variant result = new EquationEval("a = to_array(1,2,4,3,4) - to_array(1,4,8); " +
                "size(a) == 2 && a[0]==2 && a[1]==3", System.out::println).eval();

        assertThat(result.isBoolean(), is(true));
        assertThat(result.asBoolean(), is(true));
    }

    @Test
    public void testDynamicArrayAllocation() {
        Variant result = new EquationEval("a[2]=10; size(a) == 3; a[0] == null && a[2] == 10", System.out::println).eval();

        assertThat(result.isBoolean(), is(true));
        assertThat(result.asBoolean(), is(true));
    }

    @Test
    public void testDynamicArrayReallocation() {
        Variant result = new EquationEval("a=1; a[1]=2; a[2]=3; size(a) == 3 && a[0]==1 && a[1]==2 && a[2]==3", System.out::println).eval();

        assertThat(result.isBoolean(), is(true));
        assertThat(result.asBoolean(), is(true));
    }
}
