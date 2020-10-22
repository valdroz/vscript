package org.valdroz.vscript;

/**
 * @author Valerijus Drozdovas
 * Created on 9/24/20
 */
public class TracingConstantNode extends ConstantNode {
    private final Tracer tracer;

    public TracingConstantNode(ConstantNode node, Tracer tracer) {
        super(node.getId(), node.getConstantValue());
        this.tracer = tracer;
    }

    public TracingConstantNode(String id, Tracer tracer) {
        super(id);
        this.tracer = tracer;
    }

    public TracingConstantNode(String id, Variant value, Tracer tracer) {
        super(id, value);
        this.tracer = tracer;
    }

    @Override
    public Variant execute(VariantContainer variantContainer) {
        Variant result = super.execute(variantContainer);
        tracer.put(getId(), result);
        return result;
    }
}
