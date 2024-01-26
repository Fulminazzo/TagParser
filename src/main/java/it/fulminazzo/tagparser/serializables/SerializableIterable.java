package it.fulminazzo.tagparser.serializables;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a iterable to be serialized.
 */
class SerializableIterable extends SerializableObject<Iterable<?>> {

    /**
     * Instantiates a new Serializable iterable.
     *
     * @param object the object
     */
    SerializableIterable(Iterable<?> object) {
        super(object);
    }

    @Override
    public @NotNull String toYAML() {
        final StringBuilder builder = new StringBuilder();
        if (object == null) builder.append("null");
        else {
            object.forEach(k -> builder.append("- ")
                    .append(SerializableObject.toYAML(k).replace("\n", "\n  "))
                    .append("\n"));
            if (builder.length() == 0) builder.append("[]");
        }
        return builder.toString();
    }

    @Override
    public @NotNull String toJSON() {
        final StringBuilder builder = new StringBuilder();
        if (object == null) builder.append("null");
        else {
            builder.append("[");
            object.forEach(k -> builder.append(SerializableObject.toJSON(k)).append(","));
            if (builder.length() != 1) builder.setLength(builder.length() - 1);
            builder.append("]");
        }
        return builder.toString();
    }
}
