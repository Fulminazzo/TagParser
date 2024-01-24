package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;

public class ClosingTagsNotAllowedException extends NodeException {

    public ClosingTagsNotAllowedException(@NotNull String tagName) {
        super(String.format("Closing tags are not allowed, but got <%s></%s>", tagName, tagName));
    }
}
