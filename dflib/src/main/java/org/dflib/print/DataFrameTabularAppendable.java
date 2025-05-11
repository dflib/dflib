package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.row.RowProxy;

import java.io.IOException;

class DataFrameTabularAppendable extends TabularAppendable {

    private final int maxCols;

    public DataFrameTabularAppendable(Appendable out, int maxRows, int maxCols, int maxColumnChars) {
        super(out, maxRows, maxColumnChars);
        this.maxCols = maxCols;
    }

    public void print(DataFrame df) throws IOException {

        if (df == null) {
            out.append("null");
            return;
        }

        int w = df.width();
        if (w > 0) {

            DataFrameTruncator rowTruncator = DataFrameTruncator.create(df, maxRows);

            SeriesTruncator<String> colTruncator = SeriesTruncator.create(df.getColumnsIndex(), maxCols);
            TabularSeriesSplit colsSplit = new TabularSeriesSplit(
                    makeStrings(df, rowTruncator.head, rowTruncator.tail, colTruncator.head),
                    makeStrings(df, rowTruncator.head, rowTruncator.tail, colTruncator.tail),
                    colTruncator.truncated
            );

            // since tabular printer is multiline, start with a line break to ensure logger-induced prefixes don't break
            // table alignment
            printNewLine();
            printHeader(colsSplit);
            printNewLine();
            printHeaderSeparator(colsSplit);
            printData(colsSplit, 1, rowTruncator.head.height());

            if (rowTruncator.truncated) {
                printRowsSeparator();
            }

            printData(colsSplit, 1 + rowTruncator.head.height(), rowTruncator.tail.height());
        }

        int h = df.height();

        String nameLabel = df.getName() != null ? "[" + df.getName() + "] " : "";
        String rowsLabel = h == 1 ? " row x " : " rows x ";
        String columnsLabel = w == 1 ? " column" : " columns";
        printNewLine();
        out.append(nameLabel)
                .append(Integer.toString(h)).append(rowsLabel)
                .append(Integer.toString(w)).append(columnsLabel);
    }

    private void printHeader(TabularSeriesSplit colsSplit) throws IOException {
        int lw = colsSplit.left.length;
        for (int i = 0; i < lw; i++) {
            if (i > 0) {
                out.append(" ");
            }
            colsSplit.left[i].printTo(out, 0);
        }

        if (colsSplit.truncated) {
            out.append(" ...");
        }

        int rw = colsSplit.right.length;
        for (int i = 0; i < rw; i++) {
            out.append(" ");
            colsSplit.right[i].printTo(out, 0);
        }
    }

    private void printHeaderSeparator(TabularSeriesSplit colsSplit) throws IOException {

        int lw = colsSplit.left.length;
        for (int i = 0; i < lw; i++) {
            if (i > 0) {
                out.append(" ");
            }
            colsSplit.left[i].printSeparatorTo(out);
        }

        if (colsSplit.truncated) {
            out.append("    ");
        }

        int rw = colsSplit.right.length;
        for (int i = 0; i < rw; i++) {
            out.append(" ");
            colsSplit.right[i].printSeparatorTo(out);
        }
    }

    private void printRowsSeparator() throws IOException {
        printNewLine();
        out.append("...");
    }

    private void printData(TabularSeriesSplit colsSplit, int offset, int len) throws IOException {
        int lw = colsSplit.left.length;
        int rw = colsSplit.right.length;

        for (int i = 0; i < len; i++) {
            printNewLine();
            for (int j = 0; j < lw; j++) {
                if (j > 0) {
                    out.append(" ");
                }

                colsSplit.left[j].printTo(out, offset);
            }

            if (colsSplit.truncated) {
                out.append(" ...");
            }

            for (int j = 0; j < rw; j++) {
                out.append(" ");
                colsSplit.right[j].printTo(out, offset);
            }

            offset++;
        }
    }

    private TabularColumnData[] makeStrings(DataFrame df, DataFrame top, DataFrame bottom, Series<String> columns) {

        int w = columns.size();
        if (w == 0) {
            return new TabularColumnData[0];
        }

        int[] positions = df.getColumnsIndex().positions(columns.toArray(new String[0]));

        TabularColumnData.Builder[] builders = new TabularColumnData.Builder[w];

        // "1" is the size of the header
        int workerH = 1 + top.height() + bottom.height();

        for (int i = 0; i < w; i++) {
            builders[i] = TabularColumnData
                    .builder(df.getColumn(i).getInferredType(), workerH, maxColumnChars)

                    // the first value is column label
                    .append(columns.get(i));
        }

        for (RowProxy p : top) {
            for (int i = 0; i < w; i++) {
                builders[i].append(p.get(positions[i]));
            }
        }

        for (RowProxy p : bottom) {
            for (int i = 0; i < w; i++) {
                builders[i].append(p.get(positions[i]));
            }
        }

        TabularColumnData[] workers = new TabularColumnData[w];
        for (int i = 0; i < w; i++) {
            workers[i] = builders[i].build();
        }

        return workers;
    }
}
