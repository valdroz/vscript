package org.valdroz.vscripttests;


import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.valdroz.vscript.*;

import java.util.function.Supplier;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class EquationEvalTests {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testEquationEvaluator() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("movie.price", new Variant(5));

        Variant var = new EquationEval("3 + movie.price * sqrt(4)").eval(container);

        assertThat(var.getDouble(), is(13.0));
    }

    @Test
    public void testEquationEvaluatorWithMore() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        container.setVariant("movie.price", new Variant(5));

        Variant var = new EquationEval("(3 + movie.price * sqrt(4)) >= 13.0").eval(container);
        assertThat(var.toBoolean(), is(true));

        var = new EquationEval("(3 + movie.price * sqrt(4)) == 13.0").eval(container);
        assertThat(var.toBoolean(), is(true));

        var = new EquationEval("(3 + movie.price * sqrt(4)) <= 13.0").eval(container);
        assertThat(var.toBoolean(), is(true));

        var = new EquationEval("(3 + movie.price * sqrt(4)) < 13.0").eval(container);
        assertThat(var.toBoolean(), is(false));

        var = new EquationEval("(3 + movie.price * sqrt(4)) >= 13.1").eval(container);
        assertThat(var.toBoolean(), is(false));
    }


    @Test
    public void testEquationEvaluatorBooleans() {
        Variant var = new EquationEval("true + true").eval();
        assertThat(var.getDouble(), is(2.0));
    }

    @Test
    public void testEquationEvaluatorBooleans2() {
        Variant var = new EquationEval("true && true").eval();
        assertThat(var.getDouble(), is(1.0));
    }


    @Test
    public void testEquationEvaluatorString() {
        Variant var = new EquationEval("\"2\" + 1").eval();
        assertThat(var.getString(), is("21"));
    }

    @Test
    public void testEquationEvaluatorNegativeAndPriority() {
        Variant var = new EquationEval("-1.0 * (2.5 + 3.5)").eval();
        assertThat(var.getDouble(), is(-6.0));
    }

    @Test
    public void testEquationEvaluatorAssignment() {
        DefaultVariantContainer container = new DefaultVariantContainer();
        new EquationEval("a = -1.0 * (2.5 + 3.5)").eval(container);
        assertThat(container.getVariant("a").getDouble(), is(-6.0));
    }

    @Test
    public void testEquationEvaluatorConstAssignment() {
        exception.expect(RuntimeException.class);
        new EquationEval("true = -1.0 * (2.5 + 3.5)").eval();
    }

    @Test
    public void testEquationEvaluatorEqualPriorityPlusOpr() {
        Variant var = new EquationEval("1+2+3").eval();
        assertThat(var.getDouble(), is(6.0));
    }

    @Test
    public void testEquationEvaluatorEqualPriorityMinusOpr() {
        BaseNode node = new EquationEval("4 - 3 - 2").getNode();
        Variant var = node.execute(new DefaultVariantContainer());
        assertThat(var.getDouble(), is(-1.0));
    }


    @Test
    public void testEquationEvaluatorEqualPriorityMultiDivOpr() {
        Variant var = new EquationEval("2*3*4").eval();
        assertThat(var.getDouble(), is(24.0));
    }

    @Test
    public void testEquationEvaluatorEqualPrioritySumMul() {
        Variant var = new EquationEval("2+3*4").eval();
        assertThat(var.getDouble(), is(14.0));
    }

    @Test
    public void testEquationEvaluatorEqualPriorityMulSum() {
        Variant var = new EquationEval("3*4+2").eval();
        assertThat(var.getDouble(), is(14.0));
    }

    @Test
    public void testEquationEvaluatorPriority() {
        Variant var = new EquationEval(" false + 1 && true").eval();
        assertThat(var.toBoolean(), is(true));
    }

    @Test
    public void testNegatedEquationEvaluatorPriority() {
        Variant var = new EquationEval("!(false + 1 && true)").eval();
        assertThat(var.toBoolean(), is(false));
    }

    @Test
    public void testDayFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);
        Variant var = new EquationEval("day()").eval();
        assertThat(var.toInteger(), is(5));
        EquationEval.setNowSupplier(prevNow);
    }

    @Test
    public void testMonthFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);
        Variant var = new EquationEval("month()").eval();
        assertThat(var.toInteger(), is(2));
        EquationEval.setNowSupplier(prevNow);
    }

    @Test
    public void testYearFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);
        Variant var = new EquationEval("year()").eval();
        assertThat(var.toInteger(), is(2010));
        EquationEval.setNowSupplier(prevNow);
    }


    @Test
    public void testDaysInMonthFunc() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);
        // April (Current month + 2) of 2010 should have 30 days
        Variant var = new EquationEval("days_in_month(2)").eval();
        assertThat(var.toInteger(), is(30));
        EquationEval.setNowSupplier(prevNow);
    }

    @Test
    public void testDaysInMonthFunc2() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);
        // Jan (Current month - 1) of 2010 should have 31 days
        Variant var = new EquationEval("days_in_month(-1)").eval();
        assertThat(var.toInteger(), is(31));
        EquationEval.setNowSupplier(prevNow);
    }


    @Test
    public void testDaysInMonthFuncNoParam() {
        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);
        // Current month, February of 2010 should have 28 days
        Variant var = new EquationEval("days_in_month()").eval();
        assertThat(var.toInteger(), is(28));
        EquationEval.setNowSupplier(prevNow);
    }


    @Test
    public void testIsoFunc() {
        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", new Variant("2010-02-05T17:31:15Z"));
        Variant var = new EquationEval("iso(testDate)").eval(container);
        assertThat(var.toLong(), is(1265391075000L));
    }

    @Test
    public void testNowFunc() throws InterruptedException {
        VariantContainer container = new DefaultVariantContainer();
        long t1 = DateTime.now().getMillis();
        Thread.sleep(1);
        Variant var = new EquationEval("now()").eval(container);
        Thread.sleep(1);
        long t2 = DateTime.now().getMillis();
        assertThat(var.toLong(), greaterThan(t1));
        assertThat(var.toLong(), lessThan(t2));
    }

    @Test
    public void testDaysBeforeNowFunc() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", new Variant("2010-02-01"));

        Variant var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.toInteger(), is(4));

        container.setVariant("testDate", new Variant("2009-02-01"));
        var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.toInteger(), is(369));

        EquationEval.setNowSupplier(prevNow);
    }

    @Test
    public void testDaysBeforeNowFuncMillis() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", new Variant(new DateTime(2010, 2, 1, 0, 0).getMillis())); //"2010-02-01"

        Variant var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.toInteger(), is(4));

        container.setVariant("testDate", new Variant(new DateTime(2009, 2, 1, 0, 0).getMillis())); //"2009-02-01"
        var = new EquationEval("days_before_now(testDate)").eval(container);

        assertThat(var.toInteger(), is(369));

        EquationEval.setNowSupplier(prevNow);
    }


    @Test
    public void testHoursBeforeNowFunc() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", new Variant("2010-02-05T10:00:00Z"));
        Variant var = new EquationEval("hours_before_now(testDate)").eval(container);

        assertThat(var.toInteger(), is(7));

        EquationEval.setNowSupplier(prevNow);
    }

    @Test
    public void testHoursBeforeNowFuncMillis() {

        // Now is always 2010-02-05T17:31:15Z
        Supplier<Long> prevNow = EquationEval.setNowSupplier(() -> 1265391075000L);

        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("testDate", new Variant(new DateTime(2010, 2, 5, 10, 0, DateTimeZone.UTC).getMillis())); //2010-02-05T10:00:00Z
        Variant var = new EquationEval("hours_before_now(testDate)").eval(container);

        assertThat(var.toInteger(), is(7));

        EquationEval.setNowSupplier(prevNow);
    }


}
