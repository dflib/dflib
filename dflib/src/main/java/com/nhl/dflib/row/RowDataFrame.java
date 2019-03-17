package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class RowDataFrame implements DataFrame {

    private Index columns;
    private List<Object[]> rows;

    public RowDataFrame(Index columns, List<Object[]> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    /**
     * Creates a DataFrame by folding the provided stream of objects into rows and columns row by row.
     */
    public static <T> DataFrame fromStreamFoldByRow(Index columns, Stream<T> stream) {

        int width = columns.size();
        if (width == 0) {
            throw new IllegalArgumentException("Empty columns");
        }

        List<Object[]> folded = new ArrayList<>();
        Iterator<T> it = stream.iterator();

        int i = 0;
        Object[] row = null;

        while (it.hasNext()) {

            // first iteration
            if (row == null) {
                row = new Object[width];
            }
            // previous row finished
            else if (i % width == 0) {
                folded.add(row);
                row = new Object[width];
            }

            row[i % width] = it.next();
            i++;
        }

        // add last row
        folded.add(row);

        return new RowDataFrame(columns, folded);
    }

    /**
     * Creates a DataFrame by folding the provided array of objects into rows and columns row by row.
     */
    public static DataFrame fromSequenceFoldByRow(Index columns, Object... sequence) {

        int width = columns.size();
        int rows = sequence.length / width;

        List<Object[]> folded = new ArrayList<>(rows + 1);
        for (int i = 0; i < rows; i++) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, i * width, row, 0, width);
            folded.add(row);
        }

        // copy partial last row
        int leftover = sequence.length % width;
        if (leftover > 0) {
            Object[] row = new Object[width];
            System.arraycopy(sequence, rows * width, row, 0, leftover);
            folded.add(row);
        }

        return new RowDataFrame(columns, folded);
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
        return rows.size();
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return RowIterator.overArrays(columns, rows);
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("RowDataFrame ["), this).append("]").toString();
    }
}
