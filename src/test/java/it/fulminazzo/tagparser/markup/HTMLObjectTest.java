package it.fulminazzo.tagparser.markup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.fulminazzo.tagparser.nodes.ContainerNode;
import it.fulminazzo.tagparser.nodes.Node;
import it.fulminazzo.tagparser.nodes.NodeTest;
import it.fulminazzo.yamlparser.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HTMLObjectTest {
    private static final File file = new File(NodeTest.RESOURCES, "index2.html");
    private HTMLObject htmlObject;

    @BeforeEach
    void setUp() {
        htmlObject = new HTMLObject(file);
    }

    @Test
    void testWrite() throws IOException {
        final File tmpFile = File.createTempFile(file.getParent(), "index2-tmp.html");
        htmlObject.write(tmpFile);
        assertEquals(fileToString(), FileUtils.readFileToString(tmpFile));
    }

    @Test
    void testGetHead() {
        final Node head = htmlObject.getHead();
        assertNotNull(head);
        assertInstanceOf(ContainerNode.class, head);
        assertEquals("head", head.getTagName());
        assertEquals(13, ((ContainerNode) head).countChildren());
        assertTrue(head.getAttributes().isEmpty());
        final Node next = head.getNext();
        assertNotNull(next);
        assertEquals("body", next.getTagName());
    }

    @Test
    void testGetBody() {
        final Node body = htmlObject.getBody();
        assertNotNull(body);
        assertInstanceOf(ContainerNode.class, body);
        assertEquals("body", body.getTagName());
        assertEquals(3, ((ContainerNode) body).countChildren());
        assertTrue(body.getAttributes().isEmpty());
        final Node next = body.getNext();
        assertNull(next);
    }

    @Test
    void testGetScripts() {
        final @NotNull List<Node> scripts = new LinkedList<>(htmlObject.getScripts());
        assertEquals(8, scripts.size());
        final String[] src = new String[]{
                "backend.js", "script.js",
                "wave-function/utils.js", "wave-function/img_utils.js", "wave-function/tiles.js",
                "wave-function/grid.js", "wave-function/wfc.js", "wave-function/background.js"
        };
        for (int i = 0; i < scripts.size(); i++)
            assertEquals(src[i], scripts.get(i).getAttribute("src"));
    }

    @Test
    void testGetStyles() {
        final @NotNull List<Node> styles = new LinkedList<>(htmlObject.getStyles());
        assertEquals(3, styles.size());
        final String[] src = new String[]{"terminal.css", "stats.css", "style.css"};
        for (int i = 0; i < styles.size(); i++)
            assertEquals(src[i], styles.get(i).getAttribute("href"));
    }

    @Test
    void testHTMLObject() throws IOException {
        assertEquals(fileToString(), htmlObject.toHTML());
    }

    @Test
    void testHTMLObjectToJSON() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        final String expected = gson.toJson(htmlObject);
        assertEquals(expected, htmlObject.toJSON());
    }

    @Test
    void testHTMLObjectToYAML() {
        final String yaml = htmlObject.toYAML();
        assertDoesNotThrow(() -> new Yaml().loadAs(yaml, Map.class), yaml);
    }

    String fileToString() throws IOException {
        return FileUtils.readFileToString(file)
                .replaceAll("\n *", "")
                .replace("linecap=\"round\" ", "linecap=\"round\"")
                .replace("<!-- Wave Function Collapse Background -->", "");
    }

    @Test
    void testToMap() {
        final Map<?, ?> expected = new LinkedHashMap<Object, Object>(){{
            put("html", new LinkedHashMap<Object, Object>(){{
                put("head", new LinkedHashMap<Object, Object>(){{
                    put("meta", null);
                    put("title", "Wave Function");
                    put("link", null);
                    put("script", null);
                }});
                put("body", new LinkedHashMap<Object, Object>(){{
                    put("canvas", null);
                    put("div", Arrays.asList(
                            new LinkedHashMap<Object, Object>(){{
                                put("p", "Loading docker stats...");
                            }},
                            new LinkedHashMap<Object, Object>(){{
                                put("p", "Loading docker compose logs...");
                            }},
                            Arrays.asList(
                                new LinkedHashMap<Object, Object>(){{
                                    put("h3", "Storage");
                                    put("div", new LinkedHashMap<Object, Object>(){{
                                        put("div", new LinkedHashMap<Object, Object>(){{
                                            put("div", new LinkedHashMap<Object, Object>(){{
                                                put("p", "0%");
                                            }});
                                        }});
                                        put("svg", new LinkedHashMap<Object, Object>(){{
                                            put("defs", new LinkedHashMap<Object, Object>(){{
                                                put("linearGradient", Arrays.asList(null, null));
                                            }});
                                            put("circle", null);
                                        }});
                                    }});
                                    put("p", "Info");
                                }},
                                new LinkedHashMap<Object, Object>(){{
                                    put("h3", "Download");
                                    put("div", new LinkedHashMap<Object, Object>(){{
                                        put("div", new LinkedHashMap<Object, Object>(){{
                                            put("div", new LinkedHashMap<Object, Object>(){{
                                                put("p", "0%");
                                            }});
                                        }});
                                        put("svg", new LinkedHashMap<Object, Object>(){{
                                            put("defs", new LinkedHashMap<Object, Object>(){{
                                                put("linearGradient", Arrays.asList(null, null));
                                            }});
                                            put("circle", null);
                                        }});
                                    }});
                                    put("p", "Info");
                                }},
                                new LinkedHashMap<Object, Object>(){{
                                    put("h3", "Upload");
                                    put("div", new LinkedHashMap<Object, Object>(){{
                                        put("div", new LinkedHashMap<Object, Object>(){{
                                            put("div", new LinkedHashMap<Object, Object>(){{
                                                put("p", "0%");
                                            }});
                                        }});
                                        put("svg", new LinkedHashMap<Object, Object>(){{
                                            put("defs", new LinkedHashMap<Object, Object>(){{
                                                put("linearGradient", Arrays.asList(null, null));
                                            }});
                                            put("circle", null);
                                        }});
                                    }});
                                    put("p", "Info");
                                }}
                            ),
                            Arrays.asList(
                                    new LinkedHashMap<Object, Object>(){{
                                        put("button", new LinkedHashMap<Object, Object>(){{
                                            put("p", "-");
                                        }});
                                    }},
                                    new LinkedHashMap<Object, Object>(){{
                                        put("p", "Loading session terminal...");
                                    }}
                            )
                    ));
                }});
            }});
        }};
        assertEquals(expected, htmlObject.toMap());
    }
}