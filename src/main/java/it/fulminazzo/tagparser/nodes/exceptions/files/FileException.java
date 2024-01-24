package it.fulminazzo.tagparser.nodes.exceptions.files;

import it.fulminazzo.tagparser.nodes.exceptions.NodeException;

import java.io.File;

/**
 * A general node exception thrown when working with files.
 */
public abstract class FileException extends NodeException {

    /**
     * Instantiates a new File exception.
     *
     * @param message the message
     * @param file    the file
     */
    public FileException(String message, File file) {
        super(message.replace("%file%", file.getAbsolutePath()));
    }
}
