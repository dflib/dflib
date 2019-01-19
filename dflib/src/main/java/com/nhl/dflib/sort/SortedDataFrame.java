package com.nhl.dflib.sort;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.MaterializedDataFrame;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedDataFrame<T> extends MaterializedDataFrame {

    private Comparator<Object[]> rowComparator;

    public SortedDataFrame(DataFrame source, Comparator<Object[]> rowComparator) {
        super(source);
        this.rowComparator = rowComparator;
    }

    @Override
    protected List<Object[]> doMaterialize() {
        List<Object[]> list = super.doMaterialize();
        Collections.sort(list, rowComparator);
        return list;
    }
}
