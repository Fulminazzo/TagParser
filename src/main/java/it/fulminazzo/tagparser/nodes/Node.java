package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.nodes.exceptions.EndOfStreamException;
import it.fulminazzo.tagparser.nodes.exceptions.NodeException;
import it.fulminazzo.tagparser.nodes.exceptions.NotValidTagNameException;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileDoesNotExistException;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileIsDirectoryException;
import it.fulminazzo.tagparser.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

@Getter
@Setter
public class Node {
    static final String TAG_NAME_REGEX = "[A-Za-z][A-Za-z0-9_-]*[A-Za-z0-9]";
    protected final String tagName;
    protected final Map<String, String> attributes;
    protected Node next;

    Node(String tagName) {
        if (!tagName.matches(TAG_NAME_REGEX))
            throw new NotValidTagNameException(tagName);
        this.tagName = tagName;
        this.attributes = new LinkedHashMap<>();
    }

    public void setAttribute(String key, String value) {
        this.attributes.put(key, StringUtils.removeQuotes(value));
    }

    public String getAttribute(String key) {
        return this.attributes.get(key);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes.clear();
        if (attributes != null) attributes.forEach(this::setAttribute);
    }

    public int countNextNodes() {
        if (this.next != null) return 1 + this.next.countNextNodes();
        else return 0;
    }

    public void addNext(String string) {
        addNext(Node.newNode(string));
    }

    public void addNext(File file) {
        addNext(Node.newNode(file));
    }

    public void addNext(InputStream stream) {
        addNext(Node.newNode(stream));
    }

    public void addNext(Node next) {
        if (this.next != null) this.next.addNext(next);
        else this.next = next;
    }

    public void removeNext(Node next) {
        removeNext(n -> n.equals(next));
    }

    public void removeNext(Predicate<Node> predicate) {
        if (this.next == null) return;
        if (predicate.test(this.next)) {
            this.next.removeNext(predicate);
            this.next = this.next.next;
        }
    }

    public void setNext(String string) {
        setNext(Node.newNode(string));
    }

    public void setNext(File file) {
        setNext(Node.newNode(file));
    }

    public void setNext(InputStream stream) {
        setNext(Node.newNode(stream));
    }

    public void setNext(Node next) {
        this.next = next;
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
        try {
            final StringBuilder buffer = new StringBuilder();
            final Map<String, String> attributes = new LinkedHashMap<>();

            int read = stream.read();
            if (read == -1) throw new EndOfStreamException();

            // Read tag name from given stream.
            if (read != '>') {
                if (read != '<') buffer.append((char) read);
                while ((read = stream.read()) != -1)
                    if (read == ' ' || read == '>') break;
                    else buffer.append((char) read);
            }
            final String tagName = buffer.toString();
            final Node node;
            if (tagName.endsWith("/"))
                node = new Node(tagName.substring(0, tagName.length() - 1));
//            else node = new ContainerNode(tagName);
            else node = null;
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
                            if (key.isEmpty()) key = value;
                            if (value.equals(key)) value = null;
                            if (!key.isEmpty()) attributes.put(key, value);
                            buffer.setLength(0);
                            key = "";
                            if (read == '>') break;
                        } else buffer.append((char) read);
                    }
                node.setAttributes(attributes);
                buffer.setLength(0);
            }

//            if (node instanceof ContainerNode) {
//                // Read contents from given stream.
//                //TODO: TOTALLY REWORK!!!
//                final String end = "</" + tagName + ">";
//                while ((read = stream.read()) != -1 && !buffer.toString().endsWith(end))
//                    buffer.append((char) read);
//
//                String contents = buffer.toString();
//                if (contents.endsWith(end)) {
//                    contents = contents.substring(0, contents.length() - end.length());
//                    node.setClosed(true);
//                } else node.setClosed(false);
//
//                if (contents.startsWith("<")) {
//                    InputStream byteStream = new ByteArrayInputStream(contents.getBytes());
//                    node.setChild(Node.newNode(byteStream));
//                    buffer.setLength(0);
//                    while ((read = byteStream.read()) != -1) buffer.append((char) read);
//                    contents = buffer.toString();
//                }
//
//                node.setContents(contents);
//            }

            if (stream.available() > 0) node.setNext(stream);

            return node;
        } catch (IOException e) {
            throw new NodeException(e);
        }
    }
}
