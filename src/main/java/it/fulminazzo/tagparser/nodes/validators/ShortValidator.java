package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Short} objects.
 * It requires values to be specified with an 's' at the end.
 * <p>
 * Example: 1s
 */
public class ShortValidator extends IntegerValidator {

    @Override
    public void validate(String name, @NotNull String value) throws NotValidAttributeException {
        try {
            if (!value.toLowerCase().endsWith("s")) throw new Exception();
            Short.valueOf(value.substring(0, value.length() - 1));
        } catch (Exception e) {
            throw new NotValidAttributeException(name, Short.class, value);
        }
    }
}
