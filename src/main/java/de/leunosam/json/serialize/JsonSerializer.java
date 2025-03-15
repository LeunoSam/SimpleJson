package de.leunosam.json.serialize;

import java.util.LinkedList;
import java.util.List;

import de.leunosam.json.Json;
import de.leunosam.json.util.JsonConstants;
import de.leunosam.json.util.StringMapping;

/**
 * This class is used to serialize {@link Json}s. Use
 * {@link JsonSerializer#toString()} to get the String representation of the
 * registered Fields.
 * 
 * @author LeunoSam
 */
public class JsonSerializer {

    private List<String> fields = new LinkedList<>();

    public JsonSerializer() {
        super();
    }

    public void addBooleanField(String fieldName, Boolean b) {
        fields.add("\"" + fieldName + "\":" + b);
    }

    public void addIntegerField(String fieldName, Integer i) {
        fields.add("\"" + fieldName + "\":" + i);
    }

    public void addLongField(String fieldName, Long l) {
        fields.add("\"" + fieldName + "\":" + l);
    }

    public void addDoubleField(String fieldName, Double d) {
        fields.add("\"" + fieldName + "\":" + d);
    }

    public void addStringField(String fieldName, String s) {
        fields.add("\"" + fieldName + "\":\"" + s + "\"");
    }

    public void addObjectField(String fieldName, Json json) {
        fields.add("\"" + fieldName + "\":" + json.serialize());
    }

    public void addBooleanArrayField(String fieldName, List<Boolean> list) {
        addArrayField(fieldName, list, b -> b.toString());
    }

    public void addIntegerArrayField(String fieldName, List<Integer> list) {
        addArrayField(fieldName, list, i -> i.toString());
    }

    public void addLongArrayField(String fieldName, List<Long> list) {
        addArrayField(fieldName, list, l -> l.toString());
    }

    public void addDoubleArrayField(String fieldName, List<Double> list) {
        addArrayField(fieldName, list, d -> d.toString());
    }

    public void addStringArrayField(String fieldName, List<String> list) {
        addArrayField(fieldName, list, string -> "\"" + string + "\"");
    }

    public void addObjectArrayField(String fieldName, List<Json> list) {
        addArrayField(fieldName, list, json -> json.serialize());
    }

    private <T> void addArrayField(String fieldName, List<T> list, StringMapping<T> mapping) {
        StringBuilder newField = new StringBuilder();
        newField.append("\"" + fieldName + "\":[");
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size() - 1; i++) {
                newField.append(mapping.map(list.get(i)) + ",");
            }
            newField.append(mapping.map(list.get(list.size() - 1)));
        }
        newField.append(']');
        fields.add(newField.toString());
    }

    public void addNullField(String fieldName) {
        fields.add("\"" + fieldName + "\":" + JsonConstants.JSON_NULL);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        for (int i = 0; i < fields.size() - 1; i++) {
            builder.append(fields.get(i) + ",");
        }
        builder.append(fields.get(fields.size() - 1));
        builder.append('}');
        return builder.toString();
    }

}
