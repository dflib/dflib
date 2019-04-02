package com.nhl.dflib.print;

import com.nhl.dflib.Series;

public class SeriesTabularPrintWorker extends SeriesPrintWorker {

    public SeriesTabularPrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    @Override
    protected StringBuilder print(Series<?> s) {
        throw new UnsupportedOperationException("TODO");
    }
}
