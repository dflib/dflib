package org.dflib.echarts;

import org.dflib.DataFrame;

import java.time.LocalDate;

public abstract class GenerateScriptHtmlTest {

    protected static final DataFrame df1 = DataFrame.foldByRow("y", "x").of(14, "C");

    protected static final DataFrame df2 = DataFrame.foldByRow("y1", "y2", "x").of(
            10, 20, "A",
            11, 25, "B",
            14, 28, "C");

    protected static final DataFrame df3 = DataFrame.foldByRow("t", "y1", "y2", "x").of(
            LocalDate.of(2022, 1, 1), 10, 20, "A",
            LocalDate.of(2022, 2, 1), 11, 25, "B",
            LocalDate.of(2022, 3, 1), 14, 28, "C");
}
