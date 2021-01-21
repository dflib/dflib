package com.nhl.dflib.csv;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BaseCsvTest {

    static File IN_BASE;

    @TempDir
    static File OUT_BASE;
    
    @BeforeAll
    public static void setupCsvDirs() throws URISyntaxException {
        URI csvUri = BaseCsvTest.class.getResource("f1.csv").toURI();
        IN_BASE = new File(csvUri).getAbsoluteFile().getParentFile();
    }

    protected static String inPath(String name) {
        return IN_BASE.getPath() + File.separator + name;
    }

    protected static String outPath(String name) {
        return OUT_BASE.getPath() + File.separator + name;
    }

    protected String readFile(String path) throws IOException {

        try (FileReader in = new FileReader(path)) {

            StringBuilder out = new StringBuilder();
            char[] buffer = new char[1024];
            int read = -1;
            while ((read = in.read(buffer)) >= 0) {
                out.append(buffer, 0, read);
            }

            return out.toString();
        }
    }
}
