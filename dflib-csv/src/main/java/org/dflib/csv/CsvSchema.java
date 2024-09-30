package org.dflib.csv;

import org.dflib.Index;

class CsvSchema {

    private final Index csvHeader;
    private final Index dfHeader;
    private final int[] csvPositions;

    CsvSchema(Index csvHeader, Index dfHeader, int[] csvPositions) {
        this.csvHeader = csvHeader;
        this.dfHeader = dfHeader;
        this.csvPositions = csvPositions;
    }

    Index getDfHeader() {
        return dfHeader;
    }

    Index getCsvHeader() {
        return csvHeader;
    }

    int[] getCsvPositions() {
        return csvPositions;
    }
}
