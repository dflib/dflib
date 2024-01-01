package com.nhl.dflib;

/**
 * A column set within a {@link RowSet}.
 *
 * @since 1.0.0-M19
 */
public interface RowColumnSet {

    DataFrame map(Exp<?>... exps);

    DataFrame map(RowMapper mapper);

    DataFrame map(RowToValueMapper<?>... mappers);
}
