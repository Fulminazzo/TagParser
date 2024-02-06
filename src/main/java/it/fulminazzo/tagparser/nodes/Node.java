package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.Attributable;
import it.fulminazzo.tagparser.nodes.exceptions.NotValidTagNameException;
import it.fulminazzo.tagparser.serializables.Serializable;
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
 * The most basic type of node.
 * It only supports attributes and does not require closing tags.
 * <p>
 * Example: &#60;img src="test.png" alt="This will be wrapped in a simple Node" /&#62;
 * <p>
 * NOTE: the ending /&#62; is REQUIRED.
 */
@Getter
public class Node implements Attributable<Node>, Serializable {
    public static final String TAG_NAME_REGEX = "[A-Za-z]([A-Za-z0-9_\\-:.]*[A-Za-z0-9])?";
    protected final @NotNull String tagName;
    protected final @NotNull Map<String, String> attributes;
    protected @Nullable Node next;

    /**
     * Instantiates a new Node.
     *
     * @param tagName the tag name
     */
    public Node(@NotNull String tagName) {
        this(tagName, TAG_NAME_REGEX);
    }

    /**
     * Instantiates a new Node.
     *
     * @param tagName the tag name
     */
    public Node(@NotNull String tagName, @NotNull String tagRegex) {
        if (!tagName.matches(tagRegex))
            throw new NotValidTagNameException(tagName);
        this.tagName = tagName;
        this.attributes = new LinkedHashMap<>();
    }

    /**
     * Recursively get all the nodes with the specified tag name. Uses {@link #getNodes(Predicate)}.
     *
     * @param tagName the tag name
     * @return the nodes
     */
    public @NotNull Set<Node> getNodes(@NotNull final String tagName) {
        return getNodes(n -> n.getTagName().equals(tagName));
    }

    /**
     * Recursively get all the nodes that pass a test from the given {@link Predicate} function.
     *
     * @param validator the validator
     * @return the nodes
     */
    public @NotNull Set<Node> getNodes(@NotNull final Predicate<? super Node> validator) {
        final Set<Node> set = new LinkedHashSet<>();
        if (validator.test(this)) set.add(this);
        if (this.next != null) {
            if (validator.test(this.next)) set.add(this.next);
            set.addAll(this.next.getNodes(validator));
        }
        return set;
    }

    /**
     * Recursively get a node from its tag name. Uses {@link #getNode(Predicate)}.
     *
     * @param tagName the tag name
     * @return the node
     */
    public @Nullable Node getNode(@NotNull final String tagName) {
        return getNode(n -> n.getTagName().equals(tagName));
    }

    /**
     * Recursively get a node using a {@link Predicate} function to validate the node.
     *
     * @param validator the validator
     * @return the node
     */
    public @Nullable Node getNode(@NotNull final Predicate<? super Node> validator) {
        if (validator.test(this)) return this;
        if (this.next != null)
            if (validator.test(this.next)) return this.next;
            else return this.next.getNode(validator);
        return null;
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
    public @NotNull Node addNext(@NotNull String string) {
        return addNext(Node.newNode(string));
    }

    /**
     * Add the next node.
     *
     * @param file the file
     * @return the node
     */
    public @NotNull Node addNext(@NotNull File file) {
        return addNext(Node.newNode(file));
    }

    /**
     * Add the next node.
     *
     * @param stream the stream
     * @return the node
     */
    public @NotNull Node addNext(@NotNull InputStream stream) {
        return addNext(Node.newNode(stream));
    }

    /**
     * Add the next node.
     *
     * @param next the next
     * @return the node
     */
    public @NotNull Node addNext(@Nullable Node next) {
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
    public @NotNull Node removeNext(@NotNull Node next) {
        return removeNext(n -> n.equals(next));
    }

    /**
     * Remove the next node.
     *
     * @param predicate the predicate used to verify if the node should be removed or not
     * @return the node
     */
    public @NotNull Node removeNext(@NotNull Predicate<Node> predicate) {
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
    public @NotNull Node setNext(@NotNull String string) {
        return setNext(Node.newNode(string));
    }

    /**
     * Sets the next node.
     *
     * @param file the file
     * @return the next
     */
    public @NotNull Node setNext(@NotNull File file) {
        return setNext(Node.newNode(file));
    }

    /**
     * Sets the next node.
     *
     * @param stream the stream
     * @return the next
     */
    public @NotNull Node setNext(@NotNull InputStream stream) {
        return setNext(Node.newNode(stream));
    }

    /**
     * Sets the next node.
     *
     * @param next the next
     * @return the next
     */
    public @NotNull Node setNext(@Nullable Node next) {
        this.next = next;
        return this;
    }

    /**
     * Converts the current node in a HTML format.
     *
     * @return the string
     */
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
        return builder.append("/>").toString();
    }

    /**
     * Compares the current node with another one.
     *
     * @param node the node
     * @return the boolean
     */
    public boolean equals(@Nullable Node node) {
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
    protected @NotNull String printField(@NotNull Field field) {
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
    public @NotNull String toString() {
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
    public static @NotNull Node newNode(@NotNull String string) {
        return new NodeBuilder(string).build();
    }

    /**
     * Creates a new node from the given file.
     *
     * @param file the file
     * @return the node
     */
    public static @NotNull Node newNode(@NotNull File file) {
        return new NodeBuilder(file).build();
    }

    /**
     * Creates a new node from the raw stream.
     *
     * @param stream the stream
     * @return the node
     */
    public static @NotNull Node newNode(@NotNull InputStream stream) {
        return new NodeBuilder(stream).build();
    }
}
