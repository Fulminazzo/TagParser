package it.fulminazzo.tagparser.nodes.exceptions;

/**
 * A general exception used in the {@link it.fulminazzo.tagparser.nodes.Node} class and derivatives.
 */
public class NodeException extends RuntimeException {

    /**
     * Instantiates a new Node exception.
     */
    public NodeException() {

    }

    /**
     * Instantiates a new Node exception.
     *
     * @param message the message
     */
    public NodeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Node exception.
     *
     * @param exception the exception
     */
    public NodeException(Exception exception) {
        super(exception.getMessage());
        setStackTrace(exception.getStackTrace());
    }
}
