package com.nhl.dflib.sort;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.DataFrameRowProxy;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.ArraySeries;
import com.nhl.dflib.series.IndexedSeries;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;

public class IndexSorter {

    private DataFrame dataFrame;
    private Supplier<Integer[]> indexBuilder;

    public IndexSorter(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
        this.indexBuilder = () -> rowNumberSequence(dataFrame.height());
    }

    public IndexSorter(DataFrame dataFrame, Series<Integer> rangeToSort) {
        this.dataFrame = dataFrame;

        // copy range to avoid modification of the source list
        this.indexBuilder = () -> {
            int len = rangeToSort.size();
            Integer[] data = new Integer[len];
            rangeToSort.copyTo(data, 0, 0, len);
            return data;
        };
    }

    protected static Integer[] rowNumberSequence(int h) {
        Integer[] rn = new Integer[h];
        for (int i = 0; i < h; i++) {
            rn[i] = i;
        }

        return rn;
    }

    public Series<Integer> sortIndex(Comparator<RowProxy> comparator) {
        // make sure 'mutableIndex' is not visible outside this method as we are going to modify it,
        // so obtain it via the supplier right on the spot
        Integer[] mutableIndex = indexBuilder.get();

        Comparator<Integer> rowComparator = rowIndexComparator(comparator);

        // note - mutating passed index
        Arrays.sort(mutableIndex, rowComparator);
        return new ArraySeries<>(mutableIndex);
    }

    public DataFrame sort(Comparator<RowProxy> comparator) {
        Series<Integer> sortedIndex = sortIndex(comparator);

        int width = dataFrame.width();
        Series<?>[] newColumnsData = new Series[width];
        for (int i = 0; i < width; i++) {
            newColumnsData[i] = new IndexedSeries<>(dataFrame.getColumn(i), sortedIndex);
        }

        return new ColumnDataFrame(dataFrame.getColumnsIndex(), newColumnsData);
    }

    private Comparator<Integer> rowIndexComparator(Comparator<RowProxy> rowComparator) {
        DataFrameRowProxy p1 = new DataFrameRowProxy(dataFrame);
        DataFrameRowProxy p2 = new DataFrameRowProxy(dataFrame);
        return (i1, i2) -> rowComparator.compare(p1.rewind(i1), p2.rewind(i2));
    }
}