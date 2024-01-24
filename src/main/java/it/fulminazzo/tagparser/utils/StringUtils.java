package it.fulminazzo.tagparser.utils;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type String utils.
 */
public class StringUtils {
    private static final char[] REPLACED_CHARS = new char[]{'"', '\''};
    private static final Map<String, String> PARSED_CHARS = new LinkedHashMap<String, String>(){{
        put("&lt;", "<");
        put("&#60;", "<");
        put("&gt;", ">");
        put("&#62;", ">");
        put("&apos;", "'");
        put("&quot;", "\"");
        put("&amp;", "&");
    }};

    /**
     * Unescapes some special characters.
     * More specifically, single and double quotes are unescaped.
     * <p>
     * For example, \" becomes ".
     *
     * @param string the string
     * @return the string
     */
    public static String removeQuotes(@Nullable String string) {
        if (string == null) return null;
        if (string.startsWith("'") && string.endsWith("'") ||
                string.startsWith("\"") && string.endsWith("\""))
            string = string.substring(1, string.length() - 1);
        for (char c : REPLACED_CHARS) string = string.replace("\\" + c, "" + c);
        return string;
    }

    /**
     * Parse content using entity-references of XML.
     *
     * @param content the content
     * @return the string
     */
    public static String parseContent(@Nullable String content) {
        if (content == null) return null;
        for (String key : PARSED_CHARS.keySet())
            content = content.replace(key, PARSED_CHARS.get(key));
        return content;
    }

    /**
     * Un parse content using entity-references of XML.
     *
     * @param content the content
     * @return the string
     */
    public static String unParseContent(@Nullable String content) {
        if (content == null) return null;
        for (String reference : PARSED_CHARS.keySet()) {
            final String parsed = PARSED_CHARS.get(reference);
            final Pattern pattern = Pattern.compile(parsed + "(.{0,5})");
            Matcher matcher = pattern.matcher(content);
            int i = 0;
            while (matcher.find(i)) {
                final String match = matcher.group();
                final String group = matcher.group(1);
                final String found = parsed + group.split(" ")[0];
                if (PARSED_CHARS.keySet().stream().noneMatch(found::contains)) {
                    final int end = matcher.end() - match.length();
                    content = content.substring(0, end) + reference + content.substring(end + 1);
                    matcher = pattern.matcher(content);
                }
                i += group.length();
            }
        }
        return content;
    }
}