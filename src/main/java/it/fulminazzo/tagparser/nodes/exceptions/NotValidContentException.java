package it.fulminazzo.tagparser.nodes.exceptions;

public class NotValidContentException extends NodeException {

    /**
     * Instantiates a new Not valid content exception.
     *
     * @param contents      the contents
     * @param contentsRegex the contents regex
     */
    public NotValidContentException(@Nullable String contents, @NotNull String contentsRegex) {
        super(String.format("Expected \"%s\" but got {%s}", contentsRegex, contents));
    }
}
