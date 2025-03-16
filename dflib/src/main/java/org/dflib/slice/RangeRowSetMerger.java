package org.dflib.slice;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.collection.JavaArrays;
import org.dflib.range.Range;
import org.dflib.series.IntSequenceSeries;

class RangeRowSetMerger extends RowSetMerger {

    private final int fromInclusive;
    private final int toExclusive;
    private final int srcLen;

    // Implementation note: only works for non-resizing Series, so expansion or contraction operations
    // are delegated to a DefaultRowMergeSetIndex.
    // Caching the delegate, as "explodeRows" and "stretchRows" are called in a rapid succession on the same instance
    private volatile RowSetMerger delegate;

    RangeRowSetMerger(int srcLen, int fromInclusive, int toExclusive) {

        Range.checkRange(fromInclusive, toExclusive - fromInclusive, srcLen);

        this.fromInclusive = fromInclusive;
        this.toExclusive = toExclusive;
        this.srcLen = srcLen;
    }

    @Override
    public <T> Series<T> merge(Series<T> srcColumn, Series<T> rsColumn) {

        int rangeLen = toExclusive - fromInclusive;

        if (rangeLen != rsColumn.size()) {
            throw new IllegalArgumentException("Unexpected row set length: " + rsColumn.size() + " (expected " + rangeLen + ")");
        }

        if (rangeLen == 0) {
            return srcColumn;
        }

        // TODO: primitive Series
        T[] values = JavaArrays.newArray(rsColumn.getNominalType(), srcLen);

        if (fromInclusive > 0) {
            srcColumn.copyTo(values, 0, 0, fromInclusive);
        }

        rsColumn.copyTo(values, 0, fromInclusive, rangeLen);

        if (toExclusive < srcLen) {
            srcColumn.copyTo(values, toExclusive, toExclusive, srcLen - toExclusive);
        }

        return Series.of(values);
    }

    @Override
    public RowSetMerger removeUnmatchedRows(BooleanSeries rsCondition) {
        return delegate().removeUnmatchedRows(rsCondition);
    }

    @Override
    public RowSetMerger expandCols(ColumnExpander expander) {
        return delegate().expandCols(expander);
    }

    private RowSetMerger delegate() {
        if (delegate == null) {
            delegate = RowSetMerger.of(srcLen, new IntSequenceSeries(fromInclusive, toExclusive));
        }

        return delegate;
    }
}
