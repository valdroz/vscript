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

import com.google.common.collect.Lists;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.valdroz.vscript.Variant.*;
import static org.valdroz.vscript.VariantMatchers.*;
import static org.junit.Assert.*;

public class ArrayEvalTests {

    @Test
    public void testAddToArray() {
        DefaultVariantContainer container = new DefaultVariantContainer();

        container.setVariant("arr", emptyArray());

        Variant var = new EquationEval("arr = arr + \"a\" + 2 + 3").eval(container);

        assertThat(var, arrayOf(fromString("a"), fromInt(2), fromInt(3)));

        var = new EquationEval("arr[0]=1").eval(container);

        assertThat(var, arrayOf(fromInt(1), fromInt(2), fromInt(3)));
    }


    @Test
    public void testArrayValueSubstitution() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("arr", fromArray(Lists.newArrayList(fromString("a"))));

        Variant var = new EquationEval("arr[0] + arr[1]?\"b\"").eval(container);

        assertThat(var, stringOf("ab"));
    }

    @Test
    public void testArrayIndexValueReplacement() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("arr", fromArray(Lists.newArrayList(fromString("a"))));

        Variant var = new EquationEval("arr[0]=2; arr[1]=arr[0]; arr[0]==arr[1]").eval(container);
        assertThat("Expect that arr[0]==arr[1] evaluates to `true` ", var, booleanOf(true));
        assertThat(container.getVariant("arr"), arrayOf(fromInt(2), fromInt(2)));
    }

    @Test
    public void testToArrayFunction() {
        DefaultVariantContainer container = new DefaultVariantContainer();

        Variant var = new EquationEval("arr = to_array(1,2,\"3\",true); is_size_4 = size(arr); arr == 3").eval(container);

        assertThat("Last evaluation value `true` as arr includes item matching \"3\" == 3", var, booleanOf(true));
        assertThat(container.getVariant("arr"), arrayOf(fromInt(1), fromInt(2), fromString("3"), fromBoolean(true)));
    }


    @Test
    public void testArrayConcatenation() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        Variant result = new EquationEval("a = to_array(1,2,3,4) + to_array(5,6,7,8); " +
                "size(a) == 8 && a[0]==1 && a[7]==8").eval(container);

        assertThat(result, booleanOf(true));
        assertThat(container.getVariant("a"),
                arrayOf(fromInt(1), fromInt(2), fromInt(3), fromInt(4), fromInt(5), fromInt(6), fromInt(7), fromInt(8)));
    }

    @Test
    public void testArraySubtraction() {
        Variant result = new EquationEval("a = to_array(1,2,4,3,4) - to_array(1,4,8); " +
                "size(a) == 2 && a[0]==2 && a[1]==3", System.out::println).eval();

        assertThat(result, booleanOf(true));
    }

    @Test
    public void testRemoveMatchingItemsFromArray() {
        Variant result = new EquationEval("a=to_array(1,2,4,3,4); a - 4", System.out::println).eval();

        assertThat(result, arrayOf(fromInt(1), fromInt(2), fromInt(3)));
    }

    @Test
    public void testRemoveMatchingItemFromArray1() {
        // What is happenig here is that 4-a, converting `a` to numeric variant.
        // Since `a` is an array, it size value is taken, hence we have 4 - 5, where 5 is size of array.
        // This is not particularly intuitive, therefore this expression will yield error in next release
        Variant result = new EquationEval("a=to_array(1,2,4,3,4); 4 - a", System.out::println).eval();

        assertThat(result, numericOf(-1));
    }

    @Test
    public void testMultiplyIsNotSupported() {
        assertThrows(EvaluationException.class, () -> new EquationEval("a=to_array(1,2,4,3,4); a * 4").eval());
    }


    @Test
    public void testDynamicArrayAllocation() {
        Variant result = new EquationEval("a[2]=10; size(a) == 3; a[0] == null && a[2] == 10", System.out::println).eval();

        assertThat(result, booleanOf(true));
    }

    @Test
    public void testDynamicArrayReallocation() {
        Variant result = new EquationEval("a=1; a[1]=2; a[2]=3; size(a) == 3 && a[0]==1 && a[1]==2 && a[2]==3", System.out::println).eval();

        assertThat(result, booleanOf(true));
    }

    @Test
    public void testToArrayAssigmentFunction() {
        DefaultVariantContainer container = new DefaultVariantContainer();

        Variant var = new EquationEval("arr = {1,2,\"3\",true} + \"added to the end\"; is_size_4 = size(arr); arr == 3").eval(container);

        assertThat("Last evaluation value `true` as arr includes item matching \"3\" == 3", var, booleanOf(true));
        assertThat(container.getVariant("arr"), arrayOf(
                fromInt(1),
                fromInt(2),
                fromString("3"),
                fromBoolean(true),
                fromString("added to the end")
        ));
    }
}
