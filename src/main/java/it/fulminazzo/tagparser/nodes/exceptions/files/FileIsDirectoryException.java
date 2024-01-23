package it.fulminazzo.tagparser.nodes.exceptions.files;

import java.io.File;

public class FileIsDirectoryException extends FileException {
    public FileIsDirectoryException(File file) {
        super("%file% is a directory", file);
    }
}
