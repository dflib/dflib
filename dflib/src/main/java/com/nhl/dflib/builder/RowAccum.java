package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;

/**
 * A mutable row-by-row DataFrame builder.
 *
 * @since 0.16
 */
interface RowAccum<S> {

    default void push(S rowSource) {
        throw new UnsupportedOperationException("No support pushing rows");
    }

    default void replace(int pos, S rowSource) {
        throw new UnsupportedOperationException("No support for setting positional values");
    }

    DataFrame toDataFrame();
}
