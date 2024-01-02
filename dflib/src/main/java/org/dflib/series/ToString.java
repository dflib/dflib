package org.dflib.series;

import org.dflib.Printers;
import org.dflib.Series;

class ToString {

    static String toString(Series<?> series) {
        String name = series.getClass().getSimpleName();
        StringBuilder buffer = new StringBuilder(name).append(" [");
        Printers.inline.print(buffer, series);
        return buffer.append("]").toString();
    }
}
