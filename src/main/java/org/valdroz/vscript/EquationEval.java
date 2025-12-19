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
 * Equation evaluator.
 *
 * @author Valerijus Drozdovas
 */
public final class EquationEval {

    private final CompositeNode node;
    private RunBlock masterRunBlock;
    private NodeStats stats;

    /**
     * @param equation is a text with interpretable equation. E.g. "10 * 2"
     */
    public EquationEval(String equation) {
        this(equation, null);
    }

    /**
     * @param equation is a text with interpretable equation. E.g. "10 * 2"
     * @param traceListener an instance if evaluation trance listener
     */
    public EquationEval(String equation, TraceListener traceListener) {
        EquationParser parser = new EquationParser(equation, traceListener);
        if (traceListener != null) {
            traceListener.trace("DSL: " + equation);
        }
        this.node = new CompositeNode();
        String leftover = "";
        int pos = 0;
        do {
            node.addNode(parser.parse(pos));
            pos = parser.currentPosition() + 1;
            leftover = parser.unprocessedSource();
        } while (leftover.length() > 1 && leftover.startsWith(";"));

        if (leftover.length() > 0) {
            StringBuilder errorMsg = new StringBuilder("Expression error: Unexpected text \"");
            errorMsg.append(leftover);
            errorMsg.append("\"");
            throw new EvaluationException(errorMsg.toString(), parser.currentLineNumber(), parser.currentPosition());
        }
    }

    public static Node parse(String equation) {
        return new EquationEval(equation).getNode();
    }

    public static Node parse(String equation, TraceListener traceListener) {
        return new EquationEval(equation, traceListener).getNode();
    }

    public Node getNode() {
        return node;
    }

    public NodeStats getStats() {
        if (stats == null) {
            stats = new NodeStats();
            node.collectStats(stats);
        }
        return stats;
    }

    public EquationEval withMasterBlock(RunBlock masterBlock) {
        this.masterRunBlock = masterBlock;
        return this;
    }

    public Variant eval(VariantContainer variantContainer) {
        if (masterRunBlock == null) {
            masterRunBlock = new DefaultRunBlock();
        }
        node.setParentRunBlock(masterRunBlock);
        return node.execute(variantContainer);
    }

    public Variant eval() {
        return eval(new DefaultVariantContainer());
    }

    public static Supplier<Long> setCurrentTimeSupplier(Supplier<Long> currentTimeSupplier) {
        Supplier<Long> prev = BaseNode.currentTime;
        BaseNode.currentTime = currentTimeSupplier;
        return prev;
    }

}
