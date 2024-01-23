package it.fulminazzo.tagparser.nodes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ContainerNodeTest {
    private ContainerNode node;

    @BeforeEach
    void setUp() {
        node = new ContainerNode("test");
        node.setChild(new Node("child"));
    }

    @Test
    void testToHTML() {
        final String expected = "<test><child/></test>";
        assertEquals(expected, node.toHTML());
    }

    @Test
    void testToJson() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        final String expected = gson.toJson(node);
        assertEquals(expected, node.toJson());
    }

    @Test
    void testToString() {
        final String expected = "ContainerNode {\n" +
                "    children: 1\n" +
                "    text: null\n" +
                "    tagName: \"test\"\n" +
                "    attributes: {}\n" +
                "    next: null\n" +
                "}";
        assertEquals(expected, node.toString());
    }

    @Test
    void testAddChild() {
        Node n = new Node("child2");
        node.addChild(n);
        assertEquals(n, node.getChild().getNext());
    }

    @Test
    void testCountChild() {
        Node n = new Node("child2");
        node.addChild(n);
        assertEquals(2, node.countChildren());
    }

    @Test
    void testCountChildEmpty() {
        node.setChild((Node) null);
        assertEquals(0, node.countChildren());
    }

    @Test
    void testRemoveChild() {
        Node n = new Node("child2");
        node.addChild(n);
        node.removeChild(node.getChild());
        assertEquals(n, node.getChild());
    }

    @Test
    void testRemoveChildPredicate() {
        Node n = new Node("child2");
        node.addChild(n);
        node.removeChild(t -> t.getTagName().equals("child"));
        assertEquals(n, node.getChild());
    }

    @Test
    void testSetChild() {
        Node n = new Node("child2");
        node.setChild(n);
        assertEquals(n, node.getChild());
    }

    @Test
    void testGetChildren() {
        Node n = new Node("child2");
        node.addChild(n);
        List<Node> children = new ArrayList<>();
        children.add(node.getChild());
        children.add(node.getChild().getNext());
        assertIterableEquals(children, node.getChildren());
    }
}