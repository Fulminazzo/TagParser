package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;

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
    public NotValidAttributeException(String name, @NotNull Class<?> expected, String attribute) {
        this(name, expected.getSimpleName(), attribute);
    }

    /**
     * Instantiates a new not valid attribute exception.
     *
     * @param name      the name
     * @param expected  the expected
     * @param attribute the attribute
     */
    public NotValidAttributeException(String name, String expected, String attribute) {
        super(String.format("Could not validate attribute \"%s\": expected \"%s\", but got \"%s\"",
                name, expected, attribute));
    }
}