package com.nhl.dflib.concat;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.ArrayRowBuilder;
import com.nhl.dflib.row.ArrayRowProxy;
import com.nhl.dflib.row.RowProxy;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;

public class HConcatDataFrame implements DataFrame {

    private JoinType joinType;
    private DataFrame leftSource;
    private DataFrame rightSource;
    private Index columns;
    private RowCombiner rowCombiner;

    public HConcatDataFrame(
            Index columns,
            JoinType joinType,
            DataFrame leftSource,
            DataFrame rightSource,
            RowCombiner rowCombiner) {

        this.joinType = joinType;
        this.leftSource = leftSource;
        this.rightSource = rightSource;
        this.columns = columns;
        this.rowCombiner = rowCombiner;
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public int height() {
        return leftSource.height();
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return new Iterator<RowProxy>() {

            final Iterator<RowProxy> leftIt = leftSource.iterator();
            final Iterator<RowProxy> rightIt = rightSource.iterator();

            final ArrayRowBuilder rowBuilder = new ArrayRowBuilder(columns);
            final ArrayRowProxy rowProxy = new ArrayRowProxy(columns);

            BiPredicate<RowProxy, RowProxy> hasNext = buildPredicate();
            RowProxy nextL;
            RowProxy nextR;

            {
                rewind();
            }

            private void rewind() {
                nextL = leftIt.hasNext() ? leftIt.next() : null;
                nextR = rightIt.hasNext() ? rightIt.next() : null;
            }

            private BiPredicate<RowProxy, RowProxy> buildPredicate() {
                switch (joinType) {
                    case full:
                        return (l, r) -> l != null || r != null;
                    case right:
                        return (l, r) -> r != null;
                    case left:
                        return (l, r) -> l != null;
                    case inner:
                        return (l, r) -> l != null && r != null;
                    default:
                        throw new IllegalStateException("Unsupported join type: " + joinType);
                }
            }

            @Override
            public boolean hasNext() {
                return hasNext.test(nextL, nextR);
            }

            @Override
            public RowProxy next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator");
                }

                RowProxy o = transform(nextL, nextR);
                rewind();
                return o;
            }

            private RowProxy transform(RowProxy leftR, RowProxy rightR) {
                rowCombiner.combine(leftR, rightR, rowBuilder);
                return rowProxy.reset(rowBuilder.reset());
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("HConcatDataFrame ["), this).append("]").toString();
    }
}
