package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExcelLoader_LoadSheetTest {

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void testFromStream_MultiSheet(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            DataFrame s1 = new ExcelLoader().loadSheet(in, "S1");
            new DataFrameAsserts(s1, "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");
        }

        // annoying - POI closes InputStream that it didn't open... So have to restart the stream to load another Sheet

        try (InputStream in = getClass().getResourceAsStream(source)) {

            DataFrame s2 = new ExcelLoader().loadSheet(in, "S2");
            new DataFrameAsserts(s2, "A", "B", "C", "D")
                    .expectHeight(1)
                    .expectRow(0, "Five", "Six", "Seven", "Eight");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void testFromFile_MultiSheet(String source) throws URISyntaxException {

        File file = new File(getClass().getResource(source).toURI());

        DataFrame s1 = new ExcelLoader().loadSheet(file, "S1");
        new DataFrameAsserts(s1, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");

        DataFrame s2 = new ExcelLoader().loadSheet(file, "S2");
        new DataFrameAsserts(s2, "A", "B", "C", "D")
                .expectHeight(1)
                .expectRow(0, "Five", "Six", "Seven", "Eight");
    }

    @Test
    public void tesInvalidSheetName() throws URISyntaxException {
        File file = new File(getClass().getResource("multi-sheet.xlsx").toURI());
        assertThrows(RuntimeException.class, () -> new ExcelLoader().loadSheet(file, "No_such_Sheet"));
    }
}
