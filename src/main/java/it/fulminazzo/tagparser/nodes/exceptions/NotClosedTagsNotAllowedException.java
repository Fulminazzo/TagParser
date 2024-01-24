package it.fulminazzo.tagparser.nodes.exceptions;

public class NotClosedTagsNotAllowedException extends NodeException {

    public NotClosedTagsNotAllowedException(String tagName) {
        super(String.format("Not closed tags are not allowed, but got <%s/>", tagName));
    }
}
