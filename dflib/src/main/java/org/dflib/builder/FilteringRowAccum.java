package org.dflib.builder;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.RowPredicate;
import org.dflib.row.ValueHolderRowProxy;

/**
 * @since 0.16
 */
class FilteringRowAccum<S> implements RowAccum<S> {

    private final RowAccum<S> delegate;
    private final RowPredicate filter;

    // TODO: using ValueHolderProxy results in double conversion of values - once here during filtering, and the second
    //  time when we accumulate the values in the delegate. It wasn't easy to separate the extraction phase and keep all
    //  the accum efficiencies for non-filtering cases

    private final ValueHolderRowProxy rowProxy;

    FilteringRowAccum(RowAccum<S> delegate, RowPredicate filter, Index index, Extractor<S, ?>[] extractors) {
        this.delegate = delegate;
        this.filter = filter;
        this.rowProxy = new ValueHolderRowProxy(index, extractors);
    }

    @Override
    public void push(S rowSource) {
        rowProxy.reset(rowSource);
        if (filter.test(rowProxy)) {
            delegate.push(rowSource);
        }
    }

    @Override
    public void replace(int pos, S rowSource) {
        rowProxy.reset(rowSource);
        if (filter.test(rowProxy)) {
            delegate.replace(pos, rowSource);
        }
    }

    @Override
    public DataFrame toDataFrame() {
        return delegate.toDataFrame();
    }
}
