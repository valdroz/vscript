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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Valerijus Drozdovas
 * Created on 12/16/19
 */
public class JsonDataSetMakerTest {

    @Test
    public void getDataSetsKeepPrimitiveArrays() throws Exception {
        String json = IOUtils.toString(JsonDataSetMaker.class.getResource("/test-data-set.json"), Charset.defaultCharset());
        JsonElement je = new JsonParser().parse(json);

        JsonDataSetMaker dm = new JsonDataSetMaker(je.getAsJsonObject(), JsonDataSetMaker.Mode.KEEP_ARRAYS_FOR_PRIMITIVES);

        List<JsonObject> dataSets = Lists.newLinkedList(dm.getDataSets());
        assertThat(dataSets.size(), is(2));
        assertThat(dataSets.get(0).get("objects.type").getAsJsonPrimitive().getAsString(), is("obj1"));
        assertThat(dataSets.get(1).get("objects.type").getAsJsonPrimitive().getAsString(), is("obj2"));
    }

    @Test
    public void getDataSets() throws Exception {
        String json = IOUtils.toString(JsonDataSetMaker.class.getResource("/test-data-set.json"), Charset.defaultCharset());
        JsonElement je = new JsonParser().parse(json);

        JsonDataSetMaker dm = new JsonDataSetMaker(je.getAsJsonObject(), JsonDataSetMaker.Mode.ALL_FLATTENED);

        List<JsonObject> dataSets = Lists.newLinkedList(dm.getDataSets());
        assertThat(dataSets.size(), is(24));
    }

}