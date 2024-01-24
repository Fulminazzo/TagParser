package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import org.jetbrains.annotations.NotNull;

/**
 * A consumer that throws {@link NotValidAttributeException}
 */
@FunctionalInterface
public interface AttributeValidator {

    /**
     * Test if the given attribute corresponds to the desired name output.
     * If not, throw a not valid attribute exception.
     *
     * @param name the attribute name
     * @param value the attribute value
     * @throws NotValidAttributeException the not valid attribute exception
     */
    void validate(@NotNull String name, @NotNull String value) throws NotValidAttributeException;

}