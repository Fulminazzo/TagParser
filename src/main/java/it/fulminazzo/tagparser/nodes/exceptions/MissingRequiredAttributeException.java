package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An exception thrown when a required attribute is not given.
 */
public class MissingRequiredAttributeException extends RuntimeException {

    /**
     * Instantiates a new Missing required attribute exception.
     *
     * @param name the attribute name
     * @param attributes the attributes
     */
    public MissingRequiredAttributeException(@NotNull String name, @NotNull Map<String, String> attributes) {
        super(String.format("Could not find attribute \"%s\" in: %s", name, attributes));
    }
}
