package it.fulminazzo.tagparser.serializables;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a map to be serialized.
 */
class SerializableMap extends SerializableObject<Map<?, ?>> {

    /**
     * Instantiates a new Serializable map.
     *
     * @param object the object
     */
    SerializableMap(Map<?, ?> object) {
        super(object);
    }

    @Override
    public @NotNull String toYAML() {
        final StringBuilder builder = new StringBuilder();
        if (object == null) builder.append("null");
        else if (object.isEmpty()) builder.append("{}");
        else object.forEach((k, v) -> builder.append(k).append(": ").append(SerializableObject.toYAML(v)).append("\n"));
        String output = builder.toString();
        if (output.endsWith("\n")) output = output.substring(0, output.length() - 1);
        return output;
    }

    @Override
    public @NotNull String toJSON() {
        final StringBuilder builder = new StringBuilder();
        if (object == null) builder.append("null");
        else {
            builder.append("{");
            object.forEach((k, v) ->
                    builder.append("\"").append(k).append("\"").append(":").append(SerializableObject.toJSON(v)).append(","));
            if (builder.length() != 1) builder.setLength(builder.length() - 1);
            builder.append("}");
        }
        return builder.toString();
    }
}
