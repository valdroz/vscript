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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.valdroz.vscript.Configuration;
import org.valdroz.vscript.EquationEval;
import org.valdroz.vscript.Variant;

import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Valerijus Drozdovas
 * Created on 12/16/19
 */
public class JsonVariantContainerTest {

    @Test
    public void jsonToVariantContainers() throws Exception {
        Configuration.setDecimalScale(3);

        String json = IOUtils.toString(JsonDataSetMaker.class.getResource("/test-data-set.json"), Charset.defaultCharset());
        JsonElement je = new JsonParser().parse(json);

        List<JsonVariantContainer> variantContainers = JsonVariantContainer.jsonToVariantContainers(je.getAsJsonObject());

        assertThat(variantContainers.size(), is(2));
        JsonVariantContainer vc = variantContainers.get(0);

        Variant v = vc.getVariant("objects.type");

        assertThat(v.isString(), is(true));
        assertThat(v.asString(), is("obj1"));

        v = new EquationEval("size(options) == 3 && " +
                "options == \"op2\" && " +
                "objects.type == \"obj1\" && " +
                "objects.data == 90 && " +
                "objects.p == 3 && " +
                "objects.b").eval(vc);

        assertThat(v.isBoolean(), is(true));
        assertThat(v.asBoolean(), is(true));

        vc = variantContainers.get(1);

        v = new EquationEval("size(options) == 3 && " +
                "options == \"op2\" && " +
                "objects.type == \"obj2\" && " +
                "objects.data == 0 &&" +
                "!is_null(objects.b) && !objects.b").eval(vc);

        assertThat(v.isBoolean(), is(true));
        assertThat(v.asBoolean(), is(true));

        new EquationEval("test.a = 10/3.1").eval(vc);

        v = new EquationEval("test.a == 3.226").eval(vc);

        assertThat(v.isBoolean(), is(true));
        assertThat(v.asBoolean(), is(true));

        Configuration.setCaseSensitive(false);

        new EquationEval("objects.data = 5").eval(vc);

        v = new EquationEval("size(options) == 3 && " +
                "options == \"op3\" && " +
                "objects.type == \"OBJ2\" && " +
                "objects.data == 5 && test.a == 3.226").eval(vc);

        assertThat(v.isBoolean(), is(true));
        assertThat(v.asBoolean(), is(true));

        v = new EquationEval("options[3] = \"op4\"").eval(vc);
        v = new EquationEval("test.a[1] = \"test\"").eval(vc);

        System.out.println(vc.getJsonObject());

        v = new EquationEval("size(options) == 4 && " +
                "options == \"op4\" && " +
                "objects.type == \"OBJ2\" && " +
                "objects.data == 5 && test.a == 3.226 && test.a == \"test\"").eval(vc);

        assertThat(v.isBoolean(), is(true));
        assertThat(v.asBoolean(), is(true));

    }
}