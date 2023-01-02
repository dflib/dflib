package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;

/**
 * A mutable DataFrame builder.
 *
 * @since 0.16
 */
interface RowAccum<S> extends ValueStore<S> {

    DataFrame toDataFrame();
}
