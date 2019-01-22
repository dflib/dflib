package com.nhl.dflib.csv;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class CsvTest {

    private static File CSV_BASE;

    @BeforeClass
    public static void findCsvDir() throws URISyntaxException {
        URI csvUri = CsvTest.class.getResource("f1.csv").toURI();
        CSV_BASE = new File(csvUri).getAbsoluteFile().getParentFile();
    }

    private static String csvPath(String name) {
        return CSV_BASE.getPath() + File.separator + name;
    }

    @Test
    public void testFromFile() {
        DataFrame df = Csv.fromFile(csvPath("f1.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }
}
