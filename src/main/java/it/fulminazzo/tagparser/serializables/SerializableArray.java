package it.fulminazzo.tagparser.serializables;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Represents an array of type T to be serialized.
 *
 * @param <T> the type parameter
 */
class SerializableArray<T> extends SerializableObject<T[]> {

    /**
     * Instantiates a new Serializable array.
     *
     * @param object the object
     */
    @SuppressWarnings("unchecked")
    SerializableArray(Object object) {
        super((T[]) object);
    }

    @Override
    public @NotNull String toYAML() {
        return new SerializableIterable(Arrays.asList(object)).toYAML();
    }

    @Override
    public @NotNull String toJSON() {
        return new SerializableIterable(Arrays.asList(object)).toJSON();
    }
}
