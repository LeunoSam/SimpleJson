package de.leunosam.json.serialize;

import de.leunosam.json.Json;
import de.leunosam.json.util.JsonConstants;

/**
 * This class runs through a Json-String with no white-spaces and adds
 * white-spaces for better readability.
 * 
 * @author LeunoSam
 */
public class JsonPrettifier {

    private int tabLevel = 0;
    private boolean isString = false;

    /**
     * Returns a prettified version of the serialized {@link Json}.
     * 
     * @param json a {@link Json}.
     * @return the prettified Json.
     */
    public String prettify(Json json) {
        return prettify(json.serialize());
    }

    /**
     * Returns a prettified version of the given Json-String. Warning: The
     * {@link String} should contain no white-spaces!
     * 
     * @param json a {@link String} with a Json.
     * @return the prettified Json.
     */
    public String prettify(String json) {
        StringBuilder builder = new StringBuilder();
        for (char c : json.toCharArray()) {
            builder.append(c);
            if (c == '"') {
                isString = !isString;
            } else if (c == ':' && !isString) {
                builder.append('\s');
            } else if (c == ',' && !isString) {
                builder.append('\n');
                builder.append(getCurrentTabLevel());
            } else if ((c == '[' || c == '{') && !isString) {
                builder.append('\n');
                tabLevel++;
                builder.append(getCurrentTabLevel());
            } else if ((c == ']' || c == '}') && !isString) {
                builder.deleteCharAt(builder.length() - 1);
                builder.append('\n');
                tabLevel--;
                builder.append(getCurrentTabLevel());
                builder.append(c);
            }
        }
        // if the json was not verified tabLevel != 0 --> reset!
        tabLevel = 0;
        return builder.toString();
    }

    private String getCurrentTabLevel() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tabLevel; i++) {
            builder.append(JsonConstants.TAB);
        }
        return builder.toString();
    }

}
