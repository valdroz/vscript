package org.valdroz.vscript;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Valerijus Drozdovas
 * Created on 9/25/20
 */
class Tracer implements TraceListener {
    private final TraceListener traceListener;
    private final Map<String,Variant> nodeResults = Maps.newHashMap();

    Tracer(TraceListener traceListener) {
        this.traceListener = traceListener;
    }

    void put(String nid, Variant result) {
        nodeResults.put(nid, result);
    }

    Variant get(String nid) {
        return nodeResults.getOrDefault(nid, Variant.nullVariant());
    }

    @Override
    public void trace(String message) {
        traceListener.trace(message);
    }
}
