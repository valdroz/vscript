package org.valdroz.vscripttests;

import org.junit.Test;
import org.valdroz.vscript.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.valdroz.vscript.EquationEval.parse;

/**
 * @author Valerijus Drozdovas
 */
public class ScriptEvalTests {

    @Test
    public void testCustomFunctionEvaluation() {

        VariantContainer variantContainer = new DefaultVariantContainer();


        MasterRunBlock masterRunBlock = new MasterRunBlock();

        masterRunBlock.addFunctionStatement(
                new FunctionStatement("multiply")
                        .withParameterName("first")
                        .withParameterName("second")
                        .withBody(parse("multiply=first*second"))
        );

        Variant result = new EquationEval("2 + multiply(3, 4)")
                .withMasterBlock(masterRunBlock)
                .eval(variantContainer);

        assertThat(result.getDouble(), is(14.0));

    }

    @Test
    public void testIfStatement() {

        VariantContainer variantContainer = new DefaultVariantContainer();
        variantContainer.setVariant("a", new Variant("yes"));
        new MasterRunBlock()
                .withRunBlock(new IfStatement(parse("a == \"yes\""))
                        .withStatementBody(parse("result=\"got yes\"")))
                .withRunBlock(parse("theEnd=\"evaluated\""))
                .run(variantContainer);

        assertThat(variantContainer.getVariant("result").toString(), is("got yes"));
        assertThat(variantContainer.getVariant("theEnd").toString(), is("evaluated"));
    }

    @Test
    public void testIfStatementNegative() {

        VariantContainer variantContainer = new DefaultVariantContainer();
        variantContainer.setVariant("a", new Variant("no"));
        new MasterRunBlock()
                .withRunBlock(new IfStatement(parse("a == \"yes\""))
                        .withStatementBody(parse("result=\"got yes\"")))
                .withRunBlock(parse("theEnd=\"evaluated\""))
                .run(variantContainer);

        assertThat(variantContainer.getVariant("result").toString(), is("null"));
        assertThat(variantContainer.getVariant("theEnd").toString(), is("evaluated"));

    }


    @Test
    public void testWhileStatement() {

        VariantContainer variantContainer = new DefaultVariantContainer();
        variantContainer.setVariant("a", new Variant(0));

        new MasterRunBlock()
                .withRunBlock(new WhileStatement(parse("a < 10"))
                        .withStatementBody(parse("a = a + 1")))
                .withRunBlock(parse("theEnd=\"evaluated\""))
                .run(variantContainer);

        assertThat(variantContainer.getVariant("a").toDouble(), is(10.0));
        assertThat(variantContainer.getVariant("theEnd").toString(), is("evaluated"));

    }

}
