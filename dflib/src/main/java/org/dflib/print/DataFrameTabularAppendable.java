package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.row.RowProxy;

import java.io.IOException;

class DataFrameTabularAppendable extends TabularAppendable {

    public DataFrameTabularAppendable(Appendable out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    public void print(DataFrame df) throws IOException {

        if (df == null) {
            out.append("null");
            return;
        }

        int w = df.width();
        if (w > 0) {
            DataFrameTruncator truncator = DataFrameTruncator.create(df, maxDisplayRows);
            DataFrame top = truncator.top();
            DataFrame bottom = truncator.bottom();

            TabularColumnData[] strings = makeStrings(w, df, top, bottom);

            // since tabular printer is multiline, start with a line break to ensure logger-induced prefixes don't break
            // table alignment
            printNewLine();
            printHeader(strings);
            printNewLine();
            printHeaderSeparator(strings);
            printData(strings, 1, top.height());

            // print separator if needed
            if (truncator.isTruncated()) {
                printRowsSeparator();
            }

            printData(strings, 1 + top.height(), bottom.height());
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

    private void printHeader(TabularColumnData[] strings) throws IOException {
        int w = strings.length;
        for (int i = 0; i < w; i++) {
            if (i > 0) {
                out.append(" ");
            }
            strings[i].printTo(out, 0);
        }
    }

    private void printHeaderSeparator(TabularColumnData[] strings) throws IOException {
        int w = strings.length;
        for (int i = 0; i < w; i++) {
            if (i > 0) {
                out.append(" ");
            }

            strings[i].printSeparatorTo(out);
        }
    }

    private void printRowsSeparator() throws IOException {
        printNewLine();
        out.append("...");
    }

    private void printData(TabularColumnData[] strings, int offset, int len) throws IOException {
        int w = strings.length;
        for (int i = 0; i < len; i++) {
            printNewLine();
            for (int j = 0; j < w; j++) {
                if (j > 0) {
                    out.append(" ");
                }

                strings[j].printTo(out, offset);
            }

            offset++;
        }
    }

    private TabularColumnData[] makeStrings(int w, DataFrame df, DataFrame top, DataFrame bottom) {

        TabularColumnData.Builder[] builders = new TabularColumnData.Builder[w];
        Index columns = df.getColumnsIndex();

        // "1" is the size of the header
        int workerH = 1 + top.height() + bottom.height();

        for (int i = 0; i < w; i++) {
            builders[i] = TabularColumnData
                    .builder(df.getColumn(i).getInferredType(), workerH, maxDisplayColumnWidth)

                    // the first value is column label
                    .append(columns.get(i));
        }

        for (RowProxy p : top) {
            for (int i = 0; i < w; i++) {
                builders[i].append(p.get(i));
            }
        }

        for (RowProxy p : bottom) {
            for (int i = 0; i < w; i++) {
                builders[i].append(p.get(i));
            }
        }

        TabularColumnData[] workers = new TabularColumnData[w];
        for (int i = 0; i < w; i++) {
            workers[i] = builders[i].build();
        }

        return workers;
    }
}
