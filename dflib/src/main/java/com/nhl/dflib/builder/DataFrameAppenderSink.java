package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;

/**
 * @since 0.16
 */
interface DataFrameAppenderSink<S> {

    void append(S rowSource);

    void replace(S from, int toPos);

    DataFrame toDataFrame();
}
