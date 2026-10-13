package org.dflib.ql;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.Sorter;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.ql.antlr4.ExpParser;

/**
 * A context of the QL parser, such as the functions recognized in the expressions. The parsing methods of the context
 * produce expressions and sorters to be passed to the DataFrame and Series methods, e.g.:
 *
 * <pre>{@code
 * QLContext context = Environment.commonEnv().qlContext().extend()
 *         .function("myOp", Udf2.of(this::op))
 *         .build();
 *
 * df.cols().select(context.parseExps("myOp(a, b) > ?", 5));
 * }</pre>
 *
 * <p>The QL String methods of DataFrame, Series, etc. use the common context from
 * {@link org.dflib.Environment#qlContext()}. A context is immutable and can be shared between threads.
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
     * Starts a builder of a context layered on top of this context. The new context will have all the functions of
     * this context, plus the functions added to the builder. A function added with the same name and argument types
     * as a function of this context replaces it. The new context is a snapshot, and is not affected by the
     * changes to this context made later (e.g. if this is the {@link org.dflib.Environment} common context).
     */
    public Builder extend() {
        return new Builder(functions.extend());
    }

    /**
     * Returns the registry of the functions recognized by the parser in this context.
     */
    public QLFunctions functions() {
        return functions;
    }

    /**
     * Parses the provided String into an expression.
     *
     * @param ql     a QL String to parse representing a single expression
     * @param params an optional list of parameters to substitute into the parsed expression
     */
    public Exp<?> parseExp(String ql, Object... params) {
        return QLParserInvoker.parse(this, ql, ExpParser::expRoot, params).exp;
    }

    /**
     * Parses the provided String into an array of expressions.
     *
     * @param ql     a QL String to parse representing one or more comma-separated expressions
     * @param params an optional list of parameters to substitute into the parsed expressions
     */
    public Exp<?>[] parseExps(String ql, Object... params) {
        return QLParserInvoker.parse(this, ql, ExpParser::expArray, params).expressions;
    }

    /**
     * Parses the provided String into a condition. An expression that is not boolean is converted to a condition via
     * {@link Exp#castAsBool()}.
     *
     * @param ql     a QL String to parse representing a single expression
     * @param params an optional list of parameters to substitute into the parsed expression
     */
    public Condition parseCondition(String ql, Object... params) {
        return parseExp(ql, params).castAsBool();
    }

    /**
     * Parses the provided sort expression String into a sorter.
     *
     * @param ql     a QL String to parse representing a sort expression
     * @param params an optional list of parameters to substitute into the parsed sorter
     */
    public Sorter parseSorter(String ql, Object... params) {
        return QLParserInvoker.parse(this, ql, ExpParser::sorterRoot, params).sorter;
    }

    /**
     * Parses the provided sort expression String into an array of sorters.
     *
     * @param ql     a QL String to parse representing one or more comma-separated sort expressions
     * @param params an optional list of parameters to substitute into the parsed sorters
     */
    public Sorter[] parseSorters(String ql, Object... params) {
        return QLParserInvoker.parse(this, ql, ExpParser::sorterArray, params).sorters;
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
         *
         * @throws IllegalStateException if called on a builder created via {@link QLContext#extend()}
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
