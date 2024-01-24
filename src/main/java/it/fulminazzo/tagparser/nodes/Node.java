package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.nodes.exceptions.EmptyNodeException;
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
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The most basic type of node.
 * This node does not support any type of tag verification or content recognition.
 * It only supports attributes and does not require closing tags.
 * <p>
 * Example: &#60;img src="test.png" alt="This will be wrapped in a simple Node" />
 * <p>
 * NOTE: the ending /> is REQUIRED.
 */
@Getter
public class Node {
    static final String TAG_NAME_REGEX = "[A-Za-z]([A-Za-z0-9_-]*[A-Za-z0-9])?";
    protected final String tagName;
    protected final Map<String, String> attributes;
    protected Node next;

    /**
     * Instantiates a new Node.
     *
     * @param tagName the tag name
     */
    Node(String tagName) {
        if (!tagName.matches(TAG_NAME_REGEX))
            throw new NotValidTagNameException(tagName);
        this.tagName = tagName;
        this.attributes = new LinkedHashMap<>();
    }

    /**
     * Sets attribute.
     *
     * @param name   the name
     * @param value the value
     * @return the attribute
     */
    public Node setAttribute(String name, String value) {
        if (!name.matches(TAG_NAME_REGEX))
            throw new NotValidTagNameException(name);
        this.attributes.put(name, StringUtils.removeQuotes(value));
        return this;
    }

    /**
     * Gets attribute.
     *
     * @param name the name
     * @return the attribute
     */
    public String getAttribute(String name) {
        return this.attributes.get(name);
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     * @return the attributes
     */
    public Node setAttributes(String... attributes) {
        if (attributes != null && attributes.length > 1)
            for (int i = 0; i < attributes.length; i += 2)
                setAttribute(attributes[i], attributes[i + 1]);
        return this;
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     * @return the attributes
     */
    public Node setAttributes(Map<String, String> attributes) {
        this.attributes.clear();
        if (attributes != null) attributes.forEach(this::setAttribute);
        return this;
    }

    /**
     * Gets the nodes that succeed the current one.
     *
     * @return the nodes
     */
    public int countNextNodes() {
        if (this.next != null) return 1 + this.next.countNextNodes();
        else return 0;
    }

    /**
     * Add the next node.
     *
     * @param string the string
     * @return the node
     */
    public Node addNext(String string) {
        return addNext(Node.newNode(string));
    }

    /**
     * Add the next node.
     *
     * @param file the file
     * @return the node
     */
    public Node addNext(File file) {
        return addNext(Node.newNode(file));
    }

    /**
     * Add the next node.
     *
     * @param stream the stream
     * @return the node
     */
    public Node addNext(InputStream stream) {
        return addNext(Node.newNode(stream));
    }

    /**
     * Add the next node.
     *
     * @param next the next
     * @return the node
     */
    public Node addNext(Node next) {
        if (this.next != null) this.next.addNext(next);
        else this.next = next;
        return this;
    }

    /**
     * Remove the next node.
     *
     * @param next the next
     * @return the node
     */
    public Node removeNext(Node next) {
        return removeNext(n -> n.equals(next));
    }

    /**
     * Remove the next node.
     *
     * @param predicate the predicate used to verify if the node should be removed or not
     * @return the node
     */
    public Node removeNext(Predicate<Node> predicate) {
        if (this.next == null) return this;
        if (predicate.test(this.next)) {
            this.next.removeNext(predicate);
            this.next = this.next.next;
        }
        return this;
    }

    /**
     * Sets the next node.
     *
     * @param string the string
     * @return the next
     */
    public Node setNext(String string) {
        return setNext(Node.newNode(string));
    }

    /**
     * Sets the next node.
     *
     * @param file the file
     * @return the next
     */
    public Node setNext(File file) {
        return setNext(Node.newNode(file));
    }

    /**
     * Sets the next node.
     *
     * @param stream the stream
     * @return the next
     */
    public Node setNext(InputStream stream) {
        return setNext(Node.newNode(stream));
    }

    /**
     * Sets the next node.
     *
     * @param next the next
     * @return the next
     */
    public Node setNext(Node next) {
        this.next = next;
        return this;
    }

    /**
     * Converts the current node in a HTML format.
     *
     * @return the string
     */
    public String toHTML() {
        final StringBuilder builder = new StringBuilder("<").append(tagName);
        if (!this.attributes.isEmpty())
            this.attributes.forEach((k, v) -> {
                if (k == null) return;
                builder.append(" ").append(k);
                if (v != null) builder.append("=\"")
                        .append(v.replace("\"", "\\\""))
                        .append("\"");
            });
        return builder.append("/>").toString();
    }

    /**
     * Converts the current node in a JSON format.
     *
     * @return the string
     */
    public String toJSON() {
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
                            if (value instanceof Node) builder.append(((Node) value).toJSON());
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

    /**
     * Compares the current node with another one.
     *
     * @param node the node
     * @return the boolean
     */
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

    /**
     * Prints only the given field of this node.
     *
     * @param field the field
     * @return the string
     */
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

    /**
     * Creates a new node from the raw string.
     *
     * @param string the string
     * @return the node
     */
    public static Node newNode(String string) {
        return string == null ? null : newNode(new ByteArrayInputStream(string.getBytes()));
    }

    /**
     * Creates a new node from the given file.
     *
     * @param file the file
     * @return the node
     */
    public static Node newNode(File file) {
        try {
            if (file.isDirectory()) throw new FileIsDirectoryException(file);
            return newNode(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new FileDoesNotExistException(file);
        }
    }

    /**
     * Creates a new node from the raw stream.
     *
     * @param stream the stream
     * @return the node
     */
    public static Node newNode(InputStream stream) {
        return newNode(new StringBuilder(), stream, true);
    }

    /**
     * Creates a new node from the raw stream.
     * Uses buffer as the initial buffer.
     *
     * @param buffer    the buffer
     * @param stream    the stream
     * @param checkNext toggle this option to check for next elements
     * @return the node
     */
    static Node newNode(final StringBuilder buffer, InputStream stream, boolean checkNext) {
        try {
            final Map<String, String> attributes = new LinkedHashMap<>();

            // Read tag name from given stream.
            int read = read(stream, 0, buffer, r -> buffer.indexOf("<") == -1 || (r != ' ' && r != '>'), r -> {
                if (r.toString().matches("[\t\n\r]")) return;
                if (r == '<' && buffer.toString().contains("<")) throw new NotValidTagNameException(buffer.toString());
                buffer.append(r);
            });
            String tagName = buffer.toString();
            if (tagName.endsWith(" ") || tagName.endsWith(">")) tagName = tagName.substring(0, tagName.length() - 1);
            while (tagName.matches("[ \n\t\r]+.*")) tagName = tagName.substring(1);
            if (tagName.isEmpty()) throw new EmptyNodeException();
            tagName = tagName.substring(1);
            final Node node;
            boolean isContainer = true;
            if (read == '>' && tagName.endsWith("/")) {
                isContainer = false;
                tagName = tagName.substring(0, tagName.length() - 1);
            }
            buffer.setLength(0);

            if (read == ' ') {
                String name = "";
                int openQuotes = -1;
                // Read attributes from given stream.
                while ((read = stream.read()) != -1)
                    if (read == openQuotes && buffer.charAt(buffer.length() - 1) != '\\') openQuotes = -1;
                    else if (buffer.length() == 0 && (read == '"' || read == '\'')) openQuotes = read;
                    else {
                        if (openQuotes == -1) {
                            if (name.isEmpty() && ("" + (char) read).matches("[\n\t\r]")) continue;
                            if (read == '=') {
                                name = buffer.toString();
                                buffer.setLength(0);
                                continue;
                            } else if (read == ' ' || read == '>') {
                                String value = buffer.toString();
                                if (value.endsWith("/")) value = value.substring(0, value.length() - 1);
                                if (name.isEmpty()) name = value;
                                if (value.equals(name)) value = null;
                                if (!name.isEmpty()) attributes.put(name, value);
                                name = "";
                                if (read == '>') {
                                    if (buffer.length() > 0)
                                        isContainer = buffer.charAt(buffer.length() - 1) != '/';
                                    break;
                                }
                                buffer.setLength(0);
                                continue;
                            }
                        }
                        if (read != '\n') buffer.append((char) read);
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
                read(stream, read, buffer, r -> !buffer.toString().endsWith(end), r -> {
                    if (r != '/' && buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == '<') {
                        buffer.setLength(buffer.length() - 1);
                        Node n = Node.newNode(new StringBuilder("<").append((char) r), stream, false);
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
            if (checkNext && stream.available() > 0)
                try {
                    node.setNext(stream);
                } catch (EmptyNodeException ignored) {

                }

            return node;
        } catch (IOException e) {
            throw new NodeException(e);
        }
    }

    private static char read(InputStream stream, int start, StringBuilder buffer, Predicate<Character> tester, Consumer<Character> read) throws IOException {
        final StringBuilder commentBuffer = new StringBuilder(buffer.toString());
        if (start != 0) commentBuffer.append(start);
        boolean commented = false;
        int r = 0;
        while ((tester == null || tester.test((char) r)) && (r = stream.read()) != -1) {
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
            if (commentBuffer.length() > 5) commentBuffer.delete(0, commentBuffer.length() - 5);
        }
        return (char) r;
    }
}
