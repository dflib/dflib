package com.nhl.dflib.concat;

import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.map.RowCombiner;

public class HConcat {

    private static final RowCombiner ROW_CONCATENATOR = (c, lr, rr) -> c.copyToTarget(lr, rr, 0, c.getLeftIndex().size());

    public static Index zipIndex(Index leftIndex, Index rightIndex) {

        int llen = leftIndex.size();
        int rlen = rightIndex.size();

        IndexPosition[] lPositions = leftIndex.getPositions();
        IndexPosition[] rPositions = rightIndex.getPositions();

        // zipped index is continuous to match rowZipper algorithm below that rebuilds the arrays, so reset left and
        // right positions, only preserve the names...

        IndexPosition[] zipped = new IndexPosition[llen + rlen];
        for (int i = 0; i < llen; i++) {
            zipped[i] = new IndexPosition(i, i, lPositions[i].name());
        }

        // resolve dupes on the right
        for (int i = 0; i < rlen; i++) {

            String name = rPositions[i].name();
            while (leftIndex.hasName(name)) {
                name = name + "_";
            }

            int ri = i + llen;
            zipped[ri] = new IndexPosition(ri, ri, name);
        }

        return Index.withPositions(zipped);
    }

    public static RowCombiner concatenator() {
        return ROW_CONCATENATOR;
    }
}
