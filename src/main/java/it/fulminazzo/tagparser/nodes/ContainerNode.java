package it.fulminazzo.tagparser.nodes;

import lombok.Getter;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A node able to contain simple text or other nodes.
 * This type of node requires an opening and closing tag.
 * <p>
 * Example: &#60;p&#62;A paragraph would be wrapped in a container node!&#60;/p&#62;
 */
@Getter
public class ContainerNode extends Node {
    protected Node child;
    protected String text;

    /**
     * Instantiates a new Container node.
     *
     * @param tagName the tag name
     */
    public ContainerNode(String tagName) {
        super(tagName);
    }

    @Override
    public ContainerNode setAttribute(String name, String value) {
        return (ContainerNode) super.setAttribute(name, value);
    }

    @Override
    public ContainerNode setAttributes(String... attributes) {
        return (ContainerNode) super.setAttributes(attributes);
    }

    @Override
    public ContainerNode setAttributes(Map<String, String> attributes) {
        return (ContainerNode) super.setAttributes(attributes);
    }

    @Override
    public ContainerNode addNext(String string) {
        return (ContainerNode) super.addNext(string);
    }

    @Override
    public ContainerNode addNext(File file) {
        return (ContainerNode) super.addNext(file);
    }

    @Override
    public ContainerNode addNext(InputStream stream) {
        return (ContainerNode) super.addNext(stream);
    }

    @Override
    public ContainerNode addNext(Node next) {
        return (ContainerNode) super.addNext(next);
    }

    @Override
    public ContainerNode removeNext(Node next) {
        return (ContainerNode) super.removeNext(next);
    }

    @Override
    public ContainerNode removeNext(Predicate<Node> predicate) {
        return (ContainerNode) super.removeNext(predicate);
    }

    @Override
    public ContainerNode setNext(String string) {
        return (ContainerNode) super.setNext(string);
    }

    @Override
    public ContainerNode setNext(File file) {
        return (ContainerNode) super.setNext(file);
    }

    @Override
    public ContainerNode setNext(InputStream stream) {
        return (ContainerNode) super.setNext(stream);
    }

    @Override
    public ContainerNode setNext(Node next) {
        return (ContainerNode) super.setNext(next);
    }

    /**
     * Sets text.
     *
     * @param text the text
     * @return the text
     */
    public ContainerNode setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Add a child node.
     *
     * @param string the string
     * @return this node
     */
    public ContainerNode addChild(String string) {
        return addChild(Node.newNode(string));
    }

    /**
     * Add a child node.
     *
     * @param file the file
     * @return this node
     */
    public ContainerNode addChild(File file) {
        return addChild(Node.newNode(file));
    }

    /**
     * Add a child node.
     *
     * @param stream the stream
     * @return this node
     */
    public ContainerNode addChild(InputStream stream) {
        return addChild(Node.newNode(stream));
    }

    /**
     * Add a child node.
     *
     * @param child the child
     * @return this node
     */
    public ContainerNode addChild(Node child) {
        if (this.child != null) this.child.addNext(child);
        else this.child = child;
        return this;
    }

    /**
     * Remove a child node.
     *
     * @param child the child
     * @return this node
     */
    public ContainerNode removeChild(Node child) {
        return removeChild(n -> n.equals(child));
    }

    /**
     * Remove a child node.
     *
     * @param predicate the predicate used to verify if the node should be removed or not
     * @return this node
     */
    public ContainerNode removeChild(Predicate<Node> predicate) {
        if (this.child == null) return this;
        if (predicate.test(this.child)) {
            this.child.removeNext(predicate);
            this.child = this.child.next;
        }
        return this;
    }

    /**
     * Sets child.
     *
     * @param string the string
     * @return the child
     */
    public ContainerNode setChild(String string) {
        return setChild(Node.newNode(string));
    }

    /**
     * Sets child.
     *
     * @param file the file
     * @return the child
     */
    public ContainerNode setChild(File file) {
        return setChild(Node.newNode(file));
    }

    /**
     * Sets child.
     *
     * @param stream the stream
     * @return the child
     */
    public ContainerNode setChild(InputStream stream) {
        return setChild(Node.newNode(stream));
    }

    /**
     * Sets child.
     *
     * @param child the child
     * @return the child
     */
    public ContainerNode setChild(Node child) {
        this.child = child;
        return this;
    }

    /**
     * Gets the current number of children.
     *
     * @return the children
     */
    public int countChildren() {
        if (this.child != null) return 1 + this.child.countNextNodes();
        else return 0;
    }

    /**
     * Gets all the children nodes in a list.
     *
     * @return the children
     */
    public List<Node> getChildren() {
        final List<Node> children = new LinkedList<>();
        Node c = child;
        while (c != null) {
            children.add(c);
            c = c.getNext();
        }
        return children;
    }

    @Override
    public String toHTML() {
        final StringBuilder builder = new StringBuilder(super.toHTML());
        builder.setLength(builder.length() - 2);
        builder.append(">");
        if (text != null) builder.append(text);
        Node child = this.child;
        while (child != null) {
            builder.append(child.toHTML());
            child = child.getNext();
        }
        builder.append("</").append(tagName).append(">");
        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(this.getClass().getSimpleName() + " {");

        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields())
                if (!Modifier.isStatic(field.getModifiers())) {
                    builder.append("\n    ");
                    if (field.getName().equals("child"))
                        builder.append("children: ").append(countChildren());
                    else builder.append(printField(field));
                }
            clazz = clazz.getSuperclass();
        }
        return builder.append("\n}").toString();
    }
}
