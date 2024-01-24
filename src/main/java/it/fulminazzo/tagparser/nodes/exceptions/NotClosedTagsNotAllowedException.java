package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;

public class NotClosedTagsNotAllowedException extends NodeException {

    public NotClosedTagsNotAllowedException(@NotNull String tagName) {
        super(String.format("Not closed tags are not allowed, but got <%s/>", tagName));
    }
}
