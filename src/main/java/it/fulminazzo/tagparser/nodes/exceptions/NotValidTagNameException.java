package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown when the given tag name is invalid.
 * Used in {@link it.fulminazzo.tagparser.nodes.Node#newNode(java.io.InputStream)}
 */
public class NotValidTagNameException extends NodeException {

    /**
     * Instantiates a new Not valid tag name exception.
     *
     * @param tagName the tag name
     */
    public NotValidTagNameException(@NotNull String tagName) {
        super(String.format("%s does not match the following criteria: alphanumeric string with dashes or underscores", tagName));
    }
}
