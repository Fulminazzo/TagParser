package it.fulminazzo.tagparser.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {
    
    static Object[][] getOptionsTests() {
        return new String[][]{
                new String[]{"key", "test", "key=test"},
                new String[]{"key", "single", "key='single'"},
                new String[]{"key", "double", "key=\"double\""},
                new String[]{"mix", "'strange' and \"mixed\"", "mix=\"'strange' and \\\"mixed\\\"\""},
                new String[]{"mix", "'strange' and \"mixed\"", "mix=\"\\'strange\\' and \\\"mixed\\\"\""},
                new String[]{"mix", "'strange' and \"mixed\"", "mix=\"\\'strange\\' and \\\"mixed\\\"\" key=ignored"},
                new String[]{"style", "position: absolute; top: 0px; left: 0px; border: none; visibility: hidden;",
                        "style=\"position: absolute; top: 0px; left: 0px; border: none; visibility: hidden;\""},
                new String[]{"key10", "this value is <also> good", "key10='this value is <also> good'>"},
                new String[]{"json", "{name: \"Alex\"; age: 10; title: \"Json is amazing\"}",
                        "json=\"{name: \\\"Alex\\\"; age: 10; title: \\\"Json is amazing\\\"}\" title=OVERWRITTEN"}
        };
    }

    @ParameterizedTest
    @MethodSource("getOptionsTests")
    void testOptionsRegex(String key, String value, String raw) {
        Matcher matcher = Pattern.compile(StringUtils.OPTIONS_REGEX).matcher(raw);
        assertTrue(matcher.find());
        assertEquals(key, matcher.group(1));
        assertEquals(value, StringUtils.removeQuotes(matcher.group(2)));
    }
}