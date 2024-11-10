package org.dflib.echarts;

import org.dflib.DataFrame;

import java.time.LocalDate;

abstract class EChartTestDatasets {

    static final DataFrame df1 = DataFrame.foldByRow("y", "x").of(14, "C");

    static final DataFrame df2 = DataFrame.foldByRow("y1", "y2", "x").of(
            10, 20, "A",
            11, 25, "B",
            14, 28, "C");

    static final DataFrame df3 = DataFrame.foldByRow("t", "y1", "y2", "x").of(
            LocalDate.of(2022, 1, 1), 10, 20, "A",
            LocalDate.of(2022, 2, 1), 11, 25, "B",
            LocalDate.of(2022, 3, 1), 14, 28, "C");

    static final DataFrame df4 = DataFrame.foldByRow("x2", "y1", "y2", "x1").of(
            "X", 10, 20, "A",
            "Y", 11, 25, "B",
            "Z", 14, 28, "C");

    private EChartTestDatasets() {
    }
}
