package org.valdroz.vscript.examples;

import org.valdroz.vscript.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExampleCustomFunction {
    public static void main(String[] args) {
        VariantContainer variantContainer = new DefaultVariantContainer();

        DefaultRunBlock masterRunBlock = new DefaultRunBlock();
        masterRunBlock.registerFunction(
                new AbstractFunction("custom_multiply(first, second)") {
                    @Override
                    public Variant execute(VariantContainer variantContainer) {
                        Variant first = variantContainer.getVariant("first");
                        Variant second = variantContainer.getVariant("second");
                        return first.multiply(second);
                    }
                }
        );

        Variant result = new EquationEval("2 + custom_multiply(3, 4)")
                .withMasterBlock(masterRunBlock)
                .eval(variantContainer);

        assertThat(result.asNumeric().doubleValue(), is(14.0));
    }
}
