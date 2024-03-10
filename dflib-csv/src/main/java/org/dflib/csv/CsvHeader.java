package org.dflib.csv;

import org.apache.commons.csv.CSVRecord;
import org.dflib.Index;

class CsvHeader {
    private final Index header;
    private final CSVRecord maybeUnconsumedDataRow;

    CsvHeader(Index header, CSVRecord maybeUnconsumedDataRow) {
        this.header = header;
        this.maybeUnconsumedDataRow = maybeUnconsumedDataRow;
    }

    CSVRecord getMaybeUnconsumedDataRow() {
        return maybeUnconsumedDataRow;
    }

    Index getHeader() {
        return header;
    }
}
