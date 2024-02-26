package org.dflib.series;

import org.dflib.Series;
import org.dflib.Environment;

class ToString {

    static String toString(Series<?> series) {
        return Environment.commonEnv().printer().print(new StringBuilder(), series).toString();
    }
}
