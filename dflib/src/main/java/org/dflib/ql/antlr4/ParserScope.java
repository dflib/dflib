package org.dflib.ql.antlr4;

import org.antlr.v4.runtime.Token;
import org.dflib.Exp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.ql.QLContext;
import org.dflib.ql.QLParserException;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * The state of a single parser run: the context to resolve the functions, and the values bound to the {@code ?} and
 * {@code ?f} placeholders, handed out in the order of the placeholders.
 */
public class ParserScope {

    private final QLContext context;
    private final Object[] params;
    private int paramsUsed;

    public ParserScope(QLContext context, Object[] params) {
        this.context = context;
        this.params = params;
    }

    /**
     * Returns the value bound to the placeholder at the token position.
     */
    Object nextParam(Token placeholder) {
        if (paramsUsed >= params.length) {
            throw new QLParserException("Parameter '" + placeholder.getText() + "' at " + position(placeholder)
                    + " has no value: " + params.length + " parameter(s) bound, at least " + (paramsUsed + 1) + " used");
        }

        return params[paramsUsed++];
    }

    /**
     * Returns the value bound to the placeholder as an array, accepting either an array or a collection.
     */
    Object[] nextParamArray(Token placeholder) {
        Object next = nextParam(placeholder);

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
     * call: a {@code Udf0..UdfN}.
     */
    Object nextParamFunction(Token placeholder) {
        Object next = nextParam(placeholder);

        return switch (next) {
            case Udf0<?> f -> f;
            case Udf1<?, ?> f -> f;
            case Udf2<?, ?, ?> f -> f;
            case Udf3<?, ?, ?, ?> f -> f;
            case UdfN<?> f -> f;
            case null, default -> throw new QLParserException("Parameter '" + placeholder.getText() + "' at "
                    + position(placeholder) + " is used as a function, so its value must be a Udf0..UdfN object, got: "
                    + typeName(next));
        };
    }

    /**
     * Resolves a function call against the context and builds its expression. Resolution and argument errors, and
     * anything thrown by the function body, are reported as a {@link QLParserException} positioned at the call.
     */
    Exp<?> fn(Token name, List<Exp<?>> args) {
        return call(name, () -> context.call(name.getText(), args));
    }

    /**
     * Builds a call of a function returned by {@link #nextParamFunction(Token)}, with the same error reporting as
     * {@link #fn(Token, List)}.
     */
    Exp<?> udf(Token placeholder, Object function, List<Exp<?>> args) {
        return call(placeholder, () -> QLContext.callUdf(function, args));
    }

    private static Exp<?> call(Token name, Supplier<Exp<?>> call) {
        try {
            return call.get();
        } catch (QLParserException e) {
            // a nested call already reported its own position
            throw e;
        } catch (RuntimeException e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            throw new QLParserException("Function '" + name.getText() + "' at " + position(name) + ": " + message, e);
        }
    }

    private static String typeName(Object value) {
        return value != null ? value.getClass().getName() : "null";
    }

    private static String position(Token token) {
        return token.getLine() + ":" + token.getCharPositionInLine();
    }
}
