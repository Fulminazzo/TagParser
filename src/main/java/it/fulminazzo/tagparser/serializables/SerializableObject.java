package it.fulminazzo.tagparser.serializables;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents a general Serializable object.
 *
 * @param <T> the type parameter
 */
abstract class SerializableObject<T> implements Serializable {
    protected final T object;

    /**
     * Instantiates a new Serializable object.
     *
     * @param object the object
     */
    SerializableObject(T object) {
        this.object = object;
    }

    @Override
    public abstract String toYAML();

    @Override
    public abstract String toJSON();

    @Override
    public @NotNull String toHTML() {
        return "";
    }

    /**
     * Convert the given object to YAML.
     *
     * @param object the object
     * @return the string
     */
    public static String toYAML(@Nullable Object object) {
        if (object == null) return "null";
        if (object instanceof Map) return new SerializableMap((Map<?, ?>) object).toYAML();
        if (object instanceof Iterable) return new SerializableIterable((Iterable<?>) object).toYAML();
        if (object.getClass().isArray()) return new SerializableArray<>(object).toYAML();
        if (object instanceof Serializable) return ((Serializable) object).toYAML();
        if (object instanceof String) return "\"" + object.toString().replace("\"", "\\\"") + "\"";
        return object.toString().replace("\"", "\\\"");
    }

    /**
     * Convert the given object to JSON.
     *
     * @param object the object
     * @return the string
     */
    public static String toJSON(@Nullable Object object) {
        if (object == null) return "null";
        if (object instanceof Map) return new SerializableMap((Map<?, ?>) object).toJSON();
        if (object instanceof Iterable) return new SerializableIterable((Iterable<?>) object).toJSON();
        if (object.getClass().isArray()) return new SerializableArray<>(object).toJSON();
        if (object instanceof Serializable) return ((Serializable) object).toJSON();
        if (object instanceof String) return "\"" + object.toString().replace("\"", "\\\"") + "\"";
        return object.toString().replace("\"", "\\\"");
    }
}
