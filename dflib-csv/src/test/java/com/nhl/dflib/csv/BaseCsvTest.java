package com.nhl.dflib.csv;

import org.junit.BeforeClass;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class BaseCsvTest {

    private static File CSV_BASE;

    @BeforeClass
    public static void findCsvDir() throws URISyntaxException {
        URI csvUri = CsvTest.class.getResource("f1.csv").toURI();
        CSV_BASE = new File(csvUri).getAbsoluteFile().getParentFile();
    }

    protected static String csvPath(String name) {
        return CSV_BASE.getPath() + File.separator + name;
    }

}
