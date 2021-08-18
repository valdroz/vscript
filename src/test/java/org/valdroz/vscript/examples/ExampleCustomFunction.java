package org.valdroz.vscript.examples;

import org.valdroz.vscript.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExampleCustomFunction {
    public static void main(String[] args) {
        VariantContainer variantContainer = new DefaultVariantContainer();
        DefaultRunBlock masterRunBlock = new DefaultRunBlock();
        masterRunBlock.registerFunction("custom_multiply(first, second)",
                (lvc) -> lvc.getVariant("first").multiply(lvc.getVariant("second")));

        Variant result = new EquationEval("2 + custom_multiply(3, 4)").withMasterBlock(masterRunBlock)
                .eval(variantContainer);

        assertThat(result.asNumeric().doubleValue(), is(14.0));

        masterRunBlock.registerFunction("if(condition, first, second)",
                (lvc) -> lvc.getVariant("condition"));

        int a = 10;
        int b = a * 2;

        variantContainer.setVariant("a", Variant.fromInt(a));
        variantContainer.setVariant("b", Variant.fromInt(b));

        Variant result1 = new EquationEval("if(a < b , a, b)").withMasterBlock(masterRunBlock)
                .eval(variantContainer);

        assertThat(result1.asNumeric().intValue(), is(10));

        variantContainer.setVariant("c", Variant.fromString("truestatement"));
        variantContainer.setVariant("d", Variant.fromString("falsestatement"));

        Variant result2 = new EquationEval("if(a < b , c, d)").withMasterBlock(masterRunBlock)
                .eval(variantContainer);

        assertThat(result2.asString(), is("truestatement"));
    }
}
