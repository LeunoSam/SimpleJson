package de.leunosam.json.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.leunosam.json.Json;

/**
 * This is a multiton, which determines and saves all {@link Field}s annotated
 * with {@link JsonField} of a {@link Json}-Class during the runtime.
 * 
 * @author LeunoSam
 */
public class JsonFields {

    private static Map<Class<? extends Json>, List<Field>> fields = new HashMap<>();

    private JsonFields() {
        // hide
    }

    private static List<Field> addKeyValue(Class<? extends Json> jsonClass) {
        List<Field> list = new LinkedList<>();
        for (Field f : jsonClass.getDeclaredFields()) {
            if (!f.isAnnotationPresent(JsonField.class)) {
                continue;
            }
            f.setAccessible(true);
            list.add(f);
        }
        fields.put(jsonClass, list);
        return list;
    }

    /**
     * Get the {@link Field}s of the given {@link Json}-Class, which are annotated
     * with {@link JsonField}.
     * 
     * @param jsonClass a {@link Class}
     * @return a {@link List} of {@link Field}s, which will be set to accessible.
     */
    public static List<Field> getJsonFields(Class<? extends Json> jsonClass) {
        return fields.containsKey(jsonClass) ? fields.get(jsonClass) : addKeyValue(jsonClass);
    }

}
