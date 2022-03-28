package com.nhl.dflib.excel;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class BaseExcelTest {

    static File IN_BASE;

    @TempDir
    static File OUT_BASE;

    @BeforeAll
    public static void setupExcelDirs() throws URISyntaxException {
        URI excelURI = BaseExcelTest.class.getResource("one-sheet.xls").toURI();
        IN_BASE = new File(excelURI).getAbsoluteFile().getParentFile();
    }

    protected static String inPath(String name) {
        return IN_BASE.getPath() + File.separator + name;
    }

    protected static String outPath(String name) {
        return OUT_BASE.getPath() + File.separator + name;
    }
}
