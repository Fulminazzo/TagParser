package it.fulminazzo.tagparser;

import it.fulminazzo.tagparser.nodes.Node;
import it.fulminazzo.tagparser.nodes.exceptions.NotValidTagNameException;
import it.fulminazzo.tagparser.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The interface Attributable.
 *
 * @param <T> the type parameter
 */
@SuppressWarnings("unchecked")
public interface Attributable<T extends Attributable<?>> {

    /**
     * Sets attribute.
     *
     * @param name  the name
     * @param value the value
     * @return the attribute
     */
    default T setAttribute(@NotNull String name, String value) {
        if (!name.matches(Node.TAG_NAME_REGEX))
            throw new NotValidTagNameException(name);
        getAttributes().put(name, StringUtils.removeQuotes(value));
        return (T) this;
    }

    /**
     * Un set attribute t.
     *
     * @param name the name
     * @return the t
     */
    default T unsetAttribute(@NotNull String name) {
        getAttributes().remove(name);
        return (T) this;
    }

    /**
     * Gets attribute.
     *
     * @param name the name
     * @return the attribute
     */
    default String getAttribute(String name) {
        return getAttributes().get(name);
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     * @return the attributes
     */
    default T setAttributes(String @Nullable ... attributes) {
        if (attributes != null && attributes.length > 1)
            for (int i = 0; i < attributes.length; i += 2)
                setAttribute(attributes[i], attributes[i + 1]);
        return (T) this;
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     * @return the attributes
     */
    default T setAttributes(@Nullable Map<String, String> attributes) {
        getAttributes().clear();
        if (attributes != null) attributes.forEach(this::setAttribute);
        return (T) this;
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    Map<String, String> getAttributes();
}
