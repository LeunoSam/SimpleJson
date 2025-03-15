package de.leunosam.json;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.leunosam.json.deserialize.JsonDeserializationException;
import de.leunosam.json.deserialize.JsonDeserializer;
import de.leunosam.json.serialize.JsonSerializationException;
import de.leunosam.json.serialize.JsonSerializer;
import de.leunosam.json.util.JsonClass;
import de.leunosam.json.util.JsonField;
import de.leunosam.json.util.JsonFields;

/**
 * This abstract class should be used as super-class if you want to serialize or
 * deserialize an Object of your child class, which should be included in the
 * serialization. You need to add the annotation {@link JsonField} to the
 * class-fields. Json implements the interface {@link Serializable}.
 * 
 * @author LeunoSam
 */
public abstract class Json implements Serializable {

    private static final long serialVersionUID = 3916914607743930988L;

    /**
     * Creates a new Json without any {@link JsonField}s.
     */
    public Json() {
        super();
    }

    /**
     * Creates a new Json with the values of the given Json-String.
     * 
     * @param jsonString a {@link String}, which contains a verified Json.
     */
    public Json(String jsonString) {
        super();
        this.deserialize(jsonString);
    }

    /**
     * Return an {@link JsonDeserializer}, which can convert the a Json-String to an
     * instance of your Json-class.<br>
     * Hint: you can change the return value of this function to
     * JsonDeserializer&ltYourClass&gt.
     * 
     * @return a {@link JsonDeserializer} for your class
     */
    protected abstract JsonDeserializer<? extends Json> getDeserializer();

    /**
     * Deserializes the given Json-String and saves its values into the Json-Object.
     * 
     * @param json a {@link String}, which is a representation of this Json-Class.
     * 
     * @throws JsonDeserializationException if the deserialization fails.
     */
    public void deserialize(String json) {
        try {
            update(getDeserializer().getObject(json));
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // should never happen, except wrong string
            throw new JsonDeserializationException(e);
        }
    }

    private <T extends Json> void update(T newValues)
            throws IllegalArgumentException, IllegalAccessException {
        if (!this.getClass().equals(newValues.getClass())) {
            throw new IllegalStateException("Deserializer returns  not the right class!");
        }

        for (Field field : getJsonFields()) {
            field.set(this, field.get(newValues));
        }
    }

    /**
     * Serializes all {@link JsonField}s into a valid Json-String.
     * 
     * @throws throws a {@link JsonSerializationException} if the serialization
     *                fails.
     */
    public String serialize() {
        return getSerializer().toString();
    }

    /**
     * Generates a {@link JsonSerializer} with the help of the
     * {@link JsonField}-Annotations. This works through reflections. If you don't
     * want to use this JsonSerializer, because it's too slow, write a new
     * JsonSerializer for your Json and override this method.
     * 
     * @return a {@link JsonSerializer}, which contains all {@link JsonField}
     *         values.
     * @throws throws a {@link JsonSerializationException} if the serialization
     *                fails.
     */
    protected JsonSerializer getSerializer() {
        JsonSerializer serializer = new JsonSerializer();
        if (getClass().isAnnotationPresent(JsonClass.class)) {
            serializer.addStringField("class", getClass().getSimpleName());
        }
        Object value;
        String fieldName;
        try {
            for (Field field : getJsonFields()) {
                value = field.get(this);
                fieldName = determineJsonFieldName(field);
                addFieldToSerializer(value, fieldName, field, serializer);
            }
        } catch (IllegalAccessException e) {
            throw new JsonSerializationException(e);
        }
        return serializer;
    }

    private static void addFieldToSerializer(Object value, String fieldName, Field field,
            JsonSerializer serializer) throws IllegalArgumentException {
        if (value instanceof Boolean b) {
            serializer.addBooleanField(fieldName, b);
        } else if (value instanceof Integer i) {
            serializer.addIntegerField(fieldName, i);
        } else if (value instanceof Long l) {
            serializer.addLongField(fieldName, l);
        } else if (value instanceof Double d) {
            serializer.addDoubleField(fieldName, d);
        } else if (value instanceof String str) {
            serializer.addStringField(fieldName, str);
        } else if (value instanceof Json edo) {
            serializer.addObjectField(fieldName, edo);
        } else if (value instanceof List<?> list) {
            addListToSerializer(list, field, fieldName, serializer);
        } else if (value == null) {
            serializer.addNullField(fieldName);
        } else {
            throw new IllegalArgumentException(
                    "The field " + fieldName + " is not json-serializeable");
        }
    }

    @SuppressWarnings("unchecked")
    private static void addListToSerializer(List<?> list, Field listField, String fieldName,
            JsonSerializer serializer) {
        ParameterizedType listType = (ParameterizedType) listField.getGenericType();
        Class<?> listTypeClass = (Class<?>) listType.getActualTypeArguments()[0];
        if (listTypeClass.equals(Integer.class)) {
            serializer.addBooleanArrayField(fieldName, (List<Boolean>) list);
        } else if (listTypeClass.equals(Integer.class)) {
            serializer.addIntegerArrayField(fieldName, (List<Integer>) list);
        } else if (listTypeClass.equals(Long.class)) {
            serializer.addLongArrayField(fieldName, (List<Long>) list);
        } else if (listTypeClass.equals(Double.class)) {
            serializer.addDoubleArrayField(fieldName, (List<Double>) list);
        } else if (listTypeClass.equals(String.class)) {
            serializer.addStringArrayField(fieldName, (List<String>) list);
        } else if (isJson(listTypeClass)) {
            serializer.addObjectArrayField(fieldName, (List<Json>) list);
        } else {
            throw new JsonSerializationException(
                    "The List " + fieldName + " contains a not serializeable Type!");
        }
    }

    private static boolean isJson(Class<?> clazz) {
        if (clazz.equals(Json.class)) {
            return true;
        }
        Class<?> superClazz = clazz.getSuperclass();
        return superClazz.equals(Object.class) ? false : isJson(superClazz);
    }

    private String determineJsonFieldName(Field field) {
        String fieldName = field.getAnnotation(JsonField.class).value();
        if (fieldName.isBlank()) {
            fieldName = field.getName();
        }
        return fieldName;
    }

    private List<Field> getJsonFields() {
        return JsonFields.getJsonFields(getClass());
    }

    @Override
    public int hashCode() {
        List<Object> objects = new LinkedList<>();
        for (Field field : getJsonFields()) {
            try {
                objects.add(field.get(this));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // should never happen
                e.printStackTrace();
            }
        }
        return Objects.hash(objects);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        try {
            Object thisValue;
            Object otherValue;
            for (Field field : getJsonFields()) {
                thisValue = field.get(this);
                otherValue = field.get(other);
                // null check, otherwise equals is not possible
                if (thisValue == null && otherValue == null) {
                    continue;
                } else if (thisValue == null) {
                    return false;
                } else if (!thisValue.equals(otherValue)) {
                    return false;
                }
            }
        } catch (IllegalAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return serialize();
    }

}
