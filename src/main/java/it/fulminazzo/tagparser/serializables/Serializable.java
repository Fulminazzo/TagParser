package it.fulminazzo.tagparser.serializables;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * A general interface used to automatically serialize an object to JSON, YAML and HTML.
 */
public interface Serializable {

    /**
     * Converts the current object in a HTML format.
     *
     * @return the string
     */
    String toHTML();

    /**
     * Converts the current object in a YAML format.
     *
     * @return the string
     */
    default String toYAML() {
        final String SEPARATOR = "    ";
        final StringBuilder builder = new StringBuilder();

        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields())
                if (!field.getName().equals("this$0") && !Modifier.isStatic(field.getModifiers())) {
                    if (builder.length() != 0 && builder.charAt(builder.length() - 1) != '\n') builder.append("\n");
                    builder.append(field.getName()).append(":");
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        final String serialized = SerializableObject.toYAML(value);
                        if (value instanceof Iterable || (value != null && value.getClass().isArray())) {
                            if (serialized.length() > 2) builder.append("\n");
                            else builder.append(" ");
                            builder.append(serialized);
                            continue;
                        } else if ((value instanceof Map || value instanceof Serializable) && serialized.length() > 2) builder.append("\n" + SEPARATOR);
                        else builder.append(" ");
                        builder.append(serialized.replace("\n", "\n" + SEPARATOR));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            clazz = clazz.getSuperclass();
        }
        return builder.toString();
    }

    /**
     * Converts the current object in a JSON format.
     *
     * @return the string
     */
    default String toJSON() {
        final StringBuilder builder = new StringBuilder("{");

        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields())
                if (!field.getName().equals("this$0") && !Modifier.isStatic(field.getModifiers())) {
                    if (builder.length() != 1) builder.append(",");
                    builder.append("\"").append(field.getName()).append("\":");
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        builder.append(SerializableObject.toJSON(value));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            clazz = clazz.getSuperclass();
        }
        return builder.append("}").toString();
    }
}