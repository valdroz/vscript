/*
 * Copyright 2019 Valerijus Drozdovas
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
package org.valdroz.vscript.json;

import com.google.common.collect.Lists;
import com.google.gson.*;
import org.valdroz.vscript.Variant;
import org.valdroz.vscript.VariantContainer;

import java.util.List;

/**
 * @author Valerijus Drozdovas
 * Created on 12/16/19
 */
public class JsonVariantContainer implements VariantContainer {

    private final JsonObject jo;

    private JsonVariantContainer(JsonObject jo) {
        this.jo = jo;
    }


    public static List<JsonVariantContainer> jsonToVariantContainers(JsonObject je) {
        List<JsonVariantContainer> variantContainers = Lists.newLinkedList();
        JsonDataSetMaker jsonDataSetMaker = new JsonDataSetMaker(je, JsonDataSetMaker.Mode.KEEP_ARRAYS_FOR_PRIMITIVES);
        jsonDataSetMaker.getDataSets().forEach(jsonObject -> variantContainers.add(new JsonVariantContainer(jsonObject)));
        return variantContainers;
    }

    public JsonObject getJsonObject() {
        return jo;
    }

    @Override
    public void setVariant(String name, Variant value) {
        jo.add(name, primitiveFromVariant(value));
    }

    @Override
    public Variant getVariant(String name) {
        JsonElement je = jo.get(name);
        if (je == null) return Variant.nullVariant();

        if (je.isJsonPrimitive()) {
            return variantFromPrimitive(je.getAsJsonPrimitive());
        }

        if (je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            Variant va = Variant.emptyArray();
            for (int i = 0; i < ja.size(); ++i) {
                JsonElement jae = ja.get(i);
                if (jae.isJsonPrimitive()) {
                    Variant.setArrayItem(va, i, variantFromPrimitive(jae.getAsJsonPrimitive()));
                }
            }
            return va;

        }

        // Should not happen as JsonDataSetMaker flattens Json into set of primitives
        return Variant.nullVariant();
    }

    @Override
    public void setVariant(String name, int index, Variant value) {
        JsonElement je = jo.get(name);
        if (je == null) je = new JsonArray(index + 1);
        if (je.isJsonPrimitive()) {
            JsonArray _ja = new JsonArray(index + 1);
            _ja.add(je);
            for (int i=0; i<index; ++i) {
                _ja.add(JsonNull.INSTANCE);
            }
            je = _ja;
        }
        JsonArray ja = je.getAsJsonArray();
        if (ja.size() <= index) {
            JsonArray _ja = new JsonArray(index + 1);
            _ja.addAll(ja);
            for (int i=_ja.size(); i<=index; ++i) {
                _ja.add(JsonNull.INSTANCE);
            }
            ja = _ja;
        }
        ja.set(index, primitiveFromVariant(value));
        jo.add(name, ja);
    }

    @Override
    public Variant getVariant(String name, int index) {
        JsonElement je = jo.get(name);
        if (je == null) return Variant.nullVariant();
        if (je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            if (index >= 0 && index < ja.size()) {
                JsonElement jae = ja.get(index);
                if (jae.isJsonPrimitive()) {
                    return variantFromPrimitive(jae.getAsJsonPrimitive());
                }
            }
        }
        return Variant.nullVariant();
    }

    @Override
    public boolean contains(String name) {
        return jo.has(name);
    }

    private static Variant variantFromPrimitive(JsonPrimitive jp) {
        if (jp == null) {
            return Variant.nullVariant();
        }

        if (jp.isString()) {
            return Variant.fromString(jp.getAsString());
        }

        if (jp.isNumber()) {
            return Variant.fromBigDecimal(jp.getAsBigDecimal());
        }

        if (jp.isBoolean()) {
            return Variant.fromBoolean(jp.getAsBoolean());
        }

        return Variant.nullVariant();
    }

    private JsonElement primitiveFromVariant(Variant variant) {
        if (variant.isNumeric()) {
            return new JsonPrimitive(variant.asNumeric());
        }

        if (variant.isString()) {
            return new JsonPrimitive(variant.asString());
        }

        if (variant.isBoolean()) {
            return new JsonPrimitive(variant.asBoolean());
        }

        if (variant.isArray()) {
            List<Variant> va = variant.asArray();
            JsonArray ja = new JsonArray(va.size());
            va.forEach(variant1 -> ja.add(primitiveFromVariant(variant1)));
            return ja;
        }

        return JsonNull.INSTANCE;
    }
}
