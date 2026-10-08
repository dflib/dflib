package org.dflib.ql.antlr4;

import org.antlr.v4.runtime.Token;
import org.dflib.ql.QLParserException;

import java.util.Collection;

/**
 * Hands out the values bound to the {@code ?} placeholders of an expression, in the order of the placeholders.
 * A placeholder with no value, or a value of a shape the placeholder can not take, is reported as a positioned
 * {@link QLParserException}.
 */
public class PositionalParamSource {

    private final Object[] data;
    private int idx;

    public PositionalParamSource(Object[] data) {
        this.data = data;
    }

    /**
     * Returns the value bound to the placeholder at the token position.
     */
    Object next(Token placeholder) {
        if (idx >= data.length) {
            throw new QLParserException("Parameter '?' at " + position(placeholder) + " has no value: "
                    + data.length + " parameter(s) bound, at least " + (idx + 1) + " used");
        }

        return data[idx++];
    }

    /**
     * Returns the value bound to the placeholder as an array, accepting either an array or a collection.
     */
    Object[] nextArray(Token placeholder) {
        Object next = next(placeholder);

        return switch (next) {
            case Collection<?> c -> c.toArray();
            case Object[] a -> a;
            case null, default -> throw new QLParserException("Parameter '?' at " + position(placeholder)
                    + " is used as a list, so its value must be an array or a collection, got: "
                    + (next != null ? next.getClass().getName() : "null"));
        };
    }

    private static String position(Token token) {
        return token.getLine() + ":" + token.getCharPositionInLine();
    }
}
