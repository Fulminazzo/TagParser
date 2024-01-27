package it.fulminazzo.tagparser.markup;

import it.fulminazzo.tagparser.markup.exceptions.WriteException;
import it.fulminazzo.tagparser.nodes.Node;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * An INodeObject is a type that contains only one {@link it.fulminazzo.tagparser.nodes.Node}: the root.
 * It allows for easy conversion from any type of data stream into nodes or even maps containing variables.
 */
public interface INodeObject {

    /**
     * Get root.
     *
     * @return the root
     */
    Node getRoot();

    /**
     * Write to file.
     *
     * @param file the file
     */
    default void write(final @NotNull File file) {
        if (!file.getParentFile().isDirectory() && !file.mkdirs())
            throw new WriteException(String.format("Cannot create file parent directory: %s", file.getAbsolutePath()));

        try (final OutputStream stream = new FileOutputStream(file)) {
            write(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write to stream.
     *
     * @param stream the stream
     */
    default void write(final @NotNull OutputStream stream) {
        final Node root = getRoot();
        if (root == null) throw new WriteException("Cannot write null root node");

        try {
            stream.write(root.toHTML().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
