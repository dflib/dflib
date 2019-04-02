package com.nhl.dflib.print;

import com.nhl.dflib.Series;

public abstract class SeriesPrintWorker {

    protected StringBuilder out;
    protected int maxDisplayColumnWidth;
    protected int maxDisplayValues;

    SeriesPrintWorker(StringBuilder out, int maxDisplayValues, int maxDisplayColumnWidth) {
        this.out = out;
        this.maxDisplayColumnWidth = maxDisplayColumnWidth;
        this.maxDisplayValues = maxDisplayValues;
    }

    protected abstract StringBuilder print(Series<?> s);

}
