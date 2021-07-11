package org.valdroz.vscript.json;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.valdroz.vscript.Variant;

import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author Valerijus Drozdovas
 * Created on 7/7/21
 */
public class DataSetIndexTest {


    @Test
    public void testJsonWithMultipleArrayLevels() throws Exception {
        String json = IOUtils.toString(JsonDataSetMaker.class.getResource("/test-data-set-3.json"), Charset.defaultCharset());

        JsonElement je = new JsonParser().parse(json);

        DataSetIndex dsi = DataSetIndex.index(je.getAsJsonObject());

        assertThat(dsi.numberOfPermutations(), is(6));

        assertThat(dsi.getValue("id", 0), is(Variant.fromString("1234567890")));
        assertThat(dsi.getValue("id", 1), is(Variant.fromString("1234567890")));
        assertThat(dsi.getValue("id", 2), is(Variant.fromString("1234567890")));
        assertThat(dsi.getValue("id", 3), is(Variant.fromString("1234567890")));
        assertThat(dsi.getValue("id", 4), is(Variant.fromString("1234567890")));
        assertThat(dsi.getValue("id", 5), is(Variant.fromString("1234567890")));

        assertThat(dsi.getValue("prefs", 0), is(Variant.fromArray(Lists.newArrayList(Variant.fromString("p1"), Variant.fromString("p2")))));
        assertThat(dsi.getValue("prefs", 1), is(Variant.fromArray(Lists.newArrayList(Variant.fromString("p1"), Variant.fromString("p2")))));
        assertThat(dsi.getValue("prefs", 2), is(Variant.fromArray(Lists.newArrayList(Variant.fromString("p1"), Variant.fromString("p2")))));
        assertThat(dsi.getValue("prefs", 3), is(Variant.fromArray(Lists.newArrayList(Variant.fromString("p1"), Variant.fromString("p2")))));
        assertThat(dsi.getValue("prefs", 4), is(Variant.fromArray(Lists.newArrayList(Variant.fromString("p1"), Variant.fromString("p2")))));
        assertThat(dsi.getValue("prefs", 5), is(Variant.fromArray(Lists.newArrayList(Variant.fromString("p1"), Variant.fromString("p2")))));

        assertThat(dsi.getValue("objects.type", 0), is(Variant.fromString("obj1")));
        assertThat(dsi.getValue("objects.type", 1), is(Variant.fromString("obj1")));
        assertThat(dsi.getValue("objects.type", 2), is(Variant.fromString("obj1")));
        assertThat(dsi.getValue("objects.type", 3), is(Variant.fromString("obj2")));
        assertThat(dsi.getValue("objects.type", 4), is(Variant.fromString("obj2")));
        assertThat(dsi.getValue("objects.type", 5), is(Variant.fromString("obj2")));

        assertThat(dsi.getValue("otherObjects.type", 0), is(Variant.fromString("obj1")));
        assertThat(dsi.getValue("otherObjects.type", 1), is(Variant.fromString("obj2")));
        assertThat(dsi.getValue("otherObjects.type", 2), is(Variant.fromString("obj2")));
        assertThat(dsi.getValue("otherObjects.type", 3), is(Variant.fromString("obj1")));
        assertThat(dsi.getValue("otherObjects.type", 4), is(Variant.fromString("obj2")));
        assertThat(dsi.getValue("otherObjects.type", 5), is(Variant.fromString("obj2")));

        assertThat(dsi.getValue("otherObjects.ch.n", 0), is(Variant.nullVariant()));
        assertThat(dsi.getValue("otherObjects.ch.n", 1), is(Variant.fromInt(1)));
        assertThat(dsi.getValue("otherObjects.ch.n", 2), is(Variant.fromInt(2)));
        assertThat(dsi.getValue("otherObjects.ch.n", 3), is(Variant.nullVariant()));
        assertThat(dsi.getValue("otherObjects.ch.n", 4), is(Variant.fromInt(1)));
        assertThat(dsi.getValue("otherObjects.ch.n", 5), is(Variant.fromInt(2)));

    }

    @Test
    public void getDataSets4b() throws Exception {
        String json = IOUtils.toString(JsonDataSetMaker.class.getResource("/test-data-set-4.json"), Charset.defaultCharset());
        JsonElement je = new JsonParser().parse(json);

        DataSetIndex dsi = DataSetIndex.index(je.getAsJsonObject());

        assertThat(dsi.numberOfPermutations(), is(24));

    }
}