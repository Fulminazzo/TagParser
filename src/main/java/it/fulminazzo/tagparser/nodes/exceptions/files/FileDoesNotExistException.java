package it.fulminazzo.tagparser.nodes.exceptions.files;

import java.io.File;

/**
 * An exception thrown then creating a node from a non-existing file.
 * Used in {@link it.fulminazzo.tagparser.nodes.Node#newNode(File)} and derivatives.
 */
public class FileDoesNotExistException extends FileException {

    /**
     * Instantiates a new File does not exist exception.
     *
     * @param file the file
     */
    public FileDoesNotExistException(File file) {
        super("%file% does not exist", file);
    }
}
