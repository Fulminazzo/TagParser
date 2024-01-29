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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    Node getRootNode();

    /**
     * Convert the root node to a map of elements.
     * Duplicate nodes are NOT allowed.
     *
     * @return the map
     */
    default @NotNull Map<?, ?> toMap() {
        return toMap(getRootNode(), true);
    }

    /**
     * Convert the given node to a map of elements.
     * Duplicate nodes are NOT allowed.
     *
     * @param node      the node
     * @param checkNext if true, will check and add the next node
     * @return the map
     */
    default @NotNull Map<?, ?> toMap(@Nullable Node node, boolean checkNext) {
        final Map<Object, Object> map = new LinkedHashMap<>();
        while (node != null) {
            final String tagName = node.getTagName();
            if (node instanceof ContainerNode) {
                final ContainerNode containerNode = (ContainerNode) node;
                final List<Node> children = containerNode.getChildren();
                final String childTag = children.stream().filter(Objects::nonNull)
                        .map(Node::getTagName)
                        .findFirst().orElse(null);
                if (children.size() > 1 && childTag != null && children.stream().allMatch(c -> c.getTagName().equals(childTag)))
                    map.put(tagName, children.stream()
                            .map(c -> toMap(c, false).get(c.getTagName()))
                            .collect(Collectors.toList()));
                else if (containerNode.getChild() != null)
                    map.put(tagName, toMap(containerNode.getChild(), true));
                else map.put(tagName, containerNode.getText());
            } else map.put(tagName, null);
            if (!checkNext) break;
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
        final Node root = getRootNode();
        if (root == null) throw new WriteException("Cannot write null root node");

        try {
            stream.write(root.toHTML().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
