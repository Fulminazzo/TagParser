package it.fulminazzo.tagparser.utils;

public class StringUtils {
    private static final char[] REPLACED_CHARS = new char[]{'"', '\''};

    public static String removeQuotes(String string) {
        if (string == null) return null;
        if (string.startsWith("'") && string.endsWith("'") ||
                string.startsWith("\"") && string.endsWith("\""))
            string = string.substring(1, string.length() - 1);
        for (char c : REPLACED_CHARS) string = string.replace("\\" + c, "" + c);
        return string;
    }
}