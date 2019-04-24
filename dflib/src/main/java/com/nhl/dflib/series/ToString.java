package com.nhl.dflib.series;

import com.nhl.dflib.Printers;
import com.nhl.dflib.Series;

class ToString {

    static String toString(Series<?> series) {
        String name = series.getClass().getSimpleName();
        StringBuilder buffer = new StringBuilder(name).append(" [");
        Printers.inline.print(buffer, series);
        return buffer.append("]").toString();
    }
}
