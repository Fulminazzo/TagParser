package it.fulminazzo.tagparser.nodes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.fulminazzo.tagparser.nodes.exceptions.NodeException;
import it.fulminazzo.tagparser.nodes.exceptions.NotValidTagNameException;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileDoesNotExistException;
import it.fulminazzo.tagparser.nodes.exceptions.files.FileIsDirectoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NodeTest {
    protected static final File RESOURCES = new File("build/resources/test");

    static Object[][] getTagNameTests() {
        return new Object[][]{
                new Object[]{"test", true},
                new Object[]{"_test", false},
                new Object[]{"1test", false},
                new Object[]{"Test", true},
                new Object[]{"te_st", true},
                new Object[]{"TE_ST", true},
                new Object[]{"test_", false},
                new Object[]{"TESTS_", false},
                new Object[]{"test-", false},
                new Object[]{"TEST-", false},
                new Object[]{"Final_Test99", true},
                new Object[]{"final_test99", true},
                new Object[]{"Final_Test", true},
                new Object[]{"final_test", true},
                new Object[]{"<<final_test", false}
        };
    }

    static Object[][] getNodeTests() {
        return new Object[][]{
                new Object[]{"body", new LinkedHashMap<>(), "", "<body/>"},
                new Object[]{"body", new LinkedHashMap<>(), "", "<body></body>"},
                new Object[]{"body", new LinkedHashMap<String, String>(){{
                    put("key1", "value1");
                    put("key2", "'value'\"2\"");
                    put("key3", "value\"3\"");
                }}, "", "<body key1=value1 key2=\"\\'value\\'\\\"2\\\"\" key3='value\"3\"'/>"},
                new Object[]{"body", new LinkedHashMap<String, String>(){{
                    put("key1", "value1");
                    put("key2", "'value'\"2\"");
                    put("key3", "value\"3\"");
                }}, "", "<body key1=value1 key2=\"\\'value\\'\\\"2\\\"\" key3='value\"3\"' ></body>"},
                new Object[]{"body", new LinkedHashMap<>(), "Contents", "<body>Contents</body>"},
                new Object[]{"body", new LinkedHashMap<>(), "Contents", "<body>Contents</body>"},
                new Object[]{"body", new LinkedHashMap<>(),
                        new Object[]{"p", new LinkedHashMap<>(), "Contents", "<p>Contents</p>"},
                        "<body><p>Contents</p></body>"},
                new Object[]{"body", new LinkedHashMap<>(),
                        new Object[]{"p", new LinkedHashMap<>(), "Contents", "<p>Contents</p>"},
                        "<body><p>Contents</p></body><body><p>Contents</p></body>"},
        };
    }

    @ParameterizedTest
    @MethodSource("getNodeTests")
    void testNodes(String tagName, LinkedHashMap<String, String> attributes, Object contents, String rawText) {
        final Node node = Node.newNode(rawText);
        testNode(node, tagName, attributes, contents);
        final Node next = node.getNext();
        if (next != null) testNode(next, tagName, attributes, contents);
    }

    private void testNode(Node node, String tagName, LinkedHashMap<String, String> attributes, Object contents) {
        assertEquals(tagName, node.getTagName());
        assertEquals(attributes, node.getAttributes());
        if (node instanceof ContainerNode) {
            ContainerNode containerNode = (ContainerNode) node;
            if (contents instanceof String) assertEquals(contents, containerNode.getText());
            else {
                Object[] objects = (Object[]) contents;
                final Node child = containerNode.getChild();
                assertEquals(objects[0], child.getTagName());
                assertEquals(objects[1], child.getAttributes());
            }
        }
    }

    @Test
    void testMultilineAttributes() {
        final String raw = "<path class=\"line\" stroke-width=\"10\" stroke-linecap=\"round\" stroke-linejoin=\"round\"\n" +
                "                          d=\"m 20 40 h 60 a 1 1 0 0 1 0 20 h -60 a 1 1 0 0 1 0 -40 h 30 v 70\"></path>";
        Map<String, String> attributes = new LinkedHashMap<String, String>(){{
            put("class", "line");
            put("stroke-width", "10");
            put("stroke-linecap", "round");
            put("stroke-linejoin", "round");
            put("d", "m 20 40 h 60 a 1 1 0 0 1 0 20 h -60 a 1 1 0 0 1 0 -40 h 30 v 70");
        }};
        assertEquals(attributes, Node.newNode(raw).getAttributes());
    }

    @ParameterizedTest
    @MethodSource("getTagNameTests")
    void testTagNameRegex(String tag, boolean valid) {
        Executable getNode = () -> new Node(tag);
        if (valid) assertDoesNotThrow(getNode);
        else assertThrowsExactly(NotValidTagNameException.class, getNode);
    }

    @Test
    void testEquality() {
        Node node1 = Node.newNode("<test key1=value1 key2=value2/>");
        Node node2 = Node.newNode("<test key1=value1 key2=value2/>");
        assertEquals(node2, node1);
    }

    @Test
    void testNotEqualOptions() {
        Node node1 = Node.newNode("<test key1=value1 key2=value2/>");
        Node node2 = Node.newNode("<test key1=value1 key2=value3/>");
        assertNotEquals(node2, node1);
    }

    @Test
    void testNotEqualTags() {
        Node node1 = Node.newNode("<test key1=value1 key2=value2/>");
        Node node2 = Node.newNode("<test2 key1=value1 key2=value2/>");
        assertNotEquals(node2, node1);
    }

    @Nested
    @DisplayName("Test node exceptions")
    class NodeExceptions {

        @Test
        void testStreamError() throws IOException {
            InputStream stream = mock(InputStream.class);
            when(stream.read()).thenThrow(IOException.class);
            assertThrowsExactly(NodeException.class, () -> Node.newNode(stream));
        }

        @Test
        void testNotExistingFile() {
            assertThrowsExactly(FileDoesNotExistException.class, () ->
                    Node.newNode(new File(RESOURCES, "not-existing.xml")));
        }

        @Test
        void testDirectoryFile() {
            assertThrowsExactly(FileIsDirectoryException.class, () ->
                    Node.newNode(RESOURCES));
        }
    }

    @Nested
    @DisplayName("Test manipulation methods")
    class NodeMethods {
        private Node node;

        @BeforeEach
        void setUp() {
            node = new Node("test");
            node.setNext(new Node("next"));
        }

        @Test
        void testSetAttribute() {
            Map<String, String> map = new LinkedHashMap<>();
            assertEquals(map, node.getAttributes());
            map.put("key", "value");
            node.setAttribute("key", "\"value\"");
            assertEquals(map, node.getAttributes());
        }

        @Test
        void testSetAttributes() {
            Map<String, String> map = new LinkedHashMap<>();
            assertEquals(map, node.getAttributes());
            map.put("key1", "\"value\"");
            map.put("key2", null);
            node.setAttributes(map);
            map.put("key1", "value");
            assertEquals(map, node.getAttributes());
        }

        @Test
        void testGetAttributes() {
            setAttributes();
            assertEquals("value1", node.getAttribute("key1"));
            assertEquals("value\"2\"", node.getAttribute("key2"));
            assertEquals("value3", node.getAttribute("key3"));
            assertNull(node.getAttribute("key4"));
        }

        @Test
        void testToHTML() {
            setAttributes();
            final String expected = "<test key1=\"value1\" key2=\"value\\\"2\\\"\" key3=\"value3\" key4/>";
            assertEquals(expected, node.toHTML());
        }

        @Test
        void testToJson() {
            setAttributes();
            Gson gson = new GsonBuilder().serializeNulls().create();
            final String expected = gson.toJson(node);
            assertEquals(expected, node.toJson());
        }

        @Test
        void testToString() {
            setAttributes();
            final String expected = "Node {\n" +
                    "    tagName: \"test\"\n" +
                    "    attributes: {key1=value1, key2=value\"2\", key3=value3, key4=null}\n" +
                    "    next: Node(\"next\")\n" +
                    "}";
            assertEquals(expected, node.toString());
        }

        @Test
        void testAddNextString() {
            Node n = new Node("next2");
            node.addNext("<next2/>");
            assertEquals(n, node.getNext().getNext());
        }

        @Test
        void testAddNextFile() {
            Node n = new Node("next2");
            node.addNext(new File(RESOURCES, "node.xml"));
            assertEquals(n, node.getNext().getNext());
        }

        @Test
        void testAddNextStream() {
            Node n = new Node("next2");
            node.addNext(new ByteArrayInputStream("<next2/>".getBytes()));
            assertEquals(n, node.getNext().getNext());
        }

        @Test
        void testAddNextNode() {
            Node n = new Node("next2");
            node.addNext(n);
            assertEquals(n, node.getNext().getNext());
        }

        @Test
        void testCountNext() {
            Node n = new Node("next2");
            node.addNext(n);
            assertEquals(2, node.countNextNodes());
        }

        @Test
        void testRemoveNext() {
            Node n = new Node("next2");
            node.addNext(n);
            node.removeNext(node.getNext());
            assertEquals(n, node.getNext());
        }

        @Test
        void testRemoveNextPredicate() {
            Node n = new Node("next2");
            node.addNext(n);
            node.removeNext(t -> t.getTagName().equals("next"));
            assertEquals(n, node.getNext());
        }

        @Test
        void testSetNextString() {
            Node n = new Node("next2");
            node.setNext("<next2/>");
            assertEquals(n, node.getNext());
        }

        @Test
        void testSetNextFile() {
            Node n = new Node("next2");
            node.setNext(new File(RESOURCES, "node.xml"));
            assertEquals(n, node.getNext());
        }

        @Test
        void testSetNextStream() {
            Node n = new Node("next2");
            node.setNext(new ByteArrayInputStream("<next2/>".getBytes()));
            assertEquals(n, node.getNext());
        }

        @Test
        void testSetNext() {
            Node n = new Node("next2");
            node.setNext(n);
            assertEquals(n, node.getNext());
        }

        private void setAttributes() {
            node.setAttribute("key1", "value1");
            node.setAttribute("key2", "\"value\\\"2\\\"\"");
            node.setAttribute("key3", "'value3'");
            node.setAttribute("key4", null);
        }
    }
}