package de.leunosam.json.deserialize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.leunosam.json.Json;
import de.leunosam.json.util.JsonConstants;
import de.leunosam.json.util.JsonField;

/**
 * If you add a new {@link Json}-Class, you need to create an own
 * {@link JsonDeserializer}, which can deserialize a Json-String into an
 * instance of your new class.
 * 
 * @param <O> your {@link Json}-Class
 * @author LeunoSam
 */
public abstract class JsonDeserializer<O extends Json> {

    public JsonDeserializer() {
        super();
    }

    /**
     * Deserializes the given Json message into a {@link Json}-Object.
     * 
     * @param json a {@link String}, which contains a Json.
     * @return a {@link Json}-Object.
     */
    public abstract O getObject(String json) throws JsonDeserializationException;

    /**
     * Get a {@link Map} with Field-Value pairs of the given Json-String. To parse
     * the resulting values you cans use the methods
     * {@link JsonDeserializer#readBooleanValue(String)},
     * {@link JsonDeserializer#readBooleanList(String)},
     * {@link JsonDeserializer#readDoubleValue(String)},
     * {@link JsonDeserializer#readDoubleList(String)},
     * {@link JsonDeserializer#readIntegerValue(String)}
     * {@link JsonDeserializer#readIntegerList(String)},
     * {@link JsonDeserializer#readLongValue(String)},
     * {@link JsonDeserializer#readLongList(String)}
     * {@link JsonDeserializer#readStringValue(String)}
     * {@link JsonDeserializer#readStringList(String)},
     * {@link JsonDeserializer#readObjectList(String, JsonDeserializer)}.
     * 
     * @param json a {@link String} containing a valid Json.
     * @return a {@link Map} with the {@link JsonField}-Values as keys and their
     *         values as values.
     */
    protected static Map<String, String> readFields(String json) {
        Map<String, String> fieldMap = new HashMap<String, String>();

        // name of the field which will be saved in the map
        String fieldName = null;
        // shows if the current char of the iteration belongs to a value
        boolean isValue = false;
        boolean isString = false;
        // indicates the start of the fieldName
        int nameStart = -1;
        // indicates the start of the value of the field
        int valueStart = -1;
        // if greater than zero, the current value does not end with ",",
        // because of open [, { signs.
        int blocked = 0;

        for (int i = 0; i < json.length(); i++) {
            if (json.charAt(i) == '\"' && !isValue) {
                if (nameStart == -1) {
                    nameStart = i + 1;
                } else {
                    fieldName = json.substring(nameStart, i);
                    nameStart = -1;
                }
                // isString of value
            } else if (json.charAt(i) == '\"') {
                isString = !isString;
                // value start
            } else if (json.charAt(i) == ':' && !isValue) {
                valueStart = i + 1;
                isValue = true;

                // value end
                // next field or end of json
            } else if ((json.charAt(i) == ',' || json.charAt(i) == '}') && isValue && blocked == 0
                    && !isString) {
                fieldMap.put(fieldName.strip(), json.substring(valueStart, i).strip());
                valueStart = -1;
                isValue = false;
            } else if ((json.charAt(i) == '[' || json.charAt(i) == '{') && isValue && !isString) {
                blocked++;
            } else if ((json.charAt(i) == ']' || json.charAt(i) == '}') && isValue && !isString) {
                blocked--;
            }
        }
        return fieldMap;
    }

    protected static List<Boolean> readBooleanList(String list) {
        return readList(list, JsonDeserializer::readBooleanValue);
    }

    protected static List<Integer> readIntegerList(String list) {
        return readList(list, JsonDeserializer::readIntegerValue);
    }

    protected static List<Long> readLongList(String list) {
        return readList(list, JsonDeserializer::readLongValue);
    }

    protected static List<Double> readDoubleList(String list) {
        return readList(list, JsonDeserializer::readDoubleValue);
    }

    protected static List<String> readStringList(String list) {
        // remove ""
        return readList(list, JsonDeserializer::readStringValue);
    }

    protected static <T extends Json> List<T> readObjectList(String list,
            JsonDeserializer<T> deserializer) {
        return readList(list, deserializer::getObject);
    }

    private static <T> List<T> readList(String list, Deserializer<T> deserializer) {
        list = list.strip();
        if (isNull(list)) {
            return null;
        }
        List<T> result = new LinkedList<>();
        // remove []
        list = list.substring(1, list.length() - 1);
        if (list.isBlank()) {
            return result;
        }
        String element = "";
        boolean isString = false;
        int blocked = 0;
        for (char c : list.toCharArray()) {
            // End of element
            if (c == ',' && blocked == 0 && !isString) {
                result.add(deserializer.deserialize(element));
                // reset for next element
                element = "";
                // don't add , to next element
                continue;
            } else if ((c == '[' || c == '{') && !isString) {
                blocked++;
            } else if ((c == ']' || c == '}') && !isString) {
                blocked--;
            } else if (c == '\"') {
                isString = !isString;
            }
            element += c;
        }
        // add last element
        result.add(deserializer.deserialize(element));
        return result;
    }

    protected static String readStringValue(String stringValue) {
        stringValue = stringValue.strip();
        if (isNull(stringValue)) {
            return null;
        }
        // remove ""
        return stringValue.substring(1, stringValue.length() - 1);
    }

    protected static Boolean readBooleanValue(String boolValue) {
        boolValue = boolValue.strip();
        if (isNull(boolValue)) {
            return null;
        }
        return Boolean.parseBoolean(boolValue);
    }

    protected static Integer readIntegerValue(String intValue) {
        intValue = intValue.strip();
        if (isNull(intValue)) {
            return null;
        }
        return Integer.parseInt(intValue);
    }

    protected static Double readDoubleValue(String doubleValue) {
        doubleValue = doubleValue.strip();
        if (isNull(doubleValue)) {
            return null;
        }
        return Double.parseDouble(doubleValue);
    }

    protected static Long readLongValue(String longValue) {
        longValue = longValue.strip();
        if (isNull(longValue)) {
            return null;
        }
        return Long.parseLong(longValue);
    }

    private static boolean isNull(String value) {
        return value.equals(JsonConstants.JSON_NULL);
    }

    private interface Deserializer<T> {

        T deserialize(String str);

    }
}
