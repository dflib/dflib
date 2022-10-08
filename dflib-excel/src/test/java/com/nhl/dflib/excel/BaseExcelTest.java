package com.nhl.dflib.excel;

import org.junit.jupiter.api.io.TempDir;

import java.io.File;

public class BaseExcelTest {

    @TempDir
    static File OUT_BASE;

    protected static String outPath(String name) {
        return OUT_BASE.getPath() + File.separator + name;
    }
}
