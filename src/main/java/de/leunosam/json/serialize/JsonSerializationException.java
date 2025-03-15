package de.leunosam.json.serialize;

import de.leunosam.json.Json;

/**
 * This {@link RuntimeException} is thrown, if a {@link Json} could not be
 * serialized. This should never happen if you use the default
 * {@link JsonSerializer} of {@link Json}.
 * 
 * @author LeunoSam
 */
public class JsonSerializationException extends RuntimeException {

    private static final long serialVersionUID = -2073895795297981714L;

    public JsonSerializationException(Throwable cause) {
        super(cause);
    }

    public JsonSerializationException(String message) {
        super(message);
    }

}
