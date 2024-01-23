package it.fulminazzo.tagparser.nodes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeTest {

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
                new Object[]{"final_test", true}
        };
    }

    @ParameterizedTest
    @MethodSource("getTagNameTests")
    void testTagNameRegex(String tag, boolean valid) {
        assertEquals(valid, tag.matches(Node.TAG_NAME_REGEX));
    }

    @Nested
    @DisplayName("Test various methods")
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

        private void setAttributes() {
            node.setAttribute("key1", "value1");
            node.setAttribute("key2", "\"value\\\"2\\\"\"");
            node.setAttribute("key3", "'value3'");
            node.setAttribute("key4", null);
        }
    }
}