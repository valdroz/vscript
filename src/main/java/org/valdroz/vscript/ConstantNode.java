package org.valdroz.vscript;

/**
 * @author vdrozd720
 * Created on 2019-07-06
 */
public class ConstantNode extends BaseNode {
    private Variant constant;

    ConstantNode(Variant value) {
        this.constant = value;
        this.operation = NT_CONSTANT;
    }

    @Override
    public Variant execute(VariantContainer variantContainer) {
        return constant;
    }

}
