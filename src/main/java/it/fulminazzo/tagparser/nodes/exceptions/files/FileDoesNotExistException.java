package it.fulminazzo.tagparser.nodes.exceptions.files;

import java.io.File;

public class FileDoesNotExistException extends FileException {

    public FileDoesNotExistException(File file) {
        super("%file% does not exist", file);
    }
}
