package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Double} objects.
 */
public class DoubleValidator implements AttributeValidator {

    @Override
    public void validate(String name, @NotNull String value) throws NotValidAttributeException {
        try {
            Double.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new NotValidAttributeException(name, Double.class, value);
        }
    }
}
