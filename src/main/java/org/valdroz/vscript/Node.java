package org.valdroz.vscript;

/**
 * @author vdrozd720
 * Created on 3/23/20
 */
public interface Node extends RunBlock {
    Variant execute(VariantContainer variantContainer);
}
