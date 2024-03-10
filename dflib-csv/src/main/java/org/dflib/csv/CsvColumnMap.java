package org.dflib.csv;

import org.apache.commons.csv.CSVRecord;
import org.dflib.Extractor;
import org.dflib.Index;

import java.util.List;

class CsvColumnMap {

    private final Index csvHeader;
    private final Index dfHeader;
    private final int[] csvPositions;

    CsvColumnMap(Index csvHeader, Index dfHeader, int[] csvPositions) {
        this.csvHeader = csvHeader;
        this.dfHeader = dfHeader;
        this.csvPositions = csvPositions;
    }

    Index getDfHeader() {
        return dfHeader;
    }

    Extractor<CSVRecord, ?>[] extractors(List<ColumnConfig> definedColumns) {

        int w = dfHeader.size();
        Extractor<CSVRecord, ?>[] extractors = new Extractor[w];

        for (ColumnConfig c : definedColumns) {
            int csvPos = c.csvColPos >= 0 ? c.csvColPos : csvHeader.position(c.csvColName);

            // later configs override earlier configs at the same position
            extractors[csvPositions[csvPos]] = c.extractor(csvHeader);
        }

        // fill "undefined" columns with default extractor
        for (int i = 0; i < w; i++) {
            if (extractors[i] == null) {
                int csvPos = csvPositions[i];
                extractors[i] = Extractor.$col(r -> r.get(csvPos));
            }
        }

        return extractors;
    }
}
