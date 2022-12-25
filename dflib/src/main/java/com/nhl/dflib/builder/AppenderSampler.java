package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;

import java.util.Random;
import java.util.function.Consumer;

class AppenderSampler<S> {

    private final int rowSampleSize;
    private final Random rowsSampleRandom;
    private final IntAccum sampledRows;
    private final Consumer<S> appender;
    private final ObjIntConsumer<S> replacer;

    private int totalRowsRead;

    AppenderSampler(
            int rowSampleSize,
            Random rowsSampleRandom,
            Consumer<S> appender,
            ObjIntConsumer<S> replacer) {

        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = rowsSampleRandom;
        this.appender = appender;
        this.replacer = replacer;
        this.sampledRows = new IntAccum();
    }

    void append(S rowSource) {

        // Reservoir sampling algorithm per https://en.wikipedia.org/wiki/Reservoir_sampling

        // fill "reservoir" first
        if (totalRowsRead < rowSampleSize) {
            appender.accept(rowSource);
            sampledRows.pushInt(totalRowsRead);
        }
        // replace previously filled values based on random sampling with decaying probability
        else {
            int pos = rowsSampleRandom.nextInt(totalRowsRead + 1);
            if (pos < rowSampleSize) {
                replacer.accept(rowSource, pos);
                sampledRows.replaceInt(pos, totalRowsRead);
            }
        }

        totalRowsRead++;
    }

    DataFrame sortSampled(DataFrame sampledUnsorted) {
        IntSeries sortIndex = sampledRows.toSeries().sortIndexInt();
        return sampledUnsorted.selectRows(sortIndex);
    }

    interface ObjIntConsumer<S> {
        void accept(S s, int i);
    }
}
