package org.dflib.csv;

import org.dflib.Index;

/**
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 * @since 2.0.0
 */
public class CsvSchema {

    private final Index dfHeader;
    private final int[] csvPositions;

    CsvSchema(Index dfHeader, int[] csvPositions) {
        this.dfHeader = dfHeader;
        this.csvPositions = csvPositions;
    }

    public Index getDfHeader() {
        return dfHeader;
    }

    public int[] getCsvPositions() {
        return csvPositions;
    }
}
