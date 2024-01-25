package it.fulminazzo.tagparser.nodes.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown when {@link it.fulminazzo.tagparser.nodes.NodeBuilder#validateTag(String)} fails.
 */
public class NotValidTagException extends NodeException {

    /**
     * Instantiates a new Not valid tag exception.
     *
     * @param tagName the tag name
     */
    public NotValidTagException(@NotNull String tagName) {
        super(String.format("Tag \"%s\" was not recognized by the node rules. Are you sure it is the right tag?", tagName));
    }
}
