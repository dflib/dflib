package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MaterializedDataFrame is a DataFrame wrapper that resolves and caches the underlying row collection on fisrt access.
 * It serves multiple purposes:
 * <ul>
 * <li>Speeds up multiple iterations of DataFrames with slow mapping functions by caching the .</li>
 * <li>Negates side effects on non-pure mapping functions on multiple iterations.</li>
 * <li>Allows to free memory taken by the delegate DataFrame upon materialization.</li>
 * </ul>
 */
public class MaterializableRowDataFrame implements DataFrame {

    private DataFrame source;

    private Index columns;
    private volatile List<Object[]> materialized;

    public MaterializableRowDataFrame(DataFrame source) {
        this(source.getColumns(), source);
    }

    public MaterializableRowDataFrame(Index columns, DataFrame source) {
        this.source = source;
        this.columns = columns;
    }

    @Override
    public DataFrame materialize() {
        return this;
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public int height() {
        return getMaterialized().size();
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return RowIterator.overArrays(columns, getMaterialized());
    }

    protected List<Object[]> getMaterialized() {

        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterialize();
                }
            }
        }

        return materialized;
    }

    protected List<Object[]> doMaterialize() {
        List<Object[]> materialized = new ArrayList<>();

        int len = Math.min(columns.span(), source.getColumns().span());
        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(columns);

        for(RowProxy r : source) {
            r.copyRange(rowBuilder, 0, 0, len);
            materialized.add(rowBuilder.reset());
        }

        // reset source reference, allowing to free up memory..
        source = null;

        return materialized;
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("MaterializableRowDataFrame ["), this).append("]").toString();
    }
}
