package it.fulminazzo.tagparser.nodes.exceptions;

public class NotValidContentException extends NodeException {

    public NotValidContentException(String contents, String contentsRegex) {
        super(String.format("Expected \"%s\" but got {%s}", contentsRegex, contents));
    }
}
