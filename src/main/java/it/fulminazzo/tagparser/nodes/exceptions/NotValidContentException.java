package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception thrown when checking {@link it.fulminazzo.tagparser.nodes.NodeBuilder#validateContents(String)}
 */
public class NotValidContentException extends NodeException {

    /**
     * Instantiates a new not valid content exception.
     *
     * @param contents      the contents
     * @param contentsRegex the contents regex
     */
    public NotValidContentException(@Nullable String contents, @NotNull String contentsRegex) {
        super(String.format("Expected \"%s\" but got {%s}", contentsRegex, contents));
    }
}
