package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;

import java.util.Random;

/**
 * @since 0.16
 */
class DataFrameSamplingAppenderSink<S> implements DataFrameAppenderSink<S> {

    private final DataFrameAppenderSink<S> delegate;
    private final int rowSampleSize;
    private final Random rowsSampleRandom;
    private final IntAccum sampledRows;

    private int totalRowsRead;

    DataFrameSamplingAppenderSink(DataFrameAppenderSink<S> delegate, int rowSampleSize, Random rowsSampleRandom) {
        this.delegate = delegate;
        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = rowsSampleRandom;
        this.sampledRows = new IntAccum();
    }

    @Override
    public void append(S rowSource) {
        // Reservoir sampling algorithm per https://en.wikipedia.org/wiki/Reservoir_sampling

        // fill "reservoir" first
        if (totalRowsRead < rowSampleSize) {
            delegate.append(rowSource);
            sampledRows.pushInt(totalRowsRead);
        }
        // replace previously filled values based on random sampling with decaying probability
        else {
            int pos = rowsSampleRandom.nextInt(totalRowsRead + 1);
            if (pos < rowSampleSize) {
                delegate.replace(rowSource, pos);
                sampledRows.replaceInt(pos, totalRowsRead);
            }
        }

        totalRowsRead++;
    }

    @Override
    public void replace(S from, int toPos) {
        delegate.replace(from, toPos);
    }

    @Override
    public DataFrame toDataFrame() {
        IntSeries sortIndex = sampledRows.toSeries().sortIndexInt();
        return delegate.toDataFrame().selectRows(sortIndex);
    }
}
