package org.dflib;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A column set within a {@link RowSet}.
 *
 * @since 1.0.0-M19
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
     * @since 1.0.0-RC2
     */
    DataFrame merge(Exp<?>... exps);

    /**
     * @since 1.0.0-RC2
     */
    DataFrame merge(RowMapper mapper);

    /**
     * @since 1.0.0-RC2
     */
    DataFrame merge(RowToValueMapper<?>... mappers);

    /**
     * Returns the original DataFrame with rows and columns matched by this RowColumnSet removed.
     */
    DataFrame drop();

    DataFrame select();

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
