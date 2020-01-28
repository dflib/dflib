package com.nhl.dflib.sort;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.DataFrameRowProxy;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.IntArraySeries;

import java.util.Comparator;
import java.util.function.Supplier;

public class IndexSorter {

    private DataFrame dataFrame;
    private Supplier<int[]> indexBuilder;

    public static <T> IntSeries sortIndex(Series<T> s, Comparator<? super T> comparator) {
        int[] mutableIndex = IndexSorter.rowNumberSequence(s.size());
        IntComparator intComparator =  (i1, i2) -> comparator.compare(s.get(i1), s.get(i2));
        IntTimSort.sort(mutableIndex, intComparator);
        return new IntArraySeries(mutableIndex);
    }

    public IndexSorter(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.indexBuilder = () -> rowNumberSequence(dataFrame.height());
    }

    public IndexSorter(DataFrame dataFrame, IntSeries rangeToSort) {
        this.dataFrame = dataFrame;

        // copy range to avoid modification of the source list
        this.indexBuilder = () -> {
            int len = rangeToSort.size();
            int[] data = new int[len];
            rangeToSort.copyToInt(data, 0, 0, len);
            return data;
        };
    }

    public static int[] rowNumberSequence(int h) {
        int[] rn = new int[h];
        for (int i = 0; i < h; i++) {
            rn[i] = i;
        }

        return rn;
    }

    public IntSeries sortIndex(Comparator<RowProxy> comparator) {
        // make sure 'mutableIndex' is not visible outside this method as we are going to modify it,
        // so obtain it via the supplier right on the spot
        int[] mutableIndex = indexBuilder.get();

        IntComparator rowComparator = rowIndexComparator(comparator);

        // note - mutating passed index
        IntTimSort.sort(mutableIndex, rowComparator);
        return new IntArraySeries(mutableIndex);
    }

    public DataFrame sort(Comparator<RowProxy> comparator) {
        IntSeries sortedIndex = sortIndex(comparator);

        int width = dataFrame.width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = dataFrame.getColumn(i).select(sortedIndex);
        }

        return new ColumnDataFrame(dataFrame.getColumnsIndex(), newColumnsData);
    }

    private IntComparator rowIndexComparator(Comparator<RowProxy> rowComparator) {
        DataFrameRowProxy p1 = new DataFrameRowProxy(dataFrame);
        DataFrameRowProxy p2 = new DataFrameRowProxy(dataFrame);
        return (i1, i2) -> rowComparator.compare(p1.rewind(i1), p2.rewind(i2));
    }
}