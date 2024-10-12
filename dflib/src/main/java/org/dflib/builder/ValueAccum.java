package org.dflib.builder;

import org.dflib.Series;

/**
 * A mutable Series builder with API to create primitive and Object Series.

 */
public interface ValueAccum<T> extends ValueStore<T> {

    Series<T> toSeries();

    int size();
}
