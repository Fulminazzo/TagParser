package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A validator for {@link UUID} objects.
 */
public class UUIDValidator implements AttributeValidator {

    @Override
    public void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException {
        try {
            UUID.fromString(value);
        } catch (Exception e) {
            throw new NotValidAttributeException(name, UUIDValidator.class, value);
        }
    }
}
