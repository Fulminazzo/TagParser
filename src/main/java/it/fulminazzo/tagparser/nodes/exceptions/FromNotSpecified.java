package it.fulminazzo.tagparser.nodes.exceptions;

import it.fulminazzo.tagparser.nodes.NodeBuilder;

/**
 * Exception thrown when using {@link NodeBuilder#build()} without specifying a starting stream.
 */
public class FromNotSpecified extends NodeException {

    /**
     * Instantiates a new from not specified.
     */
    public FromNotSpecified() {
        super("Starting point for parser has not been specified");
    }
}
