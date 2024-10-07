package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcelSaver_SaveSheet_MergeTest {

    @TempDir
    Path outBase;

    @Test
    public void saveSheet_Merge() {

        DataFrame df1 = DataFrame.foldByRow("C1", "C2").of(
                "a", "b",
                "d", "c");

        DataFrame df2 = DataFrame.foldByRow("C3", "C4").of(
                "e", "f",
                "g", "h");

        DataFrame df3 = DataFrame.foldByRow("C5", "C6").of(
                "i", "j",
                "k", "l");

        // Note that merge only works with filesystem-based sinks; using ByteArrayOutputStream will not work
        Path out = outBase.resolve("saveSheet_Merge.xlsx");

        // New sheets are appended to the existing Excel; existing sheets are overridden
        Excel.saver().saveSheet(df1, out, "s1");
        Excel.saver().saveSheet(df2, out, "s2");
        Excel.saver().saveSheet(df3, out, "s1");

        Map<String, DataFrame> reload = Excel.loader().firstRowAsHeader().load(out);
        assertEquals(2, reload.size());
        assertEquals(Set.of("s1", "s2"), reload.keySet());

        new DataFrameAsserts(reload.get("s1"), "C5", "C6")
                .expectHeight(2)
                .expectRow(0, "i", "j")
                .expectRow(1, "k", "l");

        new DataFrameAsserts(reload.get("s2"), "C3", "C4")
                .expectHeight(2)
                .expectRow(0, "e", "f")
                .expectRow(1, "g", "h");
    }
}
