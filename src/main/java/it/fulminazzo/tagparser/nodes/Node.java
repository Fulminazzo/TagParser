package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.utils.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class Node {
    protected final String tagName;
    protected final Map<String, String> attributes;
    protected boolean closed;
    protected Node next;
    protected Node child;
    protected String contents;

    //TODO: Checks for tag name!
    Node(String tagName) {
        this.tagName = tagName.endsWith("/") ? tagName.substring(0, tagName.length() - 1) : tagName;
        this.attributes = new LinkedHashMap<>();
        this.closed = !tagName.endsWith("/");
    }

    public String getTagName() {
        return tagName;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes.clear();
        if (attributes != null) attributes.forEach((k, v) ->
                this.attributes.put(k, StringUtils.removeQuotes(v)));
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getNext() {
        return next;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public Node getChild() {
        return child;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    public static Node newNode(String string) {
        return string == null ? null : newNode(new ByteArrayInputStream(string.getBytes()));
    }

    public static Node newNode(InputStream stream) {
        return Node.newNode(new StringBuilder(), stream);
    }

    private static Node newNode(final StringBuilder buffer, InputStream stream) {
        try {
            final Map<String, String> attributes = new LinkedHashMap<>();

            int read = stream.read();
            if (read == -1) throw new IOException("End of stream");

            // Read tag name from given stream.
            if (read != '>') {
                if (read != '<') buffer.append((char) read);
                while ((read = stream.read()) != -1)
                    if (read == ' ' || read == '>') break;
                    else buffer.append((char) read);
            }
            final String tagName = buffer.toString();
            final Node node = new Node(tagName);
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

            // Read contents from given stream.
            final String end = "</" + tagName + ">";
            while ((read = stream.read()) != -1 && !buffer.toString().endsWith(end))
                buffer.append((char) read);

            String contents = buffer.toString();
            if (contents.endsWith(end)) {
                contents = contents.substring(0, contents.length() - end.length());
                node.setClosed(true);
            } else node.setClosed(false);

            if (contents.startsWith("<")) {
                InputStream byteStream = new ByteArrayInputStream(contents.getBytes());
                node.setChild(Node.newNode(byteStream));
                buffer.setLength(0);
                while ((read = byteStream.read()) != -1) buffer.append((char) read);
                contents = buffer.toString();
            }

            node.setContents(contents);

            if (stream.available() > 0) node.setNext(Node.newNode(stream));

            return node;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String printNode(Node node) {
        if (node == null) return null;
        final StringBuilder builder = new StringBuilder();
        builder.append("<").append(node.getTagName());

        final Map<String, String> attributes = node.getAttributes();
        if (attributes != null && !attributes.isEmpty()) {
            builder.append(" ");
            for (String key : attributes.keySet()) {
                final String value = attributes.get(key);
                builder.append(key);
                if (value != null) builder.append("=").append(value);
                builder.append(" ");
            }
            builder.setLength(builder.length() - 1);
        }

        builder.append(">");

        final Node child = node.getChild();
        if (child != null) builder.append(printNode(child));
        else {
            final String contents = node.getContents();
            if (contents != null) builder.append(contents);
        }

        if (node.isClosed())
            builder.append("</").append(node.getTagName()).append(">");

        if (node.getNext() != null)
            builder.append(Node.printNode(node.getNext()));

        return builder.toString();
    }
}
