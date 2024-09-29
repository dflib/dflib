package org.dflib.csv;

import org.apache.commons.csv.CSVRecord;
import org.dflib.Extractor;
import org.dflib.Index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<Integer, ColumnConfig> definedColsMap = new HashMap<>();
        for (ColumnConfig c : definedColumns) {
            int csvPos = c.srcColPos >= 0 ? c.srcColPos : csvHeader.position(c.srcColName);

            // later configs override earlier configs at the same position
            definedColsMap.put(csvPos, c);
        }

        // fill "undefined" columns with default extractor
        for (int i = 0; i < w; i++) {
            int csvPos = csvPositions[i];
            ColumnConfig cc = definedColsMap.get(csvPos);
            extractors[i] = cc != null ? cc.extractor(csvHeader) : Extractor.$col(r -> r.get(csvPos));
        }

        return extractors;
    }
}
