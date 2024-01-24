package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.nodes.exceptions.MissingRequiredAttributeException;
import it.fulminazzo.tagparser.nodes.exceptions.NotValidContentException;
import it.fulminazzo.tagparser.nodes.exceptions.NotValidTagException;
import it.fulminazzo.tagparser.nodes.validators.AttributeValidator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Use this class to specify the options to pass to {@link Node#newNode(StringBuilder, InputStream, boolean, NodeRules)} and similar.
 */
public class NodeRules {
    @Getter
    private boolean allowingClosingTags;
    @Getter
    private boolean allowingNotClosedTags;
    private final @NotNull Map<String, Boolean> validTags;
    private final @NotNull Map<String, AttributeValidator> requiredAttributes;
    private String contentsRegex;

    /**
     * Instantiates a new Node rules.
     */
    public NodeRules() {
        this.validTags = new HashMap<>();
        this.requiredAttributes = new HashMap<>();
        allowClosingTags().allowNotClosedTags();
    }

    /**
     * Allow closing tags.
     *
     * @return this node rules
     */
    public @NotNull NodeRules allowClosingTags() {
        allowingClosingTags = true;
        return this;
    }

    /**
     * Disallow closing tags.
     *
     * @return this node rules
     */
    public @NotNull NodeRules disallowClosingTags() {
        allowingClosingTags = false;
        return this;
    }

    /**
     * Allow not closed tags.
     *
     * @return this node rules
     */
    public @NotNull NodeRules allowNotClosedTags() {
        allowingNotClosedTags = true;
        return this;
    }

    /**
     * Disallow not closed tags.
     *
     * @return this node rules
     */
    public @NotNull NodeRules disallowNotClosedTags() {
        allowingNotClosedTags = false;
        return this;
    }

    /**
     * Add the given tag name as a valid tag.
     *
     * @param tagName            the tag name
     * @param requiresClosingTag toggle requires closing tag
     * @return this node rules
     */
    public @NotNull NodeRules addTag(@NotNull String tagName, boolean requiresClosingTag) {
        this.validTags.put(tagName, requiresClosingTag);
        return this;
    }

    /**
     * Add the required attribute with its associated validator.
     * Specify null for no validator.
     *
     * @param attribute the attribute
     * @param validator the validator
     * @return this node rules
     */
    public @NotNull NodeRules addRequiredAttribute(@NotNull String attribute, @Nullable AttributeValidator validator) {
        this.requiredAttributes.put(attribute, validator);
        return this;
    }

    /**
     * Sets contents regex.
     *
     * @param regex the regex
     * @return the contents regex
     */
    public @NotNull NodeRules setContentsRegex(String regex) {
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
        if (validTags.isEmpty()) return null;
        Boolean closable = validTags.get(tagName);
        if (closable == null) throw new NotValidTagException(tagName);
        else return closable;
    }

    /**
     * Validate attributes.
     *
     * @param attributes the attributes
     */
    public void validateAttributes(@NotNull Map<String, String> attributes) {
        for (String key : requiredAttributes.keySet()) {
            String option = attributes.get(key);
            if (option == null) throw new MissingRequiredAttributeException(key, attributes);
            else {
                AttributeValidator validator = requiredAttributes.get(key);
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
        if (contentsRegex != null && !contents.matches(contentsRegex))
            throw new NotValidContentException(contents, contentsRegex);
    }
}
