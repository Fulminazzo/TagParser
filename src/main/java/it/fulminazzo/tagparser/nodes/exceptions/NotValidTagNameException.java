package it.fulminazzo.tagparser.nodes.exceptions;

public class NotValidTagNameException extends NodeException {

    public NotValidTagNameException(String tagName) {
        super(String.format("%s does not match the following criteria: alphanumeric string with dashes or underscores", tagName));
    }
}
