package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExcelSaver_SaveTargetsTest {

    static final DataFrame df1 = DataFrame.foldByRow("C1", "C2").of(
            "a", "b",
            "c", "d");

    static final DataFrame df2 = DataFrame.foldByRow("C3", "C4").of(
            "e", "f",
            "g", "h");

    @TempDir
    Path outBase;

    @Test
    public void save_ToStream() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Excel.saver().save(Map.of("df1", df1, "df2", df2), out);

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(new ByteArrayInputStream(out.toByteArray()));

        assertEquals(2, reload.size());
        new DataFrameAsserts(reload.get("df1"), "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");

        new DataFrameAsserts(reload.get("df2"), "C3", "C4")
                .expectHeight(2)
                .expectRow(0, "e", "f")
                .expectRow(1, "g", "h");
    }

    @Test
    public void save_ToPath() {

        Path out = outBase.resolve("save_ToPath.csv");
        Excel.saver().save(Map.of("df1", df1, "df2", df2), out);

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(out);

        assertEquals(2, reload.size());
        new DataFrameAsserts(reload.get("df1"), "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");

        new DataFrameAsserts(reload.get("df2"), "C3", "C4")
                .expectHeight(2)
                .expectRow(0, "e", "f")
                .expectRow(1, "g", "h");
    }

    @Test
    public void save_ToFile() {

        Path out = outBase.resolve("save_ToFile.csv");
        Excel.saver().save(Map.of("df1", df1, "df2", df2), out.toFile());

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(out);

        assertEquals(2, reload.size());
        new DataFrameAsserts(reload.get("df1"), "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");

        new DataFrameAsserts(reload.get("df2"), "C3", "C4")
                .expectHeight(2)
                .expectRow(0, "e", "f")
                .expectRow(1, "g", "h");
    }

    @Test
    public void save_ToFilePath() {

        Path out = outBase.resolve("save_ToFilePath.csv");
        Excel.saver().save(Map.of("df1", df1, "df2", df2), out.toFile().getAbsolutePath());

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(out);

        assertEquals(2, reload.size());
        new DataFrameAsserts(reload.get("df1"), "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");

        new DataFrameAsserts(reload.get("df2"), "C3", "C4")
                .expectHeight(2)
                .expectRow(0, "e", "f")
                .expectRow(1, "g", "h");
    }

    @Test
    public void save_ToPath_Mkdirs() {

        Path out = outBase.resolve("dir").resolve("save_ToPath_Mkdirs.csv");
        Excel.saver().createMissingDirs().save(Map.of("df1", df1, "df2", df2), out);

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(out);

        assertEquals(2, reload.size());
        new DataFrameAsserts(reload.get("df1"), "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");

        new DataFrameAsserts(reload.get("df2"), "C3", "C4")
                .expectHeight(2)
                .expectRow(0, "e", "f")
                .expectRow(1, "g", "h");
    }

    @Test
    public void saveSheet_NoMkdirs() {

        Path out = outBase.resolve("dir").resolve("save_ToPath_Mkdirs.csv");
        assertThrows(RuntimeException.class, () -> Excel.saver().save(Map.of("df1", df1, "df2", df2), out));
    }
}
