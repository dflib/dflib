package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExcelSaver_SaveTest extends BaseExcelTest {

    @Test
    public void save_ToStream() {

        DataFrame df1 = DataFrame.foldByRow("C1", "C2").of(
                "a", "b",
                "c", "d");

        DataFrame df2 = DataFrame.foldByRow("C3", "C4").of(
                "e", "f",
                "g", "h");

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
    public void saveSheet_ToFile() {

        File file = new File(outPath("testSave_ToFile.xlsx"));

        DataFrame df1 = DataFrame.foldByRow("C1", "C2").of(
                "a", "b",
                "c", "d");

        DataFrame df2 = DataFrame.foldByRow("C3", "C4").of(
                "e", "f",
                "g", "h");

        Excel.saver().save(Map.of("df1", df1, "df2", df2), file);

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(file);

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
    public void saveSheet_ToFilePath() {

        String filePath = outPath("testSave_ToFilePath.xlsx");

        DataFrame df1 = DataFrame.foldByRow("C1", "C2").of(
                "a", "b",
                "c", "d");

        DataFrame df2 = DataFrame.foldByRow("C3", "C4").of(
                "e", "f",
                "g", "h");

        Excel.saver().save(Map.of("df1", df1, "df2", df2), filePath);

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(filePath);

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
    public void saveSheet_Mkdirs() {

        String path = outPath("Mkdirs" + File.separator + "f2" + File.separator + "testSave_Mkdirs.xlsx");
        File file = new File(path);

        DataFrame df1 = DataFrame.foldByRow("C1", "C2").of(
                "a", "b",
                "c", "d");

        DataFrame df2 = DataFrame.foldByRow("C3", "C4").of(
                "e", "f",
                "g", "h");

        Excel.saver().createMissingDirs().save(Map.of("df1", df1, "df2", df2), file);

        Map<String, DataFrame> reload = Excel.loader()
                .firstRowAsHeader()
                .load(file);

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

        String path = outPath("NoMkdirs" + File.separator + "f4" + File.separator + "testSave_NoMkdirs.xlsx");
        File file = new File(path);

        DataFrame df1 = DataFrame.foldByRow("C1", "C2").of(
                "a", "b",
                "c", "d");

        DataFrame df2 = DataFrame.foldByRow("C3", "C4").of(
                "e", "f",
                "g", "h");

        assertThrows(RuntimeException.class, () -> Excel.saver().save(Map.of("df1", df1, "df2", df2), file));
    }
}
