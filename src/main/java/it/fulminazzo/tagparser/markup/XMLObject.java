package it.fulminazzo.tagparser.markup;

import it.fulminazzo.tagparser.Attributable;
import it.fulminazzo.tagparser.nodes.Node;
import it.fulminazzo.tagparser.nodes.NodeBuilder;
import it.fulminazzo.tagparser.serializables.Serializable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * An object to load, handle and dump XML files.
 */
@SuppressWarnings("UnusedReturnValue")
public class XMLObject implements Serializable, Attributable<XMLObject>, INodeObject {
    @Getter
    private String documentType;
    private final @NotNull Map<String, String> prologAttributes;
    @Getter
    private Node root;

    private XMLObject() {
        this.prologAttributes = new LinkedHashMap<>();
    }

    /**
     * Instantiates a new XML object.
     *
     * @param string the string
     */
    public XMLObject(@NotNull String string) {
        this();
        setRoot(string);
    }

    /**
     * Instantiates a new XML object.
     *
     * @param file the file
     */
    public XMLObject(@NotNull File file) {
        this();
        setRoot(file);
    }

    /**
     * Instantiates a new XML object.
     *
     * @param stream the stream
     */
    public XMLObject(@NotNull InputStream stream) {
        this();
        setRoot(stream);
    }

    /**
     * Set the root node from string.
     *
     * @param string the string
     * @return the root
     */
    public @NotNull XMLObject setRoot(@NotNull String string) {
        this.prologAttributes.clear();
        return setRoot(new XMLBuilder(this).from(string).build());
    }

    /**
     * Set the root node from file.
     *
     * @param file the file
     * @return the root
     */
    public @NotNull XMLObject setRoot(@NotNull File file) {
        this.prologAttributes.clear();
        return setRoot(new XMLBuilder(this).from(file).build());
    }

    /**
     * Set the root node from stream.
     *
     * @param stream the stream
     * @return the root
     */
    public @NotNull XMLObject setRoot(@NotNull InputStream stream) {
        this.prologAttributes.clear();
        return setRoot(new XMLBuilder(this).from(stream).build());
    }

    /**
     * Set the root node from the given node.
     *
     * @param node the node
     * @return the root
     */
    public @NotNull XMLObject setRoot(Node node) {
        this.root = node;
        return this;
    }

    @Override
    public @NotNull Map<String, String> getAttributes() {
        return prologAttributes;
    }

    @Override
    public void write(final @NotNull OutputStream stream) {
        if (documentType != null)
            try {
                stream.write("<?".getBytes());
                stream.write(documentType.getBytes());
                final @NotNull Map<String, String> attributes = getAttributes();
                for (String k : attributes.keySet()) {
                    final String v = attributes.get(k);
                    String output = " " + k;
                    if (v != null) output += String.format("=\"%s\"", v.replace("\"", "\\\""));
                    stream.write(output.getBytes());
                }
                stream.write("?>".getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        INodeObject.super.write(stream);
    }

    @Override
    public @NotNull String toHTML() {
        final StringBuilder output = new StringBuilder();
        if (this.documentType != null) {
            output.append("<?").append(this.documentType);
            this.prologAttributes.forEach((k, v) -> {
                output.append(" ").append(k);
                if (v != null) output.append("=\"")
                        .append(v.replace("\"", "\\\""))
                        .append("\"");
            });
            output.append("?>");
        }
        if (this.root != null) output.append(root.toHTML());
        return output.toString();
    }

    private static class XMLBuilder extends NodeBuilder {
        private final XMLObject xmlObject;
        private int read;

        private XMLBuilder(@NotNull XMLObject xmlObject) {
            this.xmlObject = xmlObject;
            this.read = 0;
            this.allowingClosingTags = false;
        }

        private XMLBuilder(@NotNull NodeBuilder builder, @NotNull XMLObject xmlObject) {
            super(builder);
            this.xmlObject = xmlObject;
            this.read = 0;
            this.allowingClosingTags = false;
        }

        /**
         * Allow closing tags.
         *
         * @deprecated XML does not support auto closing tags
         * @return this builder
         */
        @Override
        @Deprecated
        public @NotNull NodeBuilder allowClosingTags() {
            return this;
        }

        /**
         * Disallow closing tags.
         *
         * @deprecated XML does not support auto closing tags
         * @return this builder
         */
        @Override
        @Deprecated
        public @NotNull NodeBuilder disallowClosingTags() {
            return this;
        }

        @Override
        public boolean isAllowingClosingTags() {
            return false;
        }

        @Override
        protected char read(int start, @Nullable Predicate<Character> tester, @NotNull BiConsumer<StringBuilder, Character> read) throws IOException {
            boolean checkPrologue = this.read == 0;
            return super.read(start, tester, (buffer, readChar) -> {
                if (checkPrologue) {
                    if (buffer.length() > 1 && buffer.charAt(buffer.length() - 1) == '?' && buffer.charAt(buffer.length() - 2) == '<') {
                        Node node = createNode();
                        this.xmlObject.documentType = node.getTagName();
                        this.xmlObject.setAttributes(node.getAttributes());
                        return;
                    }
                }
                this.read++;
                read.accept(buffer, readChar);
            });
        }

        @Override
        public NodeBuilder cloneBuilder() {
            XMLBuilder builder = new XMLBuilder(this, this.xmlObject);
            builder.read = this.read;
            return builder;
        }
    }
}
