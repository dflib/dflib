package org.dflib.ql;

import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;

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

    private final QLFunctions functions;

    QLContext(QLFunctions functions) {
        this.functions = functions;
    }

    /**
     * Starts a builder of a context that includes the built-in functions, unless {@link Builder#noDefaultFunctions()}
     * is called.
     */
    public static Builder builder() {
        return new Builder(QLFunctions.builder());
    }

    /**
     * Returns the registry of the functions recognized by the parser in this context.
     */
    public QLFunctions functions() {
        return functions;
    }

    /**
     * A builder of a {@link QLContext}. A function is registered as a {@link QLFunction} class, or as a
     * {@code Udf0..UdfN} object; a lambda needs a type to be reflected on, so it is registered via
     * {@code Udf1.of(e -> ..)}, {@code Udf2.of(..)}, etc.
     */
    public static final class Builder {

        private final QLFunctions.Builder functions;

        private Builder(QLFunctions.Builder functions) {
            this.functions = functions;
        }

        public Builder function(String name, Udf0<?> function) {
            functions.function(name, function);
            return this;
        }

        public Builder function(String name, Udf1<?, ?> function) {
            functions.function(name, function);
            return this;
        }

        public Builder function(String name, Udf2<?, ?, ?> function) {
            functions.function(name, function);
            return this;
        }

        public Builder function(String name, Udf3<?, ?, ?, ?> function) {
            functions.function(name, function);
            return this;
        }

        public Builder function(String name, UdfN<?> function) {
            functions.function(name, function);
            return this;
        }

        /**
         * Registers every public {@code call} overload of a {@link QLFunction} class as an overload of the function.
         */
        public Builder function(String name, QLFunction function) {
            functions.function(name, function);
            return this;
        }

        /**
         * Excludes the built-in functions from the context, leaving only the explicitly registered ones.
         */
        public Builder noDefaultFunctions() {
            functions.noDefaultFunctions();
            return this;
        }

        public QLContext build() {
            return new QLContext(functions.build());
        }
    }
}
