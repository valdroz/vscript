package org.valdroz.vscript.examples;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.valdroz.vscript.*;

public class Example {
    public static void main(String[] args) {
        Configuration.setDecimalScale(2);
        VariantContainer container = new DefaultVariantContainer();
        container.setVariant("myVar1", Variant.fromDouble(5.2));

        Variant var = EquationEval.parse("res = 3 + myVar1 * sqrt(4); res == 13.4").execute(container);
        Variant res = container.getVariant("res");

        Assert.assertThat(var.isBoolean(), Matchers.is(true));
        Assert.assertThat(var.asBoolean(), Matchers.is(true));

        Assert.assertThat(res.isNumeric(), Matchers.is(true));
        Assert.assertThat(res.asNumeric().doubleValue(), Matchers.is(13.4));
        Assert.assertThat(res.asString(), Matchers.is("13.40"));

        System.out.println(var); // prints: Boolean Variant of true
        System.out.println(res); // prints: Numeric Variant of 13.40
    }
}
