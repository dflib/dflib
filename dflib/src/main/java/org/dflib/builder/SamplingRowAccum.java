package org.dflib.builder;

import org.dflib.DataFrame;
import org.dflib.IntSeries;

import java.util.Random;

/**
 * @since 0.16
 */
class SamplingRowAccum<S> implements RowAccum<S> {

    private final RowAccum<S> delegate;
    private final int rowSampleSize;
    private final Random rowsSampleRandom;
    private final IntAccum sampledRows;

    private int totalRowsRead;

    SamplingRowAccum(RowAccum<S> delegate, int rowSampleSize, Random rowsSampleRandom) {
        this.delegate = delegate;
        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = rowsSampleRandom;
        this.sampledRows = new IntAccum();
    }

    @Override
    public void push(S rowSource) {
        // Reservoir sampling algorithm per https://en.wikipedia.org/wiki/Reservoir_sampling

        // fill "reservoir" first
        if (totalRowsRead < rowSampleSize) {
            delegate.push(rowSource);
            sampledRows.pushInt(totalRowsRead);
        }
        // replace previously filled values based on random sampling with decaying probability
        else {
            int pos = rowsSampleRandom.nextInt(totalRowsRead + 1);
            if (pos < rowSampleSize) {
                delegate.replace(pos, rowSource);
                sampledRows.replaceInt(pos, totalRowsRead);
            }
        }

        totalRowsRead++;
    }

    @Override
    public void replace(int toPos, S rowSource) {
        delegate.replace(toPos, rowSource);
    }

    @Override
    public DataFrame toDataFrame() {
        IntSeries sortIndex = sampledRows.toSeries().sortIndexInt();
        return delegate.toDataFrame().selectRows(sortIndex);
    }
}
