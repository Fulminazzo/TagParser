package it.fulminazzo.tagparser.nodes.exceptions.files;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * An exception thrown then creating a node from a folder.
 * Used in {@link it.fulminazzo.tagparser.nodes.Node#newNode(File)} and derivatives.
 */
public class FileIsDirectoryException extends FileException {
    /**
     * Instantiates a new File is directory exception.
     *
     * @param file the file
     */
    public FileIsDirectoryException(@NotNull File file) {
        super("%file% is a directory", file);
    }
}
