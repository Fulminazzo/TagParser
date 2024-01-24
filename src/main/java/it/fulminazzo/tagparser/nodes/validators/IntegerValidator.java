package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Integer} objects.
 */
public class IntegerValidator implements AttributeValidator {

    @Override
    public void validate(String name, @NotNull String value) throws NotValidAttributeException {
        try {
            Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new NotValidAttributeException(name, Integer.class, value);
        }
    }
}
