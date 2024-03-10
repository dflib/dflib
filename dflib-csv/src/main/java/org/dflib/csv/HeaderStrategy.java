package org.dflib.csv;

import org.apache.commons.csv.CSVRecord;
import org.dflib.Index;

import java.util.Iterator;

@FunctionalInterface
interface HeaderStrategy {

    CsvHeader createCsvHeader(Iterator<CSVRecord> it);

    static HeaderStrategy explicit(Index header) {
        return it -> new CsvHeader(header, null);
    }

    static HeaderStrategy firstRow() {
        return HeaderStrategy::firstRow;
    }

    static HeaderStrategy generated() {
        return HeaderStrategy::generated;
    }

    private static CsvHeader firstRow(Iterator<CSVRecord> it) {

        if (!it.hasNext()) {
            return new CsvHeader(Index.of(), null);
        }

        CSVRecord firstRow = it.next();

        int width = firstRow.size();
        String[] columnNames = new String[width];
        for (int i = 0; i < width; i++) {
            columnNames[i] = firstRow.get(i);
        }

        return new CsvHeader(Index.of(columnNames), null);
    }

    private static CsvHeader generated(Iterator<CSVRecord> it) {

        CSVRecord firstRow = it.next();

        int width = firstRow.size();
        String[] columnNames = new String[width];
        for (int i = 0; i < width; i++) {
            columnNames[i] = "c" + i;
        }

        // pass the first row back to the caller as the data row
        return new CsvHeader(Index.of(columnNames), firstRow);
    }
}
