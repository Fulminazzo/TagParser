package it.fulminazzo.tagparser.nodes;

import it.fulminazzo.yamlparser.utils.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTMLTest {

    ContainerNode getSimpleHTML() {
        return new ContainerNode("html")
                .setAttribute("lang", "en")
                .addChild(
                        new ContainerNode("head")
                                .addChild(new Node("meta").setAttribute("charset", "UTF-8"))
                                .addChild(new ContainerNode("title").setText("Hamburger Menus"))
                                .addChild(new Node("link").setAttributes("href", "style.css", "rel", "stylesheet"))
                                .addChild(new ContainerNode("script").setAttributes("src", "main.js", "type", "application/javascript"))
                )
                .addChild(new ContainerNode("body").addChild(new ContainerNode("div").setAttributes("class", "examples")
                                .addChild(new ContainerNode("p").setText("Hello world"))
                                .addChild(
                                        new ContainerNode("button").setAttributes("class", "button-one", "aria-expanded", "false")
                                                .addChild(
                                                        new ContainerNode("svg").setAttributes("fill", "var(--button-color)", "class", "hamburger", "viewBox", "0 0 100 100", "width", "250")
                                                                .addChild(new ContainerNode("rect").setAttributes("class", "line top", "x", "10", "y", "25", "width", "80", "height", "10", "rx", "5"))
                                                                .addChild(new ContainerNode("rect").setAttributes("class", "line middle", "x", "10", "y", "45", "width", "80", "height", "10", "rx", "5"))
                                                                .addChild(new ContainerNode("rect").setAttributes("class", "line bottom", "x", "10", "y", "65", "width", "80", "height", "10", "rx", "5"))
                                                )
                                )
                                .addChild(
                                        new ContainerNode("button").setAttributes("class", "button-two", "aria-expanded", "undefined")
                                                .addChild(
                                                        new ContainerNode("svg").setAttributes("stroke", "var(--button-color)", "class", "hamburger", "viewBox", "0 0 100 100", "width", "250")
                                                                .addChild(new ContainerNode("line").setAttributes("class", "line top", "x1", "85", "x2", "15", "y1", "40", "y2", "40", "stroke-width", "10", "stroke-linecap", "round", "stroke-dasharray", "80", "stroke-dashoffset", "0"))
                                                                .addChild(new ContainerNode("line").setAttributes("class", "line bottom", "x1", "15", "x2", "85", "y1", "60", "y2", "60", "stroke-width", "10", "stroke-linecap", "round", "stroke-dasharray", "80", "stroke-dashoffset", "0"))
                                                )
                                )
                                .addChild(
                                        new ContainerNode("button").setAttributes("class", "button-three", "aria-expanded", "false")
                                                .addChild(
                                                        new ContainerNode("svg").setAttributes("stroke", "var(--button-color)", "fill", "none", "class", "hamburger", "viewBox", "-10 -10 120 120", "width", "250")
                                                                .addChild(new ContainerNode("path").setAttributes("class", "line", "stroke-width", "10", "stroke-linecap", "round", "stroke-linejoin", "round", "d", "m 20 40 h 60 a 1 1 0 0 1 0 20 h -60 a 1 1 0 0 1 0 -40 h 30 v 70"))
                                                )
                                )
                        ))
                ;
    }

    @Test
    void testNodesFromName() {
        final Node node = getSimpleHTML();
        final Set<Node> nodes = node.getNodes("svg");
        assertEquals(3, nodes.size());
    }

    @Test
    void testNodesFromPredicate() {
        final Node node = getSimpleHTML();
        final Set<Node> nodes = node.getNodes(n -> n.getTagName().equals("button"));
        assertEquals(3, nodes.size());
    }

    @Test
    void testNodeFromName() {
        final Node node = getSimpleHTML();
        assertEquals(new Node("link").setAttributes("href", "style.css", "rel", "stylesheet")
                        .addNext(new ContainerNode("script").setAttributes("src", "main.js", "type", "application/javascript")),
                node.getNode("link"));
    }

    @Test
    void testNodeFromPredicate() {
        final Node node = getSimpleHTML();
        assertEquals(new ContainerNode("path").setAttributes("class", "line", "stroke-width", "10", "stroke-linecap", "round", "stroke-linejoin", "round", "d", "m 20 40 h 60 a 1 1 0 0 1 0 20 h -60 a 1 1 0 0 1 0 -40 h 30 v 70"),
                node.getNode(n -> n.getTagName().equals("path")));
    }

    @Test
    void testHTMLOutput() throws IOException {
        final String expected = FileUtils.readFileToString(new File(NodeTest.RESOURCES, "index.html"))
                .replaceAll("\n *", "")
                .replace("\"stroke", "\" stroke")
                .replace("\"d", "\" d")
                .replace("<!-- rx: round corners -->", "");
        final String html = getSimpleHTML().toHTML();
        assertEquals(expected, html);
    }

    @Test
    void testHTMLFileOutput() throws IOException {
        File file = new File(NodeTest.RESOURCES, "index.html");
        final String expected = FileUtils.readFileToString(file)
                .replaceAll("\n *", "")
                .replace("\"stroke", "\" stroke")
                .replace("\"d", "\" d")
                .replace("<!-- rx: round corners -->", "");
        final String html = Node.newNode(file).toHTML();
        assertEquals(expected, html);
    }

    @Test
    void testHTMLInput() {
        final Node node = Node.newNode(new File(NodeTest.RESOURCES, "index.html"));
        assertEquals(getSimpleHTML(), node);
    }
}
