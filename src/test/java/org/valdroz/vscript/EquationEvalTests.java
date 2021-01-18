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
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

public class EquationEvalTests {

    @Test
    public void testEquationEvaluator() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("movie.price", Variant.fromInt(5));

        Variant var = new EquationEval("3 + movie.price * sqrt(4)").eval(container);

        assertThat(var.asNumeric().doubleValue(), is(13.0));
    }

    @Test
    public void testEquationEvaluatorWithMore() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("movie.price", Variant.fromInt(5));

        Variant var = new EquationEval("3 + movie.price * sqrt(4) >= 13.0").eval(container);
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        var = new EquationEval("3 + movie.price * sqrt(4) == 13.0").eval(container);
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        var = new EquationEval("3 + movie.price * sqrt(4) <= 13.0").eval(container);
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        var = new EquationEval("3 + movie.price * sqrt(4) < 13.0").eval(container);
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(false));

        var = new EquationEval("3 + movie.price * sqrt(4) >= 13.1").eval(container);
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(false));
    }


    @Test
    public void testEquationEvaluatorBooleans() {
        Assert.assertThrows(EvaluationException.class,
                () -> new EquationEval("true + true").eval());
    }

    @Test
    public void testEquationEvaluatorBooleans2() {
        Variant var = new EquationEval("true && true").eval();
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));
    }


    @Test
    public void testEquationEvaluatorString() {
        Variant var = new EquationEval("\"2\" + 1").eval();
        assertThat(var.isNumeric(), is(true));
        assertThat(var.asString(), is("3"));
    }

    @Test
    public void testEquationEvaluatorNegativeAndPriority() {
        Variant var = new EquationEval("-1.0 * (2.5 + 3.5)").eval();
        assertThat(var.asNumeric().doubleValue(), is(-6.0));
    }

    @Test
    public void testEquationEvaluatorAssignment() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        new EquationEval("a = -1.0 * (2.5 + 3.5)").eval(container);
        assertThat(container.getVariant("a").asNumeric().doubleValue(), is(-6.0));
    }

    @Test
    public void testEquationEvaluatorConstAssignment() {
        assertThrows(EvaluationException.class,
                () -> new EquationEval("true = 1").eval());
    }

    @Test
    public void testEquationEvaluatorEqualPriorityPlusOpr() {
        Variant var = new EquationEval("1+2+3 == 6").eval();
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));
    }

    @Test
    public void testEquationEvaluatorEqualPriorityMinusOpr() {
        Node node = new EquationEval("0 > 4 - 3 - 2").getNode();
        Variant var = node.execute(new DefaultVariantContainer());
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));
    }


    @Test
    public void testEquationEvaluatorEqualPriorityMultiDivOpr() {
        Variant var = new EquationEval("2*3*4 == 24").eval();
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));
    }

    @Test
    public void testEquationEvaluatorEqualPrioritySumMul() {
        Variant var = new EquationEval("2+3*4").eval();
        assertThat(var.asNumeric().doubleValue(), is(14.0));
    }

    @Test
    public void testEquationEvaluatorEqualPriorityMulSum() {
        Variant var = new EquationEval("3*4+2").eval();
        assertThat(var.asNumeric().doubleValue(), is(14.0));
    }

    @Test
    public void testBooleanErrorOnArithmetics() {
        assertThrows(EvaluationException.class,
                () -> new EquationEval("false + 1").eval());
    }

    @Test
    public void testNegatedEquationEvaluatorPriority() {
        Variant var = new EquationEval("!(false || true)").eval();
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(false));
    }

    @Test
    public void testDayOfTheMonthFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);
        Variant var = new EquationEval("day()").eval();
        assertThat(var.asNumeric().intValue(), is(5));
        EquationEval.setCurrentTimeSupplier(prevNow);
    }

    @Test
    public void testMonthFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);
        Variant var = new EquationEval("month()").eval();
        assertThat(var.asNumeric().intValue(), is(2));
        EquationEval.setCurrentTimeSupplier(prevNow);
    }

    @Test
    public void testYearFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);
        Variant var = new EquationEval("year()").eval();
        assertThat(var.asNumeric().intValue(), is(2010));
        EquationEval.setCurrentTimeSupplier(prevNow);
    }


    @Test
    public void testDaysInMonthFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);
        // April (Current month + 2) of 2010 should have 30 days
        Variant var = new EquationEval("days_in_month(2)").eval();
        assertThat(var.asNumeric().intValue(), is(30));
        EquationEval.setCurrentTimeSupplier(prevNow);
    }

    @Test
    public void testDaysInMonthFunc2() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);
        // Jan (Current month - 1) of 2010 should have 31 days
        Variant var = new EquationEval("days_in_month(-1)").eval();
        assertThat(var.asNumeric().intValue(), is(31));
        EquationEval.setCurrentTimeSupplier(prevNow);
    }


    @Test
    public void testDaysInMonthFuncNoParam() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);
        // Current month, February of 2010 should have 28 days
        Variant var = new EquationEval("days_in_month()").eval();
        assertThat(var.asNumeric().intValue(), is(28));
        EquationEval.setCurrentTimeSupplier(prevNow);
    }


    @Test
    public void testIsoFunc() {
        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", Variant.fromString("2010-02-05T17:31:15Z"));
        Variant var = new EquationEval("iso(testDate)").eval(container);
        assertThat(var.asNumeric().longValue(), is(1265391075000L));
    }

    @Test
    public void testNowFunc() throws InterruptedException {
        VariantContainer container = new DefaultVariantContainer();
        long t1 = DateTime.now().getMillis();
        Thread.sleep(1);
        Variant var = new EquationEval("now()").eval(container);
        Thread.sleep(1);
        long t2 = DateTime.now().getMillis();
        assertThat(var.asNumeric().longValue(), greaterThan(t1));
        assertThat(var.asNumeric().longValue(), lessThan(t2));
    }

    @Test
    public void testDaysBeforeNowFunc() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", Variant.fromString("2010-02-01"));

        Variant var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(4));

        container.setVariant("testDate", Variant.fromString("2009-02-01"));
        var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(369));

        EquationEval.setCurrentTimeSupplier(prevNow);
    }

    @Test
    public void testDaysBeforeNowFuncMillis() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", Variant.fromLong(new DateTime(2010, 2, 1, 0, 0).getMillis())); //"2010-02-01"

        Variant var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(4));

        container.setVariant("testDate", Variant.fromLong(new DateTime(2009, 2, 1, 0, 0).getMillis())); //"2009-02-01"
        var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(369));

        EquationEval.setCurrentTimeSupplier(prevNow);
    }


    @Test
    public void testHoursBeforeNowFunc() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", Variant.fromString("2010-02-05T10:00:00Z"));
        Variant var = new EquationEval("hours_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(7));

        EquationEval.setCurrentTimeSupplier(prevNow);
    }

    @Test
    public void testHoursBeforeNowFuncMillis() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", Variant.fromLong(new DateTime(2010, 2, 5, 10, 0, DateTimeZone.UTC).getMillis())); //2010-02-05T10:00:00Z
        Variant var = new EquationEval("hours_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(7));

        EquationEval.setCurrentTimeSupplier(prevNow);
    }

    @Test
    public void testMinutesBeforeNowFunc() {

        // Now is always 2010-02-05T17:30:00Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391000000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", Variant.fromString("2010-02-05T17:00:00Z"));
        Variant var = new EquationEval("minutes_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(30));

        EquationEval.setCurrentTimeSupplier(prevNow);
    }

    @Test
    public void testMinutesBeforeNowFuncMillis() {

        // Now is always 2010-02-05T17:30:00Z
        Supplier<Long> prevNow = EquationEval.setCurrentTimeSupplier(() -> 1265391000000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", Variant.fromLong(new DateTime(2010, 2, 5, 17, 0, DateTimeZone.UTC).getMillis()));
        Variant var = new EquationEval("minutes_before_now(testDate)").eval(container);

        assertThat(var.asNumeric().intValue(), is(30));

        EquationEval.setCurrentTimeSupplier(prevNow);
    }


    @Test
    public void testSubstitutions() {

        VariantContainer variantContainer = new DefaultVariantContainer();
        variantContainer.setVariant("a", Variant.nullVariant());
        variantContainer.setVariant("b", Variant.fromInt(1));
        variantContainer.setVariant("c", Variant.fromString("c"));
        variantContainer.setVariant("a2", Variant.nullVariant());

        EquationEval eq = new EquationEval("2 + a?b + 2");
        Variant res = eq.eval(variantContainer);

        assertThat(res.isNumeric(), is(true));
        assertThat(res.asNumeric().intValue(), is(5));

        res = EquationEval.parse("a?a2?b + 2").execute(variantContainer);

        assertThat(res.isNumeric(), is(true));
        assertThat(res.asNumeric().intValue(), is(3));

        res = EquationEval.parse("a?sqrt(4) + 1").execute(variantContainer);

        assertThat(res.isNumeric(), is(true));
        assertThat(res.asNumeric().intValue(), is(3));

        res = EquationEval.parse("a?true").execute(variantContainer);

        assertThat(res.isBoolean(), is(true));
        assertThat(res.asBoolean(), is(true));

        res = EquationEval.parse("a?false").execute(variantContainer);

        assertThat(res.isBoolean(), is(true));
        assertThat(res.asBoolean(), is(false));

        res = EquationEval.parse("a?\"aaa\" + 2").execute(variantContainer);

        assertThat(res.isString(), is(true));
        assertThat(res.asString(), is("aaa2"));

        res = EquationEval.parse("a?-1 + 2").execute(variantContainer);

        assertThat(res.isNumeric(), is(true));
        assertThat(res.asNumeric().intValue(), is(1));
    }


    @Test
    public void testLeadingNegativeDigit1() {
        Variant res = EquationEval.parse("-123 + 2 * 3").execute(new DefaultVariantContainer());
        assertThat(res.isNumeric(), is(true));
        assertThat(res.asNumeric().intValue(), is(-117));
    }

    @Test
    public void testLeadingNegativeDigit2() {
        Variant res = EquationEval.parse("(-123 + 2) * 3").execute(new DefaultVariantContainer());
        assertThat(res.isNumeric(), is(true));
        assertThat(res.asNumeric().intValue(), is(-363));
    }


    @Test
    public void testEmpty() {
        Variant res = EquationEval.parse("   ").execute(new DefaultVariantContainer());
        assertThat(res.isNull(), is(true));
    }

    @Test
    public void testNull() {
        Variant res = EquationEval.parse(null).execute(new DefaultVariantContainer());
        assertThat(res.isNull(), is(true));
    }

    @Test
    public void testDefaultExpression() {
        String backup = Configuration.setExpressionForEmptyEval("true");
        Variant res = EquationEval.parse(null).execute(new DefaultVariantContainer());
        Configuration.setExpressionForEmptyEval(backup);

        assertThat(res.isBoolean(), Matchers.is(true));
    }


    @Test
    public void example() {
        Configuration.setDecimalScale(2);
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("myVar1", Variant.fromDouble(5.2));

        Variant var = EquationEval.parse("res = 3 + myVar1 * sqrt(4); res == 13.4").execute(container);
        Variant res = container.getVariant("res");

        assertThat(var.isBoolean(), Matchers.is(true));
        assertThat(var.asBoolean(), Matchers.is(true));

        assertThat(res.isNumeric(), Matchers.is(true));
        assertThat(res.asNumeric().doubleValue(), Matchers.is(13.4));
        assertThat(res.asString(), Matchers.is("13.40"));
    }


    @Test
    public void invalidAssignments() {
        assertThrows(EvaluationException.class, () -> new EquationEval("1=3"));
    }


    @Test
    public void testStats() {

        EquationEval eq = new EquationEval("c[0] = a + b; d = extf1(c[0] + e + 1); d = sin(a * 0); c[f] = extf2() ");

        NodeStats stats = eq.getStats();

        assertThat(stats.referencedVariables().size(), is(6));
        assertThat(stats.referencedVariables(), containsInAnyOrder("a", "b", "c", "d", "e", "f"));
        assertThat(stats.referencedExtFunctions().size(), is(2));
        assertThat(stats.referencedExtFunctions(), containsInAnyOrder("extf1", "extf2"));
    }

    @Test
    public void testTrace() {
        VariantContainer variantContainer = new DefaultVariantContainer();
        DefaultRunBlock masterRunBlock = new DefaultRunBlock();

        masterRunBlock.registerFunction(
                new AbstractFunction("multiply(first, second)") {
                    @Override
                    public Variant execute(VariantContainer variantContainer) {
                        Variant first = variantContainer.getVariant("first");
                        Variant second = variantContainer.getVariant("second");
                        return first.multiply(second);
                    }
                }
        );
        new EquationEval("var1 = 10 * 2; var2 = 20; var3=false;" +
                "arr = to_array(\"v1\",\"v2\"); " +
                "arr[0] == \"v1\";" +
                "var3 = var1 == var3;" +
                "var3 = !var3;" +
                "var1 >= var2;" +
                "var1 <= var2;" +
                "var1 > var3;" +
                "var1 < var2;" +
                "var4 = var3 || var1 != var2;" +
                "multiply(var1,3/2)", System.out::println)
                .withMasterBlock(masterRunBlock)
                .eval(variantContainer);

    }

    @Test
    public void testTrace2() {
        Configuration.setCaseSensitive(false);
        VariantContainer variantContainer = new DefaultVariantContainer();
        variantContainer.setVariant("var1", Variant.fromString("V1"));
        variantContainer.setVariant("var2", Variant.fromBoolean(true));
        Variant res = EquationEval.parse("var1 == \"v1\" && var2 && \"v1\" == to_array(\"A1\",\"V1\") ", System.out::println)
                .execute(variantContainer);

        assertThat(res.isBoolean(), is(true));
        assertThat(res.asBoolean(), is(true));
    }

    @Test
    public void testTraceConstants() {
        List<String> trace = Lists.newLinkedList();
        Variant var = new EquationEval("true == true", trace::add).eval();
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));
        assertThat(trace.size(), is(1));
        assertThat(trace.get(0), containsString("true EQUALS TO true YIELDS true"));
    }


    @Test
    public void testPower() {
        Variant var = new EquationEval("pow(1+1,\"3\")").eval();
        assertThat(var.isNumeric(), is(true));
        assertThat(var.asNumeric().intValue(), is(8));
        var = new EquationEval("pow(\"2\",\"3\" + 1)").eval();
        assertThat(var.isNumeric(), is(true));
        assertThat(var.asNumeric().intValue(), is(16));

        var = new EquationEval("pow(\"a\",\"3\" + 1)", System.out::println).eval();
        assertThat(var.isNull(), is(true));
    }

    @Test
    public void testCosine() {
        Variant var = new EquationEval("cos(0)").eval();
        assertThat(var.isNumeric(), is(true));
        assertThat(var.asNumeric().intValue(), is(1));

        var = new EquationEval("cos(null)").eval();
        assertThat(var.isNull(), is(true));

        assertThrows(EvaluationException.class, () -> new EquationEval("cos(false)").eval());

        var = new EquationEval("a=cos(to_array(0,0,PI)); a[0]==1 && a[1]==1 && a[2]==-1").eval();
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

    }


    @Test
    public void testBinOperators() {
        
        Variant var = new EquationEval("1|2 == 3 && 1&2 == 0 && 1^3==2", System.out::println).eval();
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));
    }


}
