/*
 * Copyright 2025 Valerijus Drozdovas
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
