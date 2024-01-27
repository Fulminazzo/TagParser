package it.fulminazzo.tagparser.markup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.fulminazzo.tagparser.markup.exceptions.WriteException;
import it.fulminazzo.tagparser.nodes.ContainerNode;
import it.fulminazzo.tagparser.nodes.Node;
import it.fulminazzo.tagparser.nodes.NodeTest;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class XMLObjectTest {
    private static final File file = new File(NodeTest.RESOURCES, "test1.xml");
    private XMLObject xmlObject;

    private static Object[][] getXMLObjectTests() {
        return new Object[][]{
                new Object[]{
                        "test1.xml", "xml",
                        new LinkedHashMap<String, String>(){{
                            put("version", "1.0");
                            put("encoding", "UTF-8");
                        }},
                        new ContainerNode("note")
                                .addChild(new ContainerNode("to").setText("Tove"))
                                .addChild(new ContainerNode("from").setText("Jani"))
                                .addChild(new ContainerNode("heading").setText("Reminder"))
                                .addChild(new ContainerNode("body").setText("Don't forget me this weekend!"))
                },
                new Object[]{
                        "test2.xml", "xml",
                        new LinkedHashMap<String, String>(){{
                            put("version", "1.0");
                            put("encoding", "UTF-8");
                        }},
                        new ContainerNode("node")
                                .addChild(new ContainerNode("name").setText("Alex"))
                                .addChild(new ContainerNode("age").setText("10"))
                                .addChild(new ContainerNode("access-granted").setText("true"))
                                .addChild(new ContainerNode("researches")
                                    .addChild(new ContainerNode("name").setText("Brain Surgery"))
                                    .addChild(new ContainerNode("name").setText("Cardiovascular system")))
                }
        };
    }

    @BeforeEach
    void setUp() {
        xmlObject = new XMLObject(file);
    }

    @Test
    void testWriteFail() {
        final File file = mock(File.class);
        final File parentFile = mock(File.class);
        when(file.getParentFile()).thenReturn(parentFile);
        when(parentFile.isDirectory()).thenReturn(false);
        when(file.mkdirs()).thenReturn(false);
        assertThrows(WriteException.class, () -> xmlObject.write(file));
    }

    @Test
    void testWrite() throws IOException {
        final File tmpFile = File.createTempFile(file.getParent(), "test1-tmp.xml");
        xmlObject.write(tmpFile);
        assertEquals(fileToString(), FileUtils.readFileToString(tmpFile));
    }

    @ParameterizedTest
    @MethodSource("getXMLObjectTests")
    void testXMLObjects(final String fileName, final String documentType,
                        final Map<String, String> attributes, final Node rootNode) throws IOException {
        final File file = new File(NodeTest.RESOURCES, fileName);
        final XMLObject xmlObject = new XMLObject(file);
        assertEquals(documentType, xmlObject.getDocumentType());
        assertEquals(attributes, xmlObject.getAttributes());
        assertEquals(rootNode, xmlObject.getRoot());
        assertEquals(FileUtils.readFileToString(file)
                .replaceAll("<!--[^\n]*-->", "")
                .replaceAll("\n *", ""), xmlObject.toHTML());
    }

    @Test
    void testToMap() {
        final Map<?, ?> expected = new LinkedHashMap<Object, Object>(){{
            put("node", new LinkedHashMap<Object, Object>(){{
                put("name", "Alex");
                put("age", "10");
                put("access-granted", "true");
                put("researches", new LinkedHashMap<Object, Object>(){{
                    put("name", "Cardiovascular system");
                }});
            }});
        }};
        assertEquals(expected, new XMLObject(new File(NodeTest.RESOURCES, "test2.xml")).toMap());
    }

    @Test
    void testXMLObjectToJSON() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        final String expected = gson.toJson(xmlObject);
        assertEquals(expected, xmlObject.toJSON());
    }

    @Test
    void testXMLObjectToYAML() {
        final String yaml = xmlObject.toYAML();
        assertDoesNotThrow(() -> new Yaml().loadAs(yaml, Map.class), yaml);
    }

    String fileToString() throws IOException {
        return FileUtils.readFileToString(file)
                .replaceAll("<!--[^\n]*-->", "")
                .replaceAll("\n *", "");
    }
}