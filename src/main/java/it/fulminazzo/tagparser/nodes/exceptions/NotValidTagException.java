package it.fulminazzo.tagparser.nodes.exceptions;

/**
 * An exception thrown when {@link it.fulminazzo.tagparser.nodes.NodeRules#validateTag(String)} fails.
 */
public class NotValidTagException extends NodeException {

    /**
     * Instantiates a new Not valid tag exception.
     *
     * @param tagName the tag name
     */
    public NotValidTagException(String tagName) {
        super(String.format("Tag \"%s\" was not recognized by the node rules. Are you sure it is the right tag?", tagName));
    }
}
