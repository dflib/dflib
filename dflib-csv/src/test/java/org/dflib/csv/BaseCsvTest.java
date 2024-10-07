package org.dflib.csv;

import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class BaseCsvTest {

    static File IN_BASE;
    
    @BeforeAll
    public static void setupCsvDirs() throws URISyntaxException {
        URI csvUri = BaseCsvTest.class.getResource("f1.csv").toURI();
        IN_BASE = new File(csvUri).getAbsoluteFile().getParentFile();
    }

    protected static String inPath(String name) {
        return IN_BASE.getPath() + File.separator + name;
    }
}
