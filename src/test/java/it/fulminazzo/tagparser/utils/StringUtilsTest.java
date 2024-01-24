package it.fulminazzo.tagparser.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testParseContent() {
        final String content = "&lt;tag1&gt; &amp; &lt;tag2&gt; are &apos;invalid&apos; tags, or &quot;unsupported&quot;";
        assertEquals("<tag1> & <tag2> are 'invalid' tags, or \"unsupported\"", StringUtils.parseContent(content));
    }

    @Test
    void testParseParsedContent() {
        final String content = "<tag1> & <tag2> are 'invalid' tags, or \"unsupported\"";
        assertEquals("<tag1> & <tag2> are 'invalid' tags, or \"unsupported\"", StringUtils.parseContent(content));
    }

    @Test
    void testUnParseContent() {
        final String content = "<tag1> & <tag2> are 'invalid' tags, or \"unsupported\"";
        assertEquals("&lt;tag1&gt; &amp; &lt;tag2&gt; are &apos;invalid&apos; tags, or &quot;unsupported&quot;", StringUtils.unParseContent(content));
    }

    @Test
    void testUnParseUnParsedContent() {
        final String content = "&lt;tag1&gt; &amp; &lt;tag2&gt; are &apos;invalid&apos; tags, or &quot;unsupported&quot;";
        assertEquals("&lt;tag1&gt; &amp; &lt;tag2&gt; are &apos;invalid&apos; tags, or &quot;unsupported&quot;", StringUtils.unParseContent(content));
    }
}