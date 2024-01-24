package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A validator for URL strings.
 */
public class URLValidator implements AttributeValidator {
    public static final String URL_REGEX = "^((?:https?://)?(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*)$";

    @Override
    public void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException {
        if (!Pattern.compile(URL_REGEX).matcher(value).find())
            throw new NotValidAttributeException(name, "URL", value);
    }
}
