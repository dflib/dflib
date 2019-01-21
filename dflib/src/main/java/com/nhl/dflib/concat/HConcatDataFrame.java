package com.nhl.dflib.concat;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.CombineContext;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.print.InlinePrinter;

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
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {

            final CombineContext context = new CombineContext(leftSource.getColumns(), rightSource.getColumns(), columns);
            final Iterator<Object[]> leftIt = HConcatDataFrame.this.leftSource.iterator();
            final Iterator<Object[]> rightIt = HConcatDataFrame.this.rightSource.iterator();

            BiPredicate<Object[], Object[]> hasNext = buildPredicate();
            Object[] nextL;
            Object[] nextR;

            {
                rewind();
            }

            private void rewind() {
                nextL = leftIt.hasNext() ? leftIt.next() : null;
                nextR = rightIt.hasNext() ? rightIt.next() : null;
            }

            private BiPredicate<Object[], Object[]> buildPredicate() {
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
            public Object[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator");
                }

                Object[] o = transform(nextL, nextR);
                rewind();
                return o;
            }

            private Object[] transform(Object[] leftR, Object[] rightR) {
                return rowCombiner.combine(context, leftR, rightR);
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("HConcatDataFrame ["), this).append("]").toString();
    }
}
