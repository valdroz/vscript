package org.valdroz.vscript;

import java.util.LinkedList;
import java.util.List;

/**
 * @author vdrozd720
 * Created on 3/23/20
 */
class CompositeNode implements Node {

    private DefaultRunBlock parentRunBlock;
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
    public void setParentRunBlock(DefaultRunBlock runBlock) {
        this.parentRunBlock = runBlock;
    }

    @Override
    public void run(VariantContainer variantContainer) {
        nodes.forEach(n -> {
            n.setParentRunBlock(parentRunBlock);
            n.run(variantContainer);
        });
    }

    void addNode(Node node) {
        if (node != null) {
            this.nodes.add(node);
        }
    }
}
