package com.nhl.dflib;

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
public class MaterializedDataFrame implements DataFrame {

    private DataFrame source;

    private Index columns;
    private volatile List<Object[]> materialized;

    public MaterializedDataFrame(DataFrame source) {
        this.source = source;
        this.columns = source.getColumns();
    }

    public MaterializedDataFrame(Index columns, List<Object[]> materialized) {
        this.columns = columns;
        this.materialized = materialized;
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
    public long height() {
        return getMaterialized().size();
    }

    @Override
    public Iterator<Object[]> iterator() {
        return getMaterialized().iterator();
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
        source.forEach(materialized::add);

        // reset source reference, allowing to free up memory..
        source = null;

        return materialized;
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("MaterializedDataFrame ["), this).append("]").toString();
    }
}
