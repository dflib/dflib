package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

class CsvLoader_TrailingDelimiterTest {

    @Test
    void trailingDelimiterEnabled() {
        String csv = "A,B,\n1,2,\n3,4,";

        DataFrame df = new CsvLoader()
                .format(CSVFormat.DEFAULT.builder()
                        .setAllowMissingColumnNames(true)
                        .setLenientEof(true)
                        .setTrailingDelimiter(true)
                        .build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void trailingDelimiterDisabled() {
        String csv = "A,B,\n1,2,\n3,4,";

        DataFrame df = new CsvLoader()
                .format(CSVFormat.DEFAULT.builder()
                        .setAllowMissingColumnNames(true)
                        .setLenientEof(true)
                        .setTrailingDelimiter(false)
                        .build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B", "")
                .expectHeight(2)
                .expectRow(0, "1", "2", "")
                .expectRow(1, "3", "4", "");
    }
}
