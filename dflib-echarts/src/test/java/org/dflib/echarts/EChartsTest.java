package org.dflib.echarts;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertTrue(js.contains("<div id='dfl_ech_"), js);
        assertTrue(js.contains("data: ['A','B','C']"), js);
        assertTrue(js.contains("data: [10,11,14]"), js);
        assertTrue(js.contains("data: [20,25,28]"), js);
    }

    @Test
    void testPlotString_DarkMode() {
        DataFrame df = DataFrame.foldByRow("y_col", "x_col").of(14, "C");

        String js1 = ECharts.x("x_col").y("y_col").plotString(df);
        assertFalse(js1.contains("'dark'));"), js1);

        String js2 = ECharts.x("x_col").y("y_col").darkMode().plotString(df);
        assertTrue(js2.contains("'dark'));"), js2);
    }
}
