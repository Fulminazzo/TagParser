package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.utils.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

/**
 * A node able to contain simple text or other nodes.
 * This type of node requires an opening and closing tag.
 * <p>
 * Example: &#60;p&#62;A paragraph would be wrapped in a container node!&#60;/p&#62;
 */
@SuppressWarnings("UnusedReturnValue")
@Getter
public class ContainerNode extends Node {
    protected @Nullable Node child;
    protected @Nullable String text;

    /**
     * Instantiates a new Container node.
     *
     * @param tagName the tag name
     */
    public ContainerNode(@NotNull String tagName) {
        super(tagName);
    }

    @Override
    public @NotNull Set<Node> getNodes(@NotNull Predicate<? super Node> validator) {
        final Set<Node> set = super.getNodes(validator);
        if (this.child != null) {
            if (validator.test(this.child)) set.add(this.child);
            set.addAll(this.child.getNodes(validator));
        }
        return set;
    }

    @Override
    public @Nullable Node getNode(@NotNull Predicate<? super Node> validator) {
        Node result = super.getNode(validator);
        if (result != null) return result;
        if (this.child != null)
            if (validator.test(this.child)) return this.child;
            else return child.getNode(validator);
        return null;
    }

    @Override
    public ContainerNode setAttribute(@NotNull String name, @Nullable String value) {
        return (ContainerNode) super.setAttribute(name, value);
    }

    @Override
    public ContainerNode setAttributes(String @Nullable ... attributes) {
        return (ContainerNode) super.setAttributes(attributes);
    }

    @Override
    public ContainerNode setAttributes(@Nullable Map<String, String> attributes) {
        return (ContainerNode) super.setAttributes(attributes);
    }

    @Override
    public @NotNull ContainerNode addNext(@NotNull String string) {
        return (ContainerNode) super.addNext(string);
    }

    @Override
    public @NotNull ContainerNode addNext(@NotNull File file) {
        return (ContainerNode) super.addNext(file);
    }

    @Override
    public @NotNull ContainerNode addNext(@NotNull InputStream stream) {
        return (ContainerNode) super.addNext(stream);
    }

    @Override
    public @NotNull ContainerNode addNext(@Nullable Node next) {
        return (ContainerNode) super.addNext(next);
    }

    @Override
    public @NotNull ContainerNode removeNext(@NotNull Node next) {
        return (ContainerNode) super.removeNext(next);
    }

    @Override
    public @NotNull ContainerNode removeNext(@NotNull Predicate<Node> predicate) {
        return (ContainerNode) super.removeNext(predicate);
    }

    @Override
    public @NotNull ContainerNode setNext(@NotNull String string) {
        return (ContainerNode) super.setNext(string);
    }

    @Override
    public @NotNull ContainerNode setNext(@NotNull File file) {
        return (ContainerNode) super.setNext(file);
    }

    @Override
    public @NotNull ContainerNode setNext(@NotNull InputStream stream) {
        return (ContainerNode) super.setNext(stream);
    }

    @Override
    public @NotNull ContainerNode setNext(@Nullable Node next) {
        return (ContainerNode) super.setNext(next);
    }

    /**
     * Sets text.
     *
     * @param text the text
     * @return the text
     */
    public @NotNull ContainerNode setText(@Nullable String text) {
        this.text = StringUtils.parseContent(text);
        return this;
    }

    /**
     * Add a child node.
     *
     * @param string the string
     * @return this node
     */
    public @NotNull ContainerNode addChild(@NotNull String string) {
        return addChild(Node.newNode(string));
    }

    /**
     * Add a child node.
     *
     * @param file the file
     * @return this node
     */
    public @NotNull ContainerNode addChild(@NotNull File file) {
        return addChild(Node.newNode(file));
    }

    /**
     * Add a child node.
     *
     * @param stream the stream
     * @return this node
     */
    public @NotNull ContainerNode addChild(@NotNull InputStream stream) {
        return addChild(Node.newNode(stream));
    }

    /**
     * Add a child node.
     *
     * @param child the child
     * @return this node
     */
    public @NotNull ContainerNode addChild(@NotNull Node child) {
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
    public @NotNull ContainerNode removeChild(@NotNull Node child) {
        return removeChild(n -> n.equals(child));
    }

    /**
     * Remove a child node.
     *
     * @param predicate the predicate used to verify if the node should be removed or not
     * @return this node
     */
    public @NotNull ContainerNode removeChild(@NotNull Predicate<Node> predicate) {
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
    public @NotNull ContainerNode setChild(@NotNull String string) {
        return setChild(Node.newNode(string));
    }

    /**
     * Sets child.
     *
     * @param file the file
     * @return the child
     */
    public @NotNull ContainerNode setChild(@NotNull File file) {
        return setChild(Node.newNode(file));
    }

    /**
     * Sets child.
     *
     * @param stream the stream
     * @return the child
     */
    public @NotNull ContainerNode setChild(@NotNull InputStream stream) {
        return setChild(Node.newNode(stream));
    }

    /**
     * Sets child.
     *
     * @param child the child
     * @return the child
     */
    public @NotNull ContainerNode setChild(@Nullable Node child) {
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
    public @NotNull List<Node> getChildren() {
        final List<Node> children = new LinkedList<>();
        Node c = child;
        while (c != null) {
            children.add(c);
            c = c.getNext();
        }
        return children;
    }

    @Override
    public @NotNull String toHTML() {
        final StringBuilder builder = new StringBuilder(super.toHTML());
        builder.setLength(builder.length() - 2);
        builder.append(">");
        if (text != null) builder.append(StringUtils.unParseContent(text));
        Node child = this.child;
        while (child != null) {
            builder.append(child.toHTML());
            child = child.getNext();
        }
        builder.append("</").append(tagName).append(">");
        return builder.toString();
    }

    /**
     * Compares the current node with another one.
     *
     * @param node the node
     * @return the boolean
     */
    public boolean equals(@Nullable ContainerNode node) {
        if (node == null) return false;
        if (!Objects.equals(this.child, node.getChild())) return false;
        return Objects.equals(this.text, node.getText());
    }

    @Override
    public boolean equals(@Nullable Node node) {
        if (node instanceof ContainerNode) return equals((ContainerNode) node);
        return super.equals(node);
    }

    @Override
    public @NotNull String toString() {
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
