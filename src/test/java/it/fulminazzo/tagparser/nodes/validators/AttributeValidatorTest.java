package it.fulminazzo.tagparser.nodes.validators;

import it.fulminazzo.tagparser.nodes.exceptions.NotValidAttributeException;
import it.fulminazzo.yamlparser.logging.LogMessage;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttributeValidatorTest {

    static Object[][] getValidators() {
        return new Object[][]{
                new Object[]{"1b", true, new ByteValidator()},
                new Object[]{"INVALID", false, new ByteValidator()},
                new Object[]{"1.0f", true, new FloatValidator()},
                new Object[]{"INVALID", false, new FloatValidator()},
                new Object[]{"1.0", true, new DoubleValidator()},
                new Object[]{"INVALID", false, new DoubleValidator()},
                new Object[]{"1000000000000l", true, new LongValidator()},
                new Object[]{"INVALID", false, new LongValidator()},
                new Object[]{"1058s", true, new ShortValidator()},
                new Object[]{"65535s", false, new ShortValidator()},
                new Object[]{"INVALID", false, new ShortValidator()},
                new Object[]{"65535", true, new IntegerValidator()},
                new Object[]{"INVALID", false, new IntegerValidator()},
                new Object[]{"65535", true, new NaturalValidator()},
                new Object[]{"-65535", false, new NaturalValidator()},
                new Object[]{"INVALID", false, new NaturalValidator()},
                new Object[]{"GENERAL_CANNOT_BE_NULL", true, new EnumValidator<>(LogMessage.class)},
                new Object[]{"INVALID", false, new EnumValidator<>(LogMessage.class)},
                new Object[]{"https://www.google.com", true, new URLValidator()},
                new Object[]{"INVALID", false, new URLValidator()},
                new Object[]{UUID.randomUUID().toString(), true, new UUIDValidator()},
                new Object[]{"INVALID", false, new UUIDValidator()},
        };
    }

    @ParameterizedTest
    @MethodSource("getValidators")
    void testAllValidators(String raw, boolean valid, AttributeValidator validator) {
        Executable executable = () -> validator.validate("unknown", raw);
        if (valid) assertDoesNotThrow(executable);
        else assertThrows(NotValidAttributeException.class, executable);
    }
}