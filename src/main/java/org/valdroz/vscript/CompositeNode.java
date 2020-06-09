/*
 * Copyright 2020 Valerijus Drozdovas
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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Valerijus Drozdovas
 * Created on 3/23/20
 */
class CompositeNode implements Node {

    private RunBlock parentRunBlock;
    private List<Node> nodes = new LinkedList<>();

    @Override
    public Variant execute(VariantContainer variantContainer) {
        Variant result = Variant.nullVariant();
        for (Node n : nodes) {
            n.setParentRunBlock(parentRunBlock);
            result = n.execute(variantContainer);
        }
        return result;
    }

    @Override
    public void setParentRunBlock(RunBlock runBlock) {
        this.parentRunBlock = runBlock;
    }

    @Override
    public void collectStats(NodeStats stats) {
        nodes.forEach(node -> node.collectStats(stats));
    }

    void addNode(Node node) {
        if (node != null) {
            this.nodes.add(node);
        }
    }
}
