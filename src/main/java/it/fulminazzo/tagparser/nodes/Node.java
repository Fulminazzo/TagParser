package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.nodes.exceptions.NodeException;
import it.fulminazzo.tagparser.nodes.exceptions.NotValidTagNameException;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileDoesNotExistException;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileIsDirectoryException;
import it.fulminazzo.tagparser.utils.StringUtils;
import lombok.Getter;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
public class Node {
    static final String TAG_NAME_REGEX = "[A-Za-z]([A-Za-z0-9_-]*[A-Za-z0-9])?";
    protected final String tagName;
    protected final Map<String, String> attributes;
    protected Node next;

    Node(String tagName) {
        if (!tagName.matches(TAG_NAME_REGEX))
            throw new NotValidTagNameException(tagName);
        this.tagName = tagName;
        this.attributes = new LinkedHashMap<>();
    }

    public Node setAttribute(String key, String value) {
        if (!key.matches(TAG_NAME_REGEX))
            throw new NotValidTagNameException(key);
        this.attributes.put(key, StringUtils.removeQuotes(value));
        return this;
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public Node setAttributes(String... attributes) {
        if (attributes != null && attributes.length > 1)
            for (int i = 0; i < attributes.length; i += 2)
                setAttribute(attributes[i], attributes[i + 1]);
        return this;
    }

    public Node setAttributes(Map<String, String> attributes) {
        this.attributes.clear();
        if (attributes != null) attributes.forEach(this::setAttribute);
        return this;
    }

    public int countNextNodes() {
        if (this.next != null) return 1 + this.next.countNextNodes();
        else return 0;
    }

    public Node addNext(String string) {
        return addNext(Node.newNode(string));
    }

    public Node addNext(File file) {
        return addNext(Node.newNode(file));
    }

    public Node addNext(InputStream stream) {
        return addNext(Node.newNode(stream));
    }

    public Node addNext(Node next) {
        if (this.next != null) this.next.addNext(next);
        else this.next = next;
        return this;
    }

    public Node removeNext(Node next) {
        return removeNext(n -> n.equals(next));
    }

    public Node removeNext(Predicate<Node> predicate) {
        if (this.next == null) return this;
        if (predicate.test(this.next)) {
            this.next.removeNext(predicate);
            this.next = this.next.next;
        }
        return this;
    }

    public Node setNext(String string) {
        return setNext(Node.newNode(string));
    }

    public Node setNext(File file) {
        return setNext(Node.newNode(file));
    }

    public Node setNext(InputStream stream) {
        return setNext(Node.newNode(stream));
    }

    public Node setNext(Node next) {
        this.next = next;
        return this;
    }

    public String toHTML() {
        final StringBuilder builder = new StringBuilder("<").append(tagName);
        if (!this.attributes.isEmpty()) {
            this.attributes.forEach((k, v) -> {
                if (k == null) return;
                builder.append(" ").append(k);
                if (v != null) builder.append("=\"")
                        .append(v.replace("\"", "\\\""))
                        .append("\"");
            });
        }
        return builder.append("/>").toString();
    }

    public String toJson() {
        final StringBuilder builder = new StringBuilder("{");

        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields())
                if (!Modifier.isStatic(field.getModifiers())) {
                    if (builder.length() != 1) builder.append(",");
                    builder.append("\"").append(field.getName()).append("\":");
                    try {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        if (value instanceof Map) {
                            builder.append("{");
                            Map<?, ?> obj = (Map<?, ?>) value;
                            obj.forEach((k, v) -> {
                                if (builder.charAt(builder.length() - 1) != '{') builder.append(",");
                                if (k == null) return;
                                builder.append("\"").append(k).append("\":");
                                if (v != null) builder.append("\"")
                                        .append(v.toString().replace("\"", "\\\""))
                                        .append("\"");
                                else builder.append("null");
                            });
                            builder.append("}");
                        } else {
                            if (value instanceof String) builder.append("\"");
                            if (value instanceof Node) builder.append(((Node) value).toJson());
                            else builder.append(value);
                            if (value instanceof String) builder.append("\"");
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            clazz = clazz.getSuperclass();
        }
        return builder.append("}").toString();
    }

    protected String printField(Field field) {
        final StringBuilder builder = new StringBuilder();
        try {
            builder.append(field.getName()).append(": ");
            field.setAccessible(true);
            Object value = field.get(this);
            if (value instanceof String) builder.append("\"");
            if (field.getName().equals("next") && next != null)
                builder.append(next.getClass().getSimpleName())
                        .append("(\"")
                        .append(next.getTagName())
                        .append("\")");
            else builder.append(value);
            if (value instanceof String) builder.append("\"");
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    public boolean equals(Node node) {
        if (node == null) return false;
        if (!this.tagName.equals(node.getTagName())) return false;
        if (!Objects.equals(this.next, node.getNext())) return false;
        return Objects.equals(this.attributes, node.getAttributes());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) return equals((Node) o);
        return super.equals(o);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getSimpleName() + " {");

        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields())
                if (!Modifier.isStatic(field.getModifiers()))
                    builder.append("\n    ").append(printField(field));
            clazz = clazz.getSuperclass();
        }
        return builder.append("\n}").toString();
    }

    public static Node newNode(String string) {
        return string == null ? null : newNode(new ByteArrayInputStream(string.getBytes()));
    }

    public static Node newNode(File file) {
        try {
            if (file.isDirectory()) throw new FileIsDirectoryException(file);
            return newNode(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new FileDoesNotExistException(file);
        }
    }

    public static Node newNode(InputStream stream) {
        return newNode(new StringBuilder(), stream, true);
    }

    static Node newNode(final StringBuilder buffer, InputStream stream, boolean checkNext) {
        try {
            final Map<String, String> attributes = new LinkedHashMap<>();

            int read = 0;

            // Read tag name from given stream.
            read = read(stream, read, buffer, null, r -> {
                if (r.toString().matches("[\t\n\r]")) return;
                if (r == '<')
                    if (buffer.toString().contains("<")) throw new NotValidTagNameException(buffer.toString());
                    else return;
                if (r == ' ' || r == '>') throw new NodeException();
                else buffer.append(r);
            });
            String tagName = buffer.toString();
            final Node node;
            boolean isContainer = true;
            if (read == '>' && tagName.endsWith("/")) {
                isContainer = false;
                tagName = tagName.substring(0, tagName.length() - 1);
            }
            buffer.setLength(0);

            if (read == ' ') {
                String key = "";
                int openQuotes = -1;
                // Read attributes from given stream.
                while ((read = stream.read()) != -1)
                    if (read == openQuotes && buffer.charAt(buffer.length() - 1) != '\\') openQuotes = -1;
                    else if (buffer.length() == 0 && (read == '"' || read == '\'')) openQuotes = read;
                    else {
                        if (read == '=') {
                            key = buffer.toString();
                            buffer.setLength(0);
                        } else if (read == ' ' || read == '>') {
                            String value = buffer.toString();
                            if (value.endsWith("/")) value = value.substring(0, value.length() - 1);
                            if (key.isEmpty()) key = value;
                            if (value.equals(key)) value = null;
                            if (!key.isEmpty()) attributes.put(key, value);
                            key = "";
                            if (read == '>') {
                                if (buffer.length() > 0)
                                    isContainer = buffer.charAt(buffer.length() - 1) != '/';
                                break;
                            }
                            buffer.setLength(0);
                        } else buffer.append((char) read);
                    }
                buffer.setLength(0);
            }

            if (!isContainer) node = new Node(tagName);
            else node = new ContainerNode(tagName);
            node.setAttributes(attributes);

            if (node instanceof ContainerNode) {
                ContainerNode containerNode = (ContainerNode) node;
                // Read contents from given stream.
                final String end = "</" + tagName + ">";
                read(stream, read, buffer, () -> !buffer.toString().endsWith(end), r -> {
                    if (r != '/' && buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == '<') {
                        buffer.setLength(buffer.length() - 1);
                        Node n = Node.newNode(new StringBuilder().append((char) r), stream, false);
                        containerNode.addChild(n);
                    } else buffer.append((char) r);
                });

                String text = buffer.toString();
                if (text.endsWith(end))
                    text = text.substring(0, text.length() - end.length());
                else throw new NodeException(String.format("Node \"%s\" not closed. Raw text: \"%s\"", tagName, text));

                containerNode.setText(text);
            }

            // Check for other content to be added.
            if (checkNext && stream.available() > 0) node.setNext(stream);

            return node;
        } catch (IOException e) {
            throw new NodeException(e);
        }
    }

    private static char read(InputStream stream, int start, StringBuilder buffer, BooleanSupplier tester, Consumer<Character> read) throws IOException {
        final StringBuilder commentBuffer = new StringBuilder().append((char) start);
        boolean commented = false;
        int r = 0;
        while ((tester == null || tester.getAsBoolean()) && (r = stream.read()) != -1)
            try {
                commentBuffer.append((char) r);
                if (commented) {
                    if (commentBuffer.toString().endsWith("-->")) {
                        commentBuffer.setLength(0);
                        commented = false;
                    }
                } else {
                    if (commentBuffer.toString().endsWith("<!--")) {
                        commentBuffer.setLength(0);
                        commented = true;
                        buffer.setLength(0);
                    } else read.accept((char) r);
                }
                if (commentBuffer.length() > 4) commentBuffer.delete(0, commentBuffer.length() - 4);
            } catch (NodeException e) {
                break;
            }
        return (char) r;
    }
}
