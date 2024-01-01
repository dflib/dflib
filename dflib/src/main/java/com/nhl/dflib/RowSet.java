package com.nhl.dflib;

/**
 * @since 1.0.0-M19
 */
public interface RowSet {

    DataFrame explode(String columnName);

    DataFrame explode(int columnPos);

    DataFrame map(Exp<?>... exps);

    DataFrame map(RowMapper mapper);

    DataFrame map(RowToValueMapper<?>... mappers);

    DataFrame sort(Sorter... sorters);

    DataFrame unique();

    DataFrame unique(String... columnNamesToCompare);

    DataFrame unique(int... columnPositionsToCompare);

    DataFrame select();
}
