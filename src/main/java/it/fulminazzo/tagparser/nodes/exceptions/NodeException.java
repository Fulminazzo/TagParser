package it.fulminazzo.tagparser.nodes.exceptions;

public class NodeException extends RuntimeException {

    public NodeException(String message) {
        super(message);
    }

    public NodeException(Exception exception) {
        super(exception.getMessage());
        setStackTrace(exception.getStackTrace());
    }
}
