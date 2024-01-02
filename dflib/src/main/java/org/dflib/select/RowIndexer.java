package org.dflib.select;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.RowPredicate;
import org.dflib.builder.IntAccum;

public class RowIndexer {

    public static IntSeries index(DataFrame source, RowPredicate p) {

        IntAccum index = new IntAccum();
        int[] i = new int[1];
        source.forEach(rp -> {
            if (p.test(rp)) {
                index.pushInt(i[0]);
            }

            i[0]++;
        });

        return index.toSeries();
    }
}
