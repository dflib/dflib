package org.dflib;

import org.dflib.exp.Exps;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A column set within a {@link RowSet}.
 */
public interface RowColumnSet {

    /**
     * @deprecated use {@link #merge(Exp[])}
     */
    @Deprecated(since = "1.0.0-RC2", forRemoval = true)
    default DataFrame map(Exp<?>... exps) {
        return merge(exps);
    }

    /**
     * @deprecated use {@link #merge(RowMapper)} 
     */
    @Deprecated(since = "1.0.0-RC2", forRemoval = true)
    default DataFrame map(RowMapper mapper) {
        return merge(mapper);
    }
    
    /**
     * @deprecated use {@link #merge(RowToValueMapper[])}
     */
    @Deprecated(since = "1.0.0-RC2", forRemoval = true)
    default DataFrame map(RowToValueMapper<?>... mappers) {
        return merge(mappers);
    }

    /**
     * @since 2.0.0
     */
    DataFrame merge();

    /**
     * Parses String arguments into an array of expressions and then applies row merge operation with those expressions
     * transforming RowColumnSet column data.
     *
     * @since 2.0.0
     */
    default DataFrame merge(String... exps) {
        return merge(Exps.asExps(exps));
    }

    DataFrame merge(Exp<?>... exps);

    DataFrame merge(RowMapper mapper);

    DataFrame merge(RowToValueMapper<?>... mappers);

    /**
     * Returns the original DataFrame with rows and columns matched by this RowColumnSet removed.
     */
    DataFrame drop();

    DataFrame select();


    /**
     * Parses String arguments into an array of expressions and then applies select operation with those expressions
     * transforming RowColumnSet column data.
     *
     * @since 2.0.0
     */
    default DataFrame select(String... exps) {
        return select(Exps.asExps(exps));
    }

    DataFrame select(Exp<?>... exps);

    DataFrame select(RowMapper mapper);

    DataFrame select(RowToValueMapper<?>... mappers);

    /**
     * A form of {@link #select()} that also renames the result columns using the provided operator.
     */
    DataFrame selectAs(UnaryOperator<String> renamer);

    /**
     * A form of {@link #select()} that also renames the result columns.
     */
    DataFrame selectAs(String... newColumnNames);

    /**
     * A form of {@link #select()} that also renames the result columns using the provided old to new names map.
     */
    DataFrame selectAs(Map<String, String> oldToNewNames);
}
