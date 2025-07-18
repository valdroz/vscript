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
