package org.dflib.echarts;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EChartsTest {

    @Test
    void testPlotString() {
        DataFrame df = DataFrame.foldByRow("y1_col", "y2_col", "x_col")
                .of(
                        10, 20, "A",
                        11, 25, "B",
                        14, 28, "C");

        String js = ECharts.x("x_col").y("y1_col", "y2_col").plotString(df);
        assertTrue(js.contains("<div id='dflib-echarts-"), js);
        assertTrue(js.contains("data: ['A','B','C']"), js);
        assertTrue(js.contains("data: [10,11,14]"), js);
        assertTrue(js.contains("data: [20,25,28]"), js);
    }
}
