package it.fulminazzo.tagparser.utils;

public class StringUtils {
    public static final String OPTIONS_REGEX = "([^=\\n ]+)(?:=(\"((?:\\\\\"|[^\"])+)\"|'((?:\\\\'|[^'])+)'|[^ ]+))?";

    static String removeQuotes(String string) {
        if (string == null) return null;
        if (string.startsWith("'") && string.endsWith("'") ||
                string.startsWith("\"") && string.endsWith("\""))
            string = string.substring(1, string.length() - 1);
        return string.replace("\\\\", "\\")
                .replace("\\\"", "\"")
                .replace("\\'", "'");
    }
}