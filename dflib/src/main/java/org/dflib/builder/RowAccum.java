package org.dflib.builder;

import org.dflib.DataFrame;

/**
 * A mutable row-by-row DataFrame builder.
 *
 * @since 0.16
 */
interface RowAccum<S> {

    void push(S rowSource);

    void replace(int pos, S rowSource);

    DataFrame toDataFrame();
}
