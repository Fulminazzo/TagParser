package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.nodes.exceptions.*;
import it.fulminazzo.tagparser.nodes.validators.IntegerValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeRulesTest {

    @Test
    void testAllowClosingTags() {
        final NodeRules rules = new NodeRules().addTag("test", false).allowClosingTags();
        assertDoesNotThrow(() -> Node.newNode("<test/>", rules));
    }

    @Test
    void testNotAllowClosingTags() {
        final NodeRules rules = new NodeRules().addTag("test", false).disallowClosingTags();
        assertThrows(ClosingTagsNotAllowedException.class, () -> Node.newNode("<test/>", rules));
    }

    @Test
    void testAllowClosedTags() {
        final NodeRules rules = new NodeRules().addTag("test", true).allowNotClosedTags();
        assertDoesNotThrow(() -> Node.newNode("<test></test>", rules));
    }

    @Test
    void testNotAllowClosedTags() {
        final NodeRules rules = new NodeRules().addTag("test", true).disallowNotClosedTags();
        assertThrows(NotClosedTagsNotAllowedException.class, () -> Node.newNode("<test></test>", rules));
    }

    @Test
    void testNotValidTagName() {
        final NodeRules rules = new NodeRules().addTag("test", true);
        assertThrows(NotValidTagException.class, () -> Node.newNode("<node/>", rules));
    }

    @Test
    void testNotClosedTagName() {
        final NodeRules rules = new NodeRules().addTag("test", true);
        assertThrows(NodeException.class, () -> Node.newNode("<test/>", rules));
    }

    @Test
    void testValidTagName() {
        final NodeRules rules = new NodeRules().addTag("test", false);
        assertDoesNotThrow(() -> Node.newNode("<test/>", rules));
    }

    @Test
    void testMissingAttribute() {
        final NodeRules rules = new NodeRules().addRequiredAttribute("attr", new IntegerValidator());
        assertThrows(MissingRequiredAttributeException.class, () -> Node.newNode("<test/>", rules));
    }

    @Test
    void testNotValidAttribute() {
        final NodeRules rules = new NodeRules().addRequiredAttribute("attr", new IntegerValidator());
        assertThrows(NotValidAttributeException.class, () -> Node.newNode("<test attr=hello/>", rules));
    }

    @Test
    void testValidAttribute() {
        final NodeRules rules = new NodeRules().addRequiredAttribute("attr", new IntegerValidator());
        assertDoesNotThrow(() -> Node.newNode("<test attr=10/>", rules));
    }

    @Test
    void testNotValidContent() {
        final NodeRules rules = new NodeRules().setContentsRegex("[A-Za-z0-9]+");
        assertThrows(NotValidContentException.class, () -> Node.newNode("<test>Content~</test>", rules));
    }

    @Test
    void testValidContent() {
        final NodeRules rules = new NodeRules().setContentsRegex("[A-Za-z0-9]+");
        assertDoesNotThrow(() -> Node.newNode("<test>Content</test>", rules));
    }
}