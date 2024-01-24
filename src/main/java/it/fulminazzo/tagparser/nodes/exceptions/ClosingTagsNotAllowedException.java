package it.fulminazzo.tagparser.nodes.exceptions;

public class ClosingTagsNotAllowedException extends NodeException {

    public ClosingTagsNotAllowedException(String tagName) {
        super(String.format("Closing tags are not allowed, but got <%s></%s>", tagName, tagName));
    }
}
