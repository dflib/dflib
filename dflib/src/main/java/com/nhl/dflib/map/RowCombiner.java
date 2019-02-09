package com.nhl.dflib.map;

@FunctionalInterface
public interface RowCombiner {

    static RowCombiner zip() {
        return (c, lr, rr, tr) -> {

            // rows can be null in case of outer joins...

            if (lr != null) {
                c.getLeftIndex().compactCopy(lr, tr, 0);
            }

            if (rr != null) {
                c.getRightIndex().compactCopy(rr, tr, c.getLeftIndex().size());
            }
        };
    }


    void combine(CombineContext context, Object[] lr, Object[] rr, Object[] tr);
}
