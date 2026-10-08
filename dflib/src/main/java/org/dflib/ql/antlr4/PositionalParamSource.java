package org.dflib.ql.antlr4;

import org.antlr.v4.runtime.Token;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.ql.QLFunction;
import org.dflib.ql.QLParserException;

import java.util.Collection;

/**
 * Hands out the values bound to the {@code ?} and {@code ?f} placeholders of an expression,
 * in the order of the placeholders.
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
            throw new QLParserException("Parameter '" + placeholder.getText() + "' at " + position(placeholder)
                    + " has no value: " + data.length + " parameter(s) bound, at least " + (idx + 1) + " used");
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
            case null, default -> throw new QLParserException("Parameter '" + placeholder.getText() + "' at "
                    + position(placeholder) + " is used as a list, so its value must be an array or a collection, got: "
                    + typeName(next));
        };
    }

    /**
     * Returns the value bound to the {@code ?f} placeholder, checking that it is a function object the parser can
     * call: a {@code Udf0..UdfN} or a {@link QLFunction}.
     */
    Object nextFunction(Token placeholder) {
        Object next = next(placeholder);

        return switch (next) {
            case QLFunction f -> f;
            case Udf0<?> f -> f;
            case Udf1<?, ?> f -> f;
            case Udf2<?, ?, ?> f -> f;
            case Udf3<?, ?, ?, ?> f -> f;
            case UdfN<?> f -> f;
            case null, default -> throw new QLParserException("Parameter '" + placeholder.getText() + "' at "
                    + position(placeholder) + " is used as a function, so its value must be a Udf0..UdfN or a"
                    + " QLFunction object, got: " + typeName(next));
        };
    }

    private static String typeName(Object value) {
        return value != null ? value.getClass().getName() : "null";
    }

    private static String position(Token token) {
        return token.getLine() + ":" + token.getCharPositionInLine();
    }
}
