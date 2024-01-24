package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for a given enum class
 *
 * @param <T> the enum type
 */
public class EnumValidator<T extends Enum<T>> implements AttributeValidator {
    private final Class<T> enumClass;

    /**
     * Instantiates a new Enum validator.
     *
     * @param enumClass the enum class
     */
    public EnumValidator(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException {
        try {
            Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotValidAttributeException(name, enumClass, value);
        }
    }
}
