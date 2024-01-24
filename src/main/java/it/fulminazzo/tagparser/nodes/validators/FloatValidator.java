package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Float} objects.
 * It requires values to be specified with an 'f' at the end.
 * <p>
 * Example: 1f
 */
public class FloatValidator extends DoubleValidator {

    @Override
    public void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException {
        try {
            if (!value.toLowerCase().endsWith("f")) throw new Exception();
            Float.valueOf(value.substring(0, value.length() - 1));
        } catch (Exception e) {
            throw new NotValidAttributeException(name, Float.class, value);
        }
    }
}
