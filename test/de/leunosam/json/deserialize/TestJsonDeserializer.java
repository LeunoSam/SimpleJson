package de.leunosam.json.deserialize;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.leunosam.json.Json;
import de.leunosam.json.serialize.JsonPrettifier;
import de.leunosam.json.util.JsonConstants;
import de.leunosam.json.util.JsonField;

class TestJsonDeserializer {

    @Test
    void testReadFields() {
        MyJson json = new MyJson();
        OtherJson otherJson = new OtherJson();

        otherJson.isJson = true;
        otherJson.someDouble = 1005d;
        otherJson.someChars = "}]]}";

        json.id = 10;
        json.list = List.of("Test{", "B]", "7A1,5");
        json.other = otherJson;

        Map<String, String> fieldMap = JsonDeserializer
                .readFields(new JsonPrettifier().prettify(json));

        assertAll(() -> assertEquals("10", fieldMap.get("id").replaceAll("\\s+", "")),
                () -> assertEquals("[\"Test{\",\"B]\",\"7A1,5\"]",
                        fieldMap.get("list").replaceAll("\\s+", "")),
                () -> assertEquals(otherJson.serialize(),
                        fieldMap.get("other").replaceAll("\\s+", "")));
    }

    @Test
    void testReadBooleanList() {
        String list = "    [   false,    true,  true    ,\tfalse\n,true]";
        assertEquals(List.of(false, true, true, false, true),
                JsonDeserializer.readBooleanList(list));
    }

    @Test
    void testReadIntegerList() {
        String list = "\t[   1,    10,  20    ,\t17\n,27]\n";
        assertEquals(List.of(1, 10, 20, 17, 27), JsonDeserializer.readIntegerList(list));
    }

    @Test
    void testReadLongList() {
        String list = "\n   [   6980005123,    10,  -3987005123    ,\t17\n,27] \t";
        assertEquals(List.of(6_980_005_123l, 10l, -3_987_005_123l, 17l, 27l),
                JsonDeserializer.readLongList(list));
    }

    @Test
    void testReadDoubleList() {
        String list = "  [   1.42,    10.69,  20.17    ,\t17.25\n,27.99]\t  ";
        assertEquals(List.of(1.42d, 10.69d, 20.17d, 17.25d, 27.99d),
                JsonDeserializer.readDoubleList(list));
    }

    @Test
    void testReadStringList() {
        String list = "\n\t\n[  \t\"str1  \", \n\"Hallo Welt!\"\t, "
                + "\"Tschüss...   :(\"\n, \"\"]   \t";
        assertEquals(List.of("str1  ", "Hallo Welt!", "Tschüss...   :(", ""),
                JsonDeserializer.readStringList(list));
    }

    @Test
    void testReadObjectList() {
        OtherJson other1 = new OtherJson();
        OtherJson other2 = new OtherJson();
        JsonPrettifier prettifier = new JsonPrettifier();

        other1.isJson = true;
        other2.isJson = null;
        other1.someLong = 100000l;
        other2.someLong = 999999999l;
        other1.someDouble = 1d;
        other2.someChars = "Hello World";

        String list = "[" + prettifier.prettify(other1) + "," + prettifier.prettify(other2) + "]";
        assertEquals(List.of(other1, other2),
                JsonDeserializer.readObjectList(list, other1.getDeserializer()));
    }

    @Test
    void testNullValues() {
        String[] nullValues = { "null", "   null     \n", "\n\tnull   ", "null\t\t" };
        for (String n : nullValues) {
            assertEquals(null, JsonDeserializer.readBooleanList(n));
            assertEquals(null, JsonDeserializer.readIntegerList(n));
            assertEquals(null, JsonDeserializer.readLongList(n));
            assertEquals(null, JsonDeserializer.readDoubleList(n));
            assertEquals(null, JsonDeserializer.readStringList(n));
            assertEquals(null, JsonDeserializer.readBooleanValue(n));
            assertEquals(null, JsonDeserializer.readIntegerValue(n));
            assertEquals(null, JsonDeserializer.readLongValue(n));
            assertEquals(null, JsonDeserializer.readDoubleValue(n));
            assertEquals(null, JsonDeserializer.readStringValue(n));
        }
    }

    @Test
    void testEmptyList() {
        String list = "   [\n  \t ]   ";
        assertTrue(JsonDeserializer.readBooleanList(list).isEmpty());
        assertTrue(JsonDeserializer.readIntegerList(list).isEmpty());
        assertTrue(JsonDeserializer.readLongList(list).isEmpty());
        assertTrue(JsonDeserializer.readDoubleList(list).isEmpty());
        assertTrue(
                JsonDeserializer.readObjectList(list, new OtherJson().getDeserializer()).isEmpty());
    }

    private class MyJson extends Json {

        private static final long serialVersionUID = 2239490901056984790L;

        @JsonField
        private Integer id;
        @JsonField
        private List<String> list;
        @JsonField
        private OtherJson other;

        @Override
        protected JsonDeserializer<? extends Json> getDeserializer() {
            return null;
        }

    }

    private class OtherJson extends Json {

        private static final long serialVersionUID = 5265545773672317331L;

        @JsonField
        private Boolean isJson;
        @JsonField
        private Double someDouble;
        @JsonField
        private Long someLong;
        @JsonField
        private String someChars;

        @Override
        protected JsonDeserializer<? extends Json> getDeserializer() {
            return new JsonDeserializer<OtherJson>() {

                @Override
                public OtherJson getObject(String json) throws JsonDeserializationException {
                    json = json.strip();
                    if (json.equals(JsonConstants.JSON_NULL)) {
                        return null;
                    }
                    OtherJson result = new OtherJson();
                    Map<String, String> map = JsonDeserializer.readFields(json);
                    result.isJson = JsonDeserializer.readBooleanValue(map.get("isJson"));
                    result.someDouble = JsonDeserializer.readDoubleValue(map.get("someDouble"));
                    result.someLong = JsonDeserializer.readLongValue(map.get("someLong"));
                    result.someChars = JsonDeserializer.readStringValue(map.get("someChars"));
                    return result;
                }
            };
        }
    }

}
