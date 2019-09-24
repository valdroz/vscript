/*
 * Copyright 2000 Valerijus Drozdovas
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

import java.util.function.Supplier;

/**
 *
 * Equation evaluator.
 *
 * @author Valerijus Drozdovas
 */
public final class EquationEval {

    private BaseNode node;
    private MasterRunBlock masterRunBlock;

    /**
     *
     * @param equation is a text with interpretable equation. E.g. "10 * 2"
     */
    public EquationEval(String equation) {
        EquationParser parser = new EquationParser(equation);

        this.node = parser.parse(0);
        String leftover = parser.getRemainderSource();

        if ( this.node == null || parser.getLastErrorCode() != EquationParser.CE_SUCCESS ) {
            StringBuilder errorMsg = new StringBuilder("Expression error: ");
            errorMsg.append(Utils.getErrorMsg(parser.getLastErrorCode(), parser.getPosition(), parser.currentLineNumber()));
            if (leftover != null) {
                errorMsg.append(" - Stopped at: \"");
                errorMsg.append(leftover);
                errorMsg.append("\"");
            }
            throw new RuntimeException(errorMsg.toString());
        }

        if (leftover.length() > 0) {
            StringBuilder errorMsg = new StringBuilder("Expression error: Unexpected text \"");
            errorMsg.append(leftover);
            errorMsg.append("\"");
            throw new RuntimeException(errorMsg.toString());
        }
    }

    public static BaseNode parse(String equation) {
        return new EquationEval(equation).getNode();
    }

    public BaseNode getNode() {
        return node;
    }

    public EquationEval withMasterBlock(MasterRunBlock masterBlock) {
        this.masterRunBlock = masterBlock;
        return this;
    }

    public Variant eval(VariantContainer variantContainer) {
        node.setMasterRunBlock(this.masterRunBlock);
        return node.execute(variantContainer);
    }

    public Variant eval() {
        return eval(new DefaultVariantContainer());
    }

    public static Supplier<Long> setNowSupplier(Supplier<Long> nowProvider) {
        Supplier<Long> prev = BaseNode.nowProvider;
        BaseNode.nowProvider = nowProvider;
        return prev;
    }

}
