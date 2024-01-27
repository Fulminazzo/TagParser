package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.tagparser.nodes.exceptions.*;
import it.fulminazzo.tagparser.nodes.validators.IntegerValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NodeBuilderTest {

    @Test
    void testAllowClosingTags() {
        final NodeBuilder builder = new NodeBuilder("<test/>").addTag("test", false).allowClosingTags();
        assertDoesNotThrow(builder::build);
    }

    @Test
    void testNotAllowClosingTags() {
        final NodeBuilder builder = new NodeBuilder("<test/>").addTag("test", false).disallowClosingTags();
        assertThrows(ClosingTagsNotAllowedException.class, builder::build);
    }

    @Test
    void testAllowClosedTags() {
        final NodeBuilder builder = new NodeBuilder("<test></test>").addTag("test", true).allowNotClosedTags();
        assertDoesNotThrow(builder::build);
    }

    @Test
    void testNotAllowClosedTags() {
        final NodeBuilder builder = new NodeBuilder("<test></test>").addTag("test", true).disallowNotClosedTags();
        assertThrows(NotClosedTagsNotAllowedException.class, builder::build);
    }

    @Test
    void testNotValidTagName() {
        final NodeBuilder builder = new NodeBuilder("<node/>").addTag("test", true);
        assertThrows(NotValidTagException.class, builder::build);
    }

    @Test
    void testNotClosedTagName() {
        final NodeBuilder builder = new NodeBuilder("<test/>").addTag("test", true);
        assertThrows(NodeException.class, builder::build);
    }

    @Test
    void testValidTagName() {
        final NodeBuilder builder = new NodeBuilder("<test/>").addTag("test", false);
        assertDoesNotThrow(builder::build);
    }

    @Test
    void testMissingAttribute() {
        final NodeBuilder builder = new NodeBuilder("<test/>").addRequiredAttribute("attr", new IntegerValidator());
        assertThrows(MissingRequiredAttributeException.class, builder::build);
    }

    @Test
    void testNotValidAttribute() {
        final NodeBuilder builder = new NodeBuilder("<test attr=hello/>").addRequiredAttribute("attr", new IntegerValidator());
        assertThrows(NotValidAttributeException.class, builder::build);
    }

    @Test
    void testValidAttribute() {
        final NodeBuilder builder = new NodeBuilder("<test attr=10/>").addRequiredAttribute("attr", new IntegerValidator());
        assertDoesNotThrow(builder::build);
    }

    @Test
    void testNotValidContent() {
        final NodeBuilder builder = new NodeBuilder("<test>Content~</test>").setContentsRegex("[A-Za-z0-9]+");
        assertThrows(NotValidContentException.class, builder::build);
    }

    @Test
    void testValidContent() {
        final NodeBuilder builder = new NodeBuilder("<test>Content</test>").setContentsRegex("[A-Za-z0-9]+");
        assertDoesNotThrow(builder::build);
    }

    @Test
    void testFromNotSpecified() {
        assertThrows(FromNotSpecified.class, () -> new NodeBuilder().build());
    }
}