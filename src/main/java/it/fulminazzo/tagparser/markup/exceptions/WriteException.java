package it.fulminazzo.tagparser.markup.exceptions;

/**
 * An exception thrown when writing to file.
 */
public class WriteException extends RuntimeException {

    /**
     * Instantiates a new Write exception.
     *
     * @param message the message
     */
    public WriteException(final String message) {
        super(message);
    }
}
