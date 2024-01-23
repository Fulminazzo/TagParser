package it.fulminazzo.tagparser.nodes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NodeTest {

    static Object[][] getNodeTests() {
        return new Object[][]{
                new Object[]{"body", new LinkedHashMap<>(), "", true, "<body></body>"},
                new Object[]{"body", new LinkedHashMap<>(), "", false, "<body>"},
                new Object[]{"body", new LinkedHashMap<>(), "", false, "<body/>"},
                new Object[]{"body", new LinkedHashMap<String, String>(){{
                    put("key1", "value1");
                    put("key2", "'value'\"2\"");
                    put("key3", "value\"3\"");
                }}, "", true, "<body key1=value1 key2=\"\\'value\\'\\\"2\\\"\" key3='value\"3\"' ></body>"},
                new Object[]{"body", new LinkedHashMap<>(), "Contents", true, "<body>Contents</body>"},
                new Object[]{"body", new LinkedHashMap<>(), "Contents", true, "<body>Contents</body>"},
                new Object[]{"body", new LinkedHashMap<>(),
                        new Object[]{"p", new LinkedHashMap<>(), "Contents", true, "<p>Contents</p>"}, true,
                        "<body><p>Contents</p></body>"},
                new Object[]{"body", new LinkedHashMap<>(),
                        new Object[]{"p", new LinkedHashMap<>(), "Contents", true, "<p>Contents</p>"}, true,
                        "<body><p>Contents</p></body><body><p>Contents</p></body>"},
        };
    }

    @ParameterizedTest
    @MethodSource("getNodeTests")
    void testNodes(String tagName, LinkedHashMap<String, String> attributes, Object contents, boolean closed, String rawText) {
        final Node node = Node.newNode(rawText);
        testNode(node, tagName, closed, attributes, contents);
        final Node next = node.getNext();
        if (next != null) testNode(next, tagName, closed, attributes, contents);
    }

    private void testNode(Node node, String tagName, boolean closed, LinkedHashMap<String, String> attributes, Object contents) {
        assertEquals(tagName, node.getTagName());
        assertEquals(closed, node.isClosed());
        assertEquals(attributes, node.getAttributes());
        if (contents instanceof String) assertEquals(contents, node.getContents());
        else {
            Object[] objects = (Object[]) contents;
            final Node child = node.getChild();
            assertEquals(objects[0], child.getTagName());
            assertEquals(objects[1], child.getAttributes());
            assertEquals(objects[2], child.getContents());
        }
    }

//    @Test
//    void testReadHTMLPage() {
//        InputStream stream = NodeTest.class.getResourceAsStream("/index.html");
//        assertNotNull(stream);
//        Node node = Node.newNode(stream);
//    }
}