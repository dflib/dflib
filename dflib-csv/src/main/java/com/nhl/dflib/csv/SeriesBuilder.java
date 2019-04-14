package com.nhl.dflib.csv;

import com.nhl.dflib.Series;

interface SeriesBuilder<T> {

    void append(String csvValue);

    Series<T> toSeries();
}
