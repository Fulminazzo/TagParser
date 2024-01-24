package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Byte} objects.
 * It requires values to be specified with a 'b' at the end.
 * <p>
 * Example: 1b
 */
public class ByteValidator implements AttributeValidator {

    @Override
    public void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException {
        try {
            if (!value.toLowerCase().endsWith("b")) throw new Exception();
            Byte.valueOf(value.substring(0, value.length() - 1));
        } catch (Exception e) {
            throw new NotValidAttributeException(name, Byte.class, value);
        }
    }
}
