package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExcelSaver_SaveSheetTargetsTest {

    static final DataFrame df = DataFrame.foldByRow("C1", "C2").of(
            "a", "b",
            "c", "d");

    @TempDir
    Path outBase;

    @Test
    public void saveSheet_ToStream() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Excel.saver().saveSheet(df, out, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(new ByteArrayInputStream(out.toByteArray()), "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void saveSheet_ToFile() {

        Path out = outBase.resolve("saveSheet_ToFile.xlsx");
        Excel.saver().saveSheet(df, out.toFile(), "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(out, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void saveSheet_ToFilePath() {

        Path out = outBase.resolve("saveSheet_ToFilePath.xlsx");
        Excel.saver().saveSheet(df, out.toFile().getAbsolutePath(), "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(out, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void saveSheet_Mkdirs() {

        Path out = outBase.resolve("dir").resolve("saveSheet_Mkdirs.xlsx");
        Excel.saver().createMissingDirs().saveSheet(df, out, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(out, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void saveSheet_NoMkdirs() {
        Path out = outBase.resolve("dir").resolve("saveSheet_NoMkdirs.xlsx");
        assertThrows(RuntimeException.class, () -> Excel.saver().saveSheet(df, out, "s1"));
    }
}
