package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Integer} objects higher or equal than 0.
 */
public class NaturalValidator implements AttributeValidator {

    @Override
    public void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException {
        try {
            if (Integer.parseInt(value) < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            throw new NotValidAttributeException(name, Integer.class, value);
        }
    }
}
