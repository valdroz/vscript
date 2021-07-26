/*
 * Copyright 2021 Valerijus Drozdovas
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
import com.google.common.collect.Maps;
import com.google.gson.*;
import org.apache.commons.lang3.StringUtils;
import org.valdroz.vscript.Variant;
import org.valdroz.vscript.VariantContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Valerijus Drozdovas
 * Created on 07/06/21
 */
public final class DataSetIndex {

    private final Map<String, VariantValue> valueMap = Maps.newHashMap();
    private final List<List<DataSetIndex>> subIndexes = Lists.newArrayList();
    private final Map<String, JsonElements> jsonElementMap = Maps.newHashMap();

    private int size = 1;

    private DataSetIndex() {
    }

    /**
     * Flattens JSON into indexed data set.
     *
     * @param jo JsonObject
     * @return DataSetIndex instance
     */
    public static DataSetIndex index(JsonObject jo) {
        DataSetIndex index = new DataSetIndex();
        populateIndex(index, "", jo);
        index.consolidate();
        return index;
    }

    /**
     * @return Number of data permutations
     */
    public int numberOfPermutations() {
        return size;
    }

    /**
     * @param name             Variable path name
     * @param permutationIndex Permutation index
     * @return Variant value
     */
    public Variant getValue(String name, int permutationIndex) {
        VariantValue v = valueMap.get(name);
        if (v != null) {
            return v.getValue(permutationIndex);
        }
        return Variant.nullVariant();
    }

    /**
     * @param name  Variable path name
     * @return `true` if value has more that one permutation.
     */
    public boolean hasPermutations(String name) {
        VariantValue v = valueMap.get(name);
        if (v != null) {
            return v.size() > 1;
        }
        return false;
    }

    /**
     * @param name             JSON array path name
     * @param permutationIndex Permutation index
     * @return JsonElement
     */
    public JsonElement getJsonElement(String name, int permutationIndex) {
        JsonElements v = jsonElementMap.get(name);
        if (v != null) {
            return v.get(permutationIndex);
        }
        JsonObject jo = new JsonObject();
        String pref = name + ".";
        valueMap.keySet().stream()
                .filter(s -> s.startsWith(pref))
                .forEach(s -> JsonUtils.set(jo, s.substring(pref.length()), getValue(s, permutationIndex)));

        return jo;
    }

    /**
     * @return Iterable of all paths available in data set.
     */
    public Iterable<String> getNames() {
        return this.valueMap.keySet();
    }


    /**
     * @return Iterable of json element names.
     */
    public Iterable<String> getObjectArrayNames() {
        return this.jsonElementMap.keySet();
    }

    /**
     * @param permutationIndex Data set permutation index
     * @return Variant container
     */
    public VariantContainer getAsVariantContainer(int permutationIndex) {
        return new DataSetVariantContainer(this, permutationIndex);
    }

    private void consolidate() {
        List<DataSetIndex> permutations = Lists.newArrayList();
        if (!subIndexes.isEmpty()) {
            generatePermutations(subIndexes, permutations, 0, new DataSetIndex());
        }
        int level = 0;
        for (DataSetIndex dsi : permutations) {
            int inc = dsi.numberOfPermutations();
            for (Map.Entry<String, VariantValue> v : dsi.valueMap.entrySet()) {
                for (int j = 0; j < inc; ++j) {
                    setIndexValue(this, v.getKey(), level + j, v.getValue().getValue(j));
                }
            }
            for (Map.Entry<String, JsonElements> jvalues : dsi.jsonElementMap.entrySet()) {
                for (int j = 0; j < inc; ++j) {
                    setIndexJsonValue(this, jvalues.getKey(), level + j, jvalues.getValue().getOrLast(j));
                }
            }
            level += inc;
        }
        this.size = Math.max(1, level);
    }

    private void generatePermutations(List<List<DataSetIndex>> allIndex, List<DataSetIndex> permutations, int depth, DataSetIndex dsi) {
        if (allIndex.size() == depth) {
            permutations.add(dsi);
            return;
        }
        for (int i = 0; i < allIndex.get(depth).size(); i++) {
            DataSetIndex ndsi = new DataSetIndex();
            ndsi.jsonElementMap.putAll(dsi.jsonElementMap);
            ndsi.valueMap.putAll(dsi.valueMap);
            DataSetIndex dsi2 = allIndex.get(depth).get(i);
            ndsi.valueMap.putAll(dsi2.valueMap);
            ndsi.jsonElementMap.putAll(dsi2.jsonElementMap);
            ndsi.size = Math.max(dsi.numberOfPermutations(), dsi2.numberOfPermutations());
            generatePermutations(allIndex, permutations, depth + 1, ndsi);
        }
    }

    private static void populateIndex(DataSetIndex dsi, String key, JsonElement je) {
        if (je.isJsonPrimitive()) {
            setIndexValue(dsi, key, variantFromPrimitive(je.getAsJsonPrimitive()));
        } else if (je.isJsonObject()) {
            JsonObject jo = je.getAsJsonObject();
            jo.entrySet().forEach(entry -> {
                String newKey = newKey(key, entry.getKey());
                populateIndex(dsi, newKey, entry.getValue());
            });
        } else if (je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            List<DataSetIndex> indices = Lists.newArrayList();
            List<Variant> variants = Lists.newLinkedList();
            for (int i = 0; i < ja.size(); ++i) {
                JsonElement item = ja.get(i);
                if (item.isJsonPrimitive()) {
                    variants.add(variantFromPrimitive(item.getAsJsonPrimitive()));
                } else if (item.isJsonObject()) {
                    DataSetIndex ndsi = new DataSetIndex();
                    populateIndex(ndsi, key, item.getAsJsonObject());
                    indices.add(ndsi);
                    ndsi.consolidate();
                }
            }
            if (!variants.isEmpty()) {
                setIndexValue(dsi, key, Variant.fromArray(variants));
            }
            if (!indices.isEmpty()) {
                dsi.subIndexes.add(indices);
                setIndexJsonValue(dsi, key, 0, ja);
            }
        }
    }

    private static void setIndexValue(DataSetIndex dsi, String key, int index, Variant variant) {
        VariantValue variantValue = dsi.valueMap.get(key);
        if (variantValue == null) {
            variantValue = new IndexedVariantValue();
            dsi.valueMap.put(key, variantValue);
        }
        variantValue.setValue(index, variant);
        dsi.size = Math.max(dsi.size, variantValue.size());
    }

    private static void setIndexValue(DataSetIndex dsi, String key, Variant variant) {
        VariantValue variantValue = dsi.valueMap.get(key);
        if (variantValue == null) {
            variantValue = new BaseVariantValue();
            dsi.valueMap.put(key, variantValue);
        }
        variantValue.setValue(0, variant);
        dsi.size = Math.max(dsi.size, variantValue.size());
    }


    private static void setIndexJsonValue(DataSetIndex dsi, String key, int index, JsonElement je) {
        JsonElements elements = dsi.jsonElementMap.get(key);
        if (elements == null) {
            elements = new JsonElements();
            dsi.jsonElementMap.put(key, elements);
        }
        elements.set(index, je);
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


    private static String newKey(String pref, String key) {
        if (StringUtils.isBlank(pref)) {
            return key;
        }
        return pref + "." + key;
    }

    private static class JsonElements {
        private final ArrayList<JsonElement> elements = new ArrayList<>();

        void set(int index, JsonElement value) {
            elements.ensureCapacity(index + 1);
            while (elements.size() < index + 1) {
                elements.add(JsonNull.INSTANCE);
            }
            elements.set(index, value);
        }

        JsonElement get(int index) {
            if (index >= 0 && index < elements.size()) {
                return elements.get(index);
            }
            return JsonNull.INSTANCE;
        }

        JsonElement getOrLast(int index) {
            if (index >= 0 && index < elements.size()) {
                return elements.get(index);
            } else if (elements.size() == 1) {
                return elements.get(0);
            }
            return JsonNull.INSTANCE;
        }

        int size() {
            return elements.size();
        }

    }

    private interface VariantValue {
        void setValue(int index, Variant value);

        Variant getValue(int index);

        int size();
    }

    private static class BaseVariantValue implements VariantValue {
        private Variant value = Variant.nullVariant();

        @Override
        public void setValue(int index, Variant value) {
            this.value = value;
        }

        @Override
        public Variant getValue(int index) {
            return value;
        }

        @Override
        public int size() {
            return 1;
        }
    }

    private static class IndexedVariantValue implements VariantValue {
        private final ArrayList<Variant> valueIndex = new ArrayList<>();

        @Override
        public void setValue(int index, Variant value) {
            while (valueIndex.size() < index + 1) {
                valueIndex.add(Variant.nullVariant());
            }
            valueIndex.set(index, value);
        }

        @Override
        public Variant getValue(int index) {
            if (index >= 0 && index < valueIndex.size()) {
                return valueIndex.get(index);
            }
            return Variant.nullVariant();
        }

        @Override
        public int size() {
            return valueIndex.size();
        }

    }

    private static class DataSetVariantContainer implements VariantContainer {
        private final DataSetIndex dataSetIndex;
        private final int permutationIndex;

        public DataSetVariantContainer(DataSetIndex dataSetIndex, int permutationIndex) {
            this.dataSetIndex = dataSetIndex;
            this.permutationIndex = permutationIndex;
        }

        @Override
        public void setVariant(String varName, Variant varValue) {
            // no op
        }

        @Override
        public Variant getVariant(String varName) {
            return dataSetIndex.getValue(varName, permutationIndex);
        }

        @Override
        public void setVariant(String varName, int index, Variant varValue) {
            // no op
        }

        @Override
        public Variant getVariant(String varName, int index) {
            return Variant.getArrayItem(dataSetIndex.getValue(varName, permutationIndex), index);
        }

        @Override
        public boolean contains(String varName) {
            return dataSetIndex.valueMap.containsKey(varName);
        }
    }


}
