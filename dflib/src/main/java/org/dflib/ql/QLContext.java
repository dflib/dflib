package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;

import java.util.ArrayList;
import java.util.List;

/**
 * A context of the QL parser, such as the functions recognized in the expressions. The QL String methods of
 * DataFrame, Series, etc. use the common context from {@link org.dflib.Environment#qlContext()}, that can be replaced
 * with a custom one, e.g.:
 *
 * <pre>{@code
 * Environment.setQLContext(QLContext.builder()
 *         .function("myOp", Udf2.of(this::op))
 *         .build());
 * }</pre>
 *
 * <p>A context is immutable and can be shared between threads.
 *
 * @since 2.0.0
 */
public final class QLContext {

    final QLFunctions functions;

    QLContext(QLFunctions functions) {
        this.functions = functions;
    }

    /**
     * Starts a builder of a context that includes the built-in functions.
     */
    public static Builder builder() {
        Builder builder = new Builder();
        DefaultQLFunctions.register(builder);
        return builder;
    }

    /**
     * Resolves a function call by its name and number of arguments against the functions of this context, and
     * builds the call expression.
     *
     * @throws IllegalArgumentException if the function is unknown, no overload matches the arguments, or the
     *                                  matching overload rejects them
     */
    public Exp<?> call(String name, List<Exp<?>> args) {
        return functions.call(name, args);
    }

    /**
     * A builder of a {@link QLContext}. A function is registered as a {@code Udf0..UdfN} object. A lambda is
     * wrapped via {@code Udf1.of(e -> ..)}, {@code Udf2.of(..)}, etc., and its arguments are untyped.
     */
    public static final class Builder {

        private final List<QLFunctionDescriptor> functions = new ArrayList<>();

        private Builder() {
        }

        public Builder function(String name, Udf0<?> function) {
            functions.add(QLFunctionDescriptor.of(name, function));
            return this;
        }

        public Builder function(String name, Udf1<?, ?> function) {
            functions.add(QLFunctionDescriptor.of(name, function));
            return this;
        }

        public Builder function(String name, Udf2<?, ?, ?> function) {
            functions.add(QLFunctionDescriptor.of(name, function));
            return this;
        }

        public Builder function(String name, Udf3<?, ?, ?, ?> function) {
            functions.add(QLFunctionDescriptor.of(name, function));
            return this;
        }

        public Builder function(String name, UdfN<?> function) {
            functions.add(QLFunctionDescriptor.of(name, function));
            return this;
        }

        public QLContext build() {
            return new QLContext(new QLFunctions(functions));
        }
    }
}
