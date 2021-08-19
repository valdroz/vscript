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
    }
}
