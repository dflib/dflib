package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;

public class Excel_LoadSheetTest {

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void fromStreamMultiSheet(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            DataFrame s1 = Excel.loadSheet(in, "S1");
            new DataFrameAsserts(s1, "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");
        }

        // annoying - POI closes InputStream that it didn't open... So have to restart the stream to load another Sheet

        try (InputStream in = getClass().getResourceAsStream(source)) {

            DataFrame s2 = Excel.loadSheet(in, "S2");
            new DataFrameAsserts(s2, "A", "B", "C", "D")
                    .expectHeight(1)
                    .expectRow(0, "Five", "Six", "Seven", "Eight");
        }
    }
}
