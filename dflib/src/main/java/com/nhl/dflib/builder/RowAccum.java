package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;

/**
 * @since 0.16
 */
interface RowAccum<S> extends ValueStore<S> {

    DataFrame toDataFrame();
}
