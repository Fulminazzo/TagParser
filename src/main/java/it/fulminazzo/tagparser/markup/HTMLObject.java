package it.fulminazzo.tagparser.markup;

import it.fulminazzo.tagparser.nodes.ContainerNode;
import it.fulminazzo.tagparser.nodes.Node;
import it.fulminazzo.tagparser.nodes.NodeBuilder;
import it.fulminazzo.tagparser.serializables.Serializable;
import it.fulminazzo.tagparser.utils.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

@Getter
@SuppressWarnings("UnusedReturnValue")
public class HTMLObject implements Serializable, INodeObject {
    private Node root;

    public HTMLObject(@NotNull String string) {
        setRoot(string);
    }

    public HTMLObject(@NotNull File file) {
        setRoot(file);
    }

    public HTMLObject(InputStream stream) {
        setRoot(stream);
    }

    public @Nullable HTMLObject setRoot(@NotNull String string) {
        return setRoot(new HTMLBuilder().from(string).build());
    }
    
    public @Nullable HTMLObject setRoot(@NotNull File file) {
        return setRoot(new HTMLBuilder().from(file).build());
    }
    
    public @Nullable HTMLObject setRoot(InputStream stream) {
        return setRoot(new HTMLBuilder().from(stream).build());
    }
    
    public @NotNull HTMLObject setRoot(Node node) {
        this.root = node;
        return this;
    }

    public @Nullable Node getHead() {
        return root == null ? null : root.getNode("head");
    }

    public @Nullable Node getBody() {
        return root == null ? null : root.getNode("body");
    }

    public @NotNull Set<Node> getScripts() {
        return root == null ? new LinkedHashSet<>() : root.getNodes("script");
    }

    public @NotNull Set<Node> getStyles() {
        return root == null ? new LinkedHashSet<>() : root.getNodes(n -> {
            if (!n.getTagName().equalsIgnoreCase("link")) return false;
            final String rel = n.getAttribute("rel");
            if (rel == null) return false;
            else return rel.endsWith("stylesheet");
        });
    }

    @Override
    public void write(final OutputStream stream) {
        try {
            stream.write("<!DOCTYPE html>".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        INodeObject.super.write(stream);
    }

    @Override
    public @NotNull String toHTML() {
        final StringBuilder output = new StringBuilder("<!DOCTYPE html>");
        if (this.root != null) output.append(root.toHTML());
        return output.toString();
    }

    private static class HTMLNode extends ContainerNode {
        private final Map<String, Boolean> validTags;

        /**
         * Instantiates a new HTML node.
         *
         * @param tagName the tag name
         */
        public HTMLNode(@NotNull String tagName, Map<String, Boolean> validTags) {
            super(tagName);
            this.validTags = validTags;
        }

        @SuppressWarnings("DuplicatedCode")
        @Override
        public @NotNull String toHTML() {
            final StringBuilder builder = new StringBuilder("<").append(tagName);
            if (!this.attributes.isEmpty())
                this.attributes.forEach((k, v) -> {
                    if (k == null) return;
                    builder.append(" ").append(k);
                    if (v != null) builder.append("=\"")
                            .append(v.replace("\"", "\\\""))
                            .append("\"");
                });
            builder.append(">");

            if (text != null) builder.append(StringUtils.unParseContent(text));
            Node child = this.child;
            while (child != null) {
                String output = child.toHTML();
                if (child.getClass().equals(Node.class) && !validTags.getOrDefault(child.getTagName(), true))
                    output = output.substring(0, output.length() - 2) + ">";
                builder.append(output);
                child = child.getNext();
            }
            builder.append("</").append(tagName).append(">");
            return builder.toString();
        }
    }

    private static class HTMLBuilder extends NodeBuilder {
        private int read;

        private HTMLBuilder() {
            setOptions();
        }

        private HTMLBuilder(NodeBuilder builder) {
            super(builder);
            setOptions();
        }

        private void setOptions() {
            allowGeneralTags().addTag("meta", false)
                    .addTag("link", false)
                    .addTag("script", true);
        }

        @Override
        protected @NotNull Node createNode() {
            Node node = super.createNode();
            if (node instanceof ContainerNode) {
                ContainerNode n = (ContainerNode) node;
                HTMLNode tmp = new HTMLNode(n.getTagName(), validTags);
                tmp.setAttributes(n.getAttributes());
                tmp.setNext(n.getNext());
                tmp.setChild(n.getChild());
                node = tmp;
            }
            // Set attributes to lowercase.
            final @NotNull Map<String, String> attributes = new LinkedHashMap<>(node.getAttributes());
            for (final String k : attributes.keySet())
                node.unsetAttribute(k).setAttribute(k.toLowerCase(), attributes.get(k));
            return node;
        }

        @Override
        protected char read(int start, @Nullable Predicate<Character> tester, @NotNull BiConsumer<StringBuilder, Character> read) throws IOException {
            boolean checkPrologue = this.read == 0;
            return super.read(start, tester, (buffer, readChar) -> {
                if (checkPrologue) {
                    if (buffer.length() > 1 && buffer.charAt(buffer.length() - 1) == '!' && buffer.charAt(buffer.length() - 2) == '<') {
                        createNode();
                        return;
                    }
                }
                this.read++;
                read.accept(buffer, readChar);
            });
        }

        @Override
        public NodeBuilder cloneBuilder() {
            HTMLBuilder builder = new HTMLBuilder(this);
            builder.read = this.read;
            return builder;
        }
    }
}
