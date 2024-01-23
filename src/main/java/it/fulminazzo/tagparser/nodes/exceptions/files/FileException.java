package it.fulminazzo.tagparser.nodes.exceptions.files;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidTagNameException;

import java.io.File;

public abstract class FileException extends NotValidTagNameException {

    public FileException(String message, File file) {
        super(message.replace("%file%", file.getAbsolutePath()));
    }
}
