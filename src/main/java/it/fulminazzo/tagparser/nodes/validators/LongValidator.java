package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Long} objects.
 * It requires values to be specified with an 'l' at the end.
 * <p>
 * Example: 1l
 */
public class LongValidator extends IntegerValidator {

    @Override
    public void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException {
        try {
            if (!value.toLowerCase().endsWith("l")) throw new Exception();
            Long.valueOf(value.substring(0, value.length() - 1));
        } catch (Exception e) {
            throw new NotValidAttributeException(name, Long.class, value);
        }
    }
}
