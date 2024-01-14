package org.dflib;

/**
 * A column set within a {@link RowSet}.
 *
 * @since 1.0.0-M19
 */
public interface RowColumnSet {

    DataFrame map(Exp<?>... exps);

    DataFrame map(RowMapper mapper);

    DataFrame map(RowToValueMapper<?>... mappers);

    DataFrame select();

    DataFrame select(Exp<?>... exps);

    DataFrame select(RowMapper mapper);

    DataFrame select(RowToValueMapper<?>... mappers);
}
