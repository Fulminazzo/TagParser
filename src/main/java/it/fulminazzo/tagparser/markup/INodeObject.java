package it.fulminazzo.tagparser.markup;

import it.fulminazzo.tagparser.markup.exceptions.WriteException;
import it.fulminazzo.tagparser.nodes.ContainerNode;
import it.fulminazzo.tagparser.nodes.Node;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

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
     * Convert the root node to a map of elements.
     * Duplicate nodes are NOT allowed.
     *
     * @return the map
     */
    default @NotNull Map<?, ?> toMap() {
        return toMap(getRoot());
    }

    /**
     * Convert the given node to a map of elements.
     * Duplicate nodes are NOT allowed.
     *
     * @param node the node
     * @return the map
     */
    default @NotNull Map<?, ?> toMap(@Nullable Node node) {
        final Map<Object, Object> map = new LinkedHashMap<>();
        while (node != null) {
            final String tagName = node.getTagName();
            if (node instanceof ContainerNode) {
                final ContainerNode containerNode = (ContainerNode) node;
                if (containerNode.getChild() != null)
                    map.put(tagName, toMap(containerNode.getChild()));
                else map.put(tagName, containerNode.getText());
            } else map.put(tagName, null);
            node = node.getNext();
        }
        return map;
    }

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
