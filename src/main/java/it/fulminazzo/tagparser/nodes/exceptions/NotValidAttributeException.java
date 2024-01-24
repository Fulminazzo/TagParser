package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An exception thrown when a given attribute results not valid.
 */
public class NotValidAttributeException extends RuntimeException {

    /**
     * Instantiates a new not valid attribute exception.
     *
     * @param name      the name
     * @param expected  the expected
     * @param attribute the attribute
     */
    public NotValidAttributeException(@NotNull String name, @NotNull Class<?> expected, @Nullable String attribute) {
        this(name, expected.getSimpleName(), attribute);
    }

    /**
     * Instantiates a new not valid attribute exception.
     *
     * @param name      the name
     * @param expected  the expected
     * @param attribute the attribute
     */
    public NotValidAttributeException(@NotNull String name, @NotNull String expected, @Nullable String attribute) {
        super(String.format("Could not validate attribute \"%s\": expected \"%s\", but got \"%s\"",
                name, expected, attribute));
    }
}