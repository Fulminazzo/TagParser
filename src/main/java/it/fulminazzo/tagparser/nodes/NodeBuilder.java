package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.nodes.exceptions.*;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileDoesNotExistException;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileIsDirectoryException;
import it.fulminazzo.tagparser.nodes.validators.AttributeValidator;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Create a new node from the given specifications.
 */
@SuppressWarnings("UnusedReturnValue")
public class NodeBuilder {
    /**
     * If enabled, every tag will be accepted.
     * <p>
     * If disabled, only the tags specified in {@link #validTags} will be accepted (only if not empty).
     */
    @Getter
    protected boolean allowingGeneralTags;
    /**
     * If enabled, tags like &lt;tag&gt;&lt;/tag&gt; will be accepted.
     * <p>
     * If disabled, tags like &lt;tag&gt;&lt;/tag&gt; will not be accepted.
     */
    @Getter
    protected boolean allowingClosingTags;
    /**
     * If enabled, tags like &lt;tag/&gt; will be accepted.
     * <p>
     * If disabled, tags like &lt;tag/&gt; will not be accepted.
     */
    @Getter
    protected boolean allowingNotClosedTags;
    /**
     * If enabled and the stream is not ended,
     * a new NodeBuilder similar to this will be created to retrieve another node if possible.
     * <p>
     * If disabled, will not check if another node is available.
     */
    @Getter
    protected boolean checkingNext;
    /**
     * Specify a list of all the valid tags and specify true for closed tags or false for closing tags.
     */
    @Getter
    protected final @NotNull Map<String, Boolean> validTags;
    /**
     * Specify a list of all the required attributes with each {@link AttributeValidator}.
     * Use null for no validation.
     */
    protected final @NotNull Map<String, AttributeValidator> requiredAttributes;
    /**
     * A regular expression to verify the validity of the contents.
     */
    protected @Nullable String contentsRegex;
    /**
     * The regex used when creating nodes.
     */
    @Setter
    protected @NotNull String tagNameRegex = Node.TAG_NAME_REGEX;

    protected @Nullable StringBuilder buffer;
    protected InputStream stream;

    /**
     * Instantiates a new Node builder.
     */
    public NodeBuilder() {
        this(new StringBuilder());
    }

    /**
     * Instantiates a new Node builder.
     *
     * @param buffer the buffer
     */
    public NodeBuilder(@Nullable StringBuilder buffer) {
        this.buffer = buffer;
        this.validTags = new HashMap<>();
        this.requiredAttributes = new HashMap<>();
        allowClosingTags().allowNotClosedTags().checkNext();
    }

    /**
     * Instantiates a new Node builder.
     *
     * @param string the string
     */
    public NodeBuilder(@NotNull String string) {
        this();
        from(string);
    }

    /**
     * Instantiates a new Node builder.
     *
     * @param file the file
     */
    public NodeBuilder(@NotNull File file) {
        this();
        from(file);
    }

    /**
     * Instantiates a new Node builder.
     *
     * @param stream the stream
     */
    public NodeBuilder(@NotNull InputStream stream) {
        this();
        from(stream);
    }

    /**
     * Instantiates a new Node builder.
     *
     * @param builder the builder
     */
    public NodeBuilder(@Nullable NodeBuilder builder) {
        this();
        if (builder == null) return;

        Class<?> clazz = this.getClass();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields())
                if (!field.getName().equals("this$0") && !Modifier.isStatic(field.getModifiers())) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(builder);
                        field.set(this, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * Sets buffer.
     *
     * @param buffer the buffer
     * @return the buffer
     */
    public @NotNull NodeBuilder setBuffer(@Nullable StringBuilder buffer) {
        this.buffer = buffer;
        return this;
    }

    /**
     * Allow general tags.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder allowGeneralTags() {
        this.allowingGeneralTags = true;
        return this;
    }

    /**
     * Disallow general tags.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder disallowGeneralTags() {
        this.allowingGeneralTags = false;
        return this;
    }

    /**
     * Allow closing tags.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder allowClosingTags() {
        this.allowingClosingTags = true;
        return this;
    }

    /**
     * Disallow closing tags.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder disallowClosingTags() {
        this.allowingClosingTags = false;
        return this;
    }

    /**
     * Allow not closed tags.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder allowNotClosedTags() {
        this.allowingNotClosedTags = true;
        return this;
    }

    /**
     * Disallow not closed tags.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder disallowNotClosedTags() {
        this.allowingNotClosedTags = false;
        return this;
    }

    /**
     * Allow checking for the next node.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder checkNext() {
        this.checkingNext = true;
        return this;
    }

    /**
     * Disallow checking for the next node.
     *
     * @return this builder
     */
    public @NotNull NodeBuilder uncheckNext() {
        this.checkingNext = false;
        return this;
    }

    /**
     * Add the given tag name as a valid tag.
     *
     * @param tagName            the tag name
     * @param requiresClosingTag toggle requires closing tag
     * @return this builder
     */
    public @NotNull NodeBuilder addTag(@NotNull String tagName, boolean requiresClosingTag) {
        this.validTags.put(tagName, requiresClosingTag);
        return this;
    }

    /**
     * Add the required attribute with its associated validator.
     * Specify null for no validator.
     *
     * @param attribute the attribute
     * @param validator the validator
     * @return this builder
     */
    public @NotNull NodeBuilder addRequiredAttribute(@NotNull String attribute, @Nullable AttributeValidator validator) {
        this.requiredAttributes.put(attribute, validator);
        return this;
    }

    /**
     * Sets contents regex.
     *
     * @param regex the regex
     * @return the contents regex
     */
    public @NotNull NodeBuilder setContentsRegex(@Nullable String regex) {
        this.contentsRegex = regex;
        return this;
    }

    /**
     * Validate tag boolean.
     *
     * @param tagName the tag name
     * @return null if no valid tag is specified, else true if it requires closing tag.
     */
    public @Nullable Boolean validateTag(@NotNull String tagName) {
        if (this.validTags.isEmpty()) return null;
        Boolean closable = this.validTags.get(tagName);
        if (closable == null && !isAllowingGeneralTags()) throw new NotValidTagException(tagName);
        else return closable;
    }

    /**
     * Validate attributes.
     *
     * @param attributes the attributes
     */
    public void validateAttributes(@NotNull Map<String, String> attributes) {
        for (String key : this.requiredAttributes.keySet()) {
            String option = attributes.get(key);
            if (option == null) throw new MissingRequiredAttributeException(key, attributes);
            else {
                AttributeValidator validator = this.requiredAttributes.get(key);
                if (validator != null) validator.validate(key, option);
            }
        }
    }

    /**
     * Validate contents.
     *
     * @param contents the contents
     */
    public void validateContents(@NotNull String contents) {
        if (this.contentsRegex != null && !contents.matches(this.contentsRegex))
            throw new NotValidContentException(contents, this.contentsRegex);
    }

    /**
     * Set the starting point for building the node.
     *
     * @param string the string
     * @return this builder
     */
    public @NotNull NodeBuilder from(@Nullable String string) {
        if (string != null) from(new ByteArrayInputStream(string.getBytes()));
        return this;
    }

    /**
     * Set the starting point for building the node.
     *
     * @param file the file
     * @return this builder
     */
    public @NotNull NodeBuilder from(@NotNull File file) {
        try {
            if (file.isDirectory()) throw new FileIsDirectoryException(file);
            return from(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new FileDoesNotExistException(file);
        }
    }

    /**
     * Set the starting point for building the node.
     *
     * @param stream the stream
     * @return this builder
     */
    public @NotNull NodeBuilder from(@NotNull InputStream stream) {
        this.stream = stream;
        return this;
    }

    /**
     * Build node.
     *
     * @return the node
     */
    public @NotNull Node build() {
        try {
            if (stream == null) throw new FromNotSpecified();
            if (buffer == null) buffer = new StringBuilder();
            final Node node = createNode();
            final String tagName = node.getTagName();

            if (node instanceof ContainerNode) {
                ContainerNode containerNode = (ContainerNode) node;
                // Read contents from given stream.
                final String end = "</" + tagName + ">";
                read(0, r -> !buffer.toString().endsWith(end), (b, r) -> {
                    if (r != '/' && r != '!' &&
                            buffer.length() > 0 && buffer.charAt(buffer.length() - 1) == '<' &&
                            (buffer.length() < 2 || buffer.charAt(buffer.length() - 2) != '[')) {
                        buffer.setLength(buffer.length() - 1);
                        Node n = cloneBuilder()
                                .setBuffer(new StringBuilder("<").append((char) r))
                                .uncheckNext()
                                .build();
                        containerNode.addChild(n);
                    } else buffer.append((char) r);
                });

                String text = buffer.toString();
                if (text.endsWith(end))
                    text = text.substring(0, text.length() - end.length());
                else throw new NodeException(String.format("Node \"%s\" not closed. Raw text: \"%s\"", tagName, text));

                if (!text.trim().isEmpty()) {
                    validateContents(text);
                    containerNode.setText(text);
                }
            }

            buffer.setLength(0);

            // Check for other content to be added.
            if (isCheckingNext() && stream.available() > 0)
                try {
                    node.setNext(cloneBuilder().build());
                } catch (EmptyNodeException ignored) {

                }

            return node;
        } catch (IOException e) {
            throw new NodeException(e);
        }
    }

    /**
     * Create node from stream.
     *
     * @return the node
     */
    protected @NotNull Node createNode() {
        try {
            if (stream == null) throw new FromNotSpecified();
            if (buffer == null) buffer = new StringBuilder();
            final Map<String, String> attributes = new LinkedHashMap<>();

            // Read tag name from given stream.
            int read = read(0, r -> buffer.indexOf("<") == -1 || (r != ' ' && r != '>'), (b, r) -> {
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
                                if (value.endsWith("/") || value.endsWith("?")) value = value.substring(0, value.length() - 1);
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

            Boolean validateTag = validateTag(tagName);
            if (validateTag != null) isContainer = validateTag;

            if (isContainer && !isAllowingNotClosedTags())
                throw new NotClosedTagsNotAllowedException(tagName);
            else if (!isContainer && !isAllowingClosingTags())
                throw new ClosingTagsNotAllowedException(tagName);

            if (!isContainer) node = new Node(tagName, tagNameRegex);
            else node = new ContainerNode(tagName, tagNameRegex);

            validateAttributes(attributes);
            node.setAttributes(attributes);

            return node;
        } catch (IOException e) {
            throw new NodeException(e);
        }
    }

    /**
     * Read from the stream until a certain condition is met or the end of the stream is reached.
     *
     * @param start  the starting char
     * @param tester the tester applied for every while loop
     * @param read   the function to execute when reading
     * @return the last read char
     * @throws IOException the io exception
     */
    protected char read(int start, @Nullable Predicate<Character> tester, @NotNull BiConsumer<StringBuilder, Character> read) throws IOException {
        if (stream == null) throw new FromNotSpecified();
        if (buffer == null) buffer = new StringBuilder();
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
                } else read.accept(commentBuffer, (char) r);
            }
            if (commentBuffer.length() > 5) commentBuffer.delete(0, commentBuffer.length() - 5);
        }
        return (char) r;
    }

    /**
     * Clone this builder.
     *
     * @return the node builder
     */
    public @NotNull NodeBuilder cloneBuilder() {
        return new NodeBuilder(this);
    }
}