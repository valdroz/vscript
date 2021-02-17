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

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Valerijus Drozdovas
 * Created on 12/8/19
 */
public class JsonDataSetMaker {

    private final JfObject root = new JfObject(StringUtils.EMPTY);

    public enum Mode {
        KEEP_ARRAYS_FOR_PRIMITIVES,
        ALL_FLATTENED
    }

    private final Mode mode;

    public JsonDataSetMaker(JsonObject je, Mode mode) {
        this(je, StringUtils.EMPTY, mode);
    }

    public JsonDataSetMaker(JsonObject je, String prefix, Mode mode) {
        this.mode = mode;
        flatten(root, prefix, je);
    }

    public Iterable<JsonObject> getDataSets() {
        return root.getDataSets();
    }

    private void flatten(JfObject parent, String pref, JsonElement je) {
        if (je.isJsonPrimitive()) {
            parent.addNode(pref, je.getAsJsonPrimitive());
        } else if (je.isJsonObject()) {
            JfObject child = parent.addChild(new JfObject(pref));
            JsonObject jo = je.getAsJsonObject();
            jo.entrySet().forEach(entry -> {
                String key = newKey(pref, entry.getKey());
                flatten(child, key, entry.getValue());
            });
        } else if (je.isJsonArray()) {
            JsonArray ja = je.getAsJsonArray();
            for (int i = 0; i < ja.size(); ++i) {
                JsonElement item = ja.get(i);
                if (item.isJsonPrimitive()) {
                    if (mode == Mode.KEEP_ARRAYS_FOR_PRIMITIVES) {
                        parent.addNodeAsArray(pref, item.getAsJsonPrimitive());
                    } else {
                        parent.addNode(pref, item.getAsJsonPrimitive());
                    }
                } else {
                    flatten(parent, pref, ja.get(i));
                }
            }
        }
    }

    private static String newKey(String pref, String key) {
        if (StringUtils.isBlank(pref)) {
            return key;
        }
        return pref + "." + key;
    }


    private static class JfObject {
        private final LinkedListMultimap<String, JfObject> children = LinkedListMultimap.create();
        private final List<JsonObject> flats = Lists.newArrayList(new JsonObject());
        private final String name;

        public JfObject(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addNode(String key, JsonPrimitive value) {
            Set<JsonObject> additions = Sets.newHashSet();
            additions.addAll(flats);
            for (JsonObject jo : flats) {
                if (jo.has(key)) {
                    JsonObject addition = jo.deepCopy();
                    addition.add(key, value);
                    additions.add(addition);
                } else {
                    jo.add(key, value);
                }
            }
            flats.clear();
            flats.addAll(additions);
        }

        public void addNodeAsArray(String key, JsonPrimitive value) {
            Set<JsonObject> additions = Sets.newHashSet();
            additions.addAll(flats);
            for (JsonObject jo : flats) {
                if (jo.has(key)) {
                    JsonElement je = jo.get(key);
                    if (je.isJsonArray()) {
                        JsonArray ja = je.getAsJsonArray();
                        ja.add(value);
                    } else {
                        JsonArray ja = new JsonArray();
                        ja.add(je);
                        ja.add(value);
                        jo.add(key, ja);
                    }
                } else {
                    JsonArray ja = new JsonArray();
                    ja.add(value);
                    jo.add(key, ja);
                }
            }
            flats.clear();
            flats.addAll(additions);
        }


        public JfObject addChild(JfObject child) {
            children.put(child.getName(), child);
            return child;
        }

        public List<JsonObject> getDataSets() {
            List<JsonObject> dataSet = Lists.newLinkedList();
            dataSet.addAll(flats);

            if (children.size() > 0) {
                List<JsonObject> augmentedChildDataSet = Lists.newLinkedList();
                for (Map.Entry<String, Collection<JfObject>> entry : children.asMap().entrySet()) {
                    List<JsonObject> list = Lists.newLinkedList();
                    for (JfObject jfObject : entry.getValue()) {
                        list.addAll(jfObject.getDataSets());
                    }
                    augmentedChildDataSet = replicate(augmentedChildDataSet, list);
                }

                dataSet = replicate(dataSet, augmentedChildDataSet);
            }
            return dataSet;
        }

    }

    private static List<JsonObject> replicate(List<JsonObject> base, List<JsonObject> source) {
        if (base.size() == 0) {
            return source;
        }
        List<JsonObject> res = Lists.newLinkedList();
        for (JsonObject jo1 : base) {
            for (JsonObject jo2 : source) {
                JsonObject jo = jo1.deepCopy();
                for (Map.Entry<String, JsonElement> entry : jo2.entrySet()) {
                    jo.add(entry.getKey(), entry.getValue());
                }
                res.add(jo);
            }
        }
        return res;
    }
}
