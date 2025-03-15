package de.leunosam.json.deserialize;

import de.leunosam.json.Json;

/**
 * This {@link RuntimeException} is thrown if the deserialization of a
 * {@link Json} fails.
 * 
 * @author LeunoSam
 */
public class JsonDeserializationException extends RuntimeException {

    private static final long serialVersionUID = -6171074280659784161L;

    public JsonDeserializationException(Throwable cause) {
        super(cause);
    }

}
