package org.valdroz.vscript.examples;

import org.valdroz.vscript.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat; 

public class Example {
    public static void main(String[] args) {
         // Configure decimal scale for numeric operations to 2
        Configuration.setDecimalScale(2);

        // Initialize variant container with one variable as
        // myVar1 = 5.2        
        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("myVar1", Variant.fromDouble(5.2));

        // Execute expression with given variant container
        Variant var = EquationEval.parse("res = 3 + myVar1 * sqrt(4); res == 13.4").execute(container);
        Variant res = container.getVariant("res");

        // Evaluation response expected to be boolean `true` due to `res == 13.4`
        assertThat(var.isBoolean(), is(true));
        assertThat(var.asBoolean(), is(true));

        // Expecting res to be of numeric type since it is a result of algebraic expression
        assertThat(res.isNumeric(), is(true));
        
        // `res` must be equal to 13.4
        assertThat(res.asNumeric().doubleValue(), is(13.4));

        // String should have two places after decimal due to `Configuration.setDecimalScale(2)`
        assertThat(res.asString(), is("13.40"));

        System.out.println(var); // prints: Boolean Variant of true
        System.out.println(res); // prints: Numeric Variant of 13.40
    }
}
