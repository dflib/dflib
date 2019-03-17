package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortedRowDataFrame<T> extends MaterializableRowDataFrame {

    private Comparator<Object[]> rowComparator;

    public SortedRowDataFrame(DataFrame source, Comparator<Object[]> rowComparator) {
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
