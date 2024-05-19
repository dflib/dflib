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
    void testPlotString_Title() {
        DataFrame df = DataFrame.foldByRow("y_col", "x_col").of(14, "C");

        String js1 = ECharts.x("x_col").plotString(df);
        assertFalse(js1.contains("title: {"), js1);
        assertFalse(js1.contains("text: 'My chart'"), js1);

        String js2 = ECharts.x("x_col").title("My chart").plotString(df);
        assertTrue(js2.contains("title: {"), js2);
        assertTrue(js2.contains("text: 'My chart'"), js2);
    }

    @Test
    void testPlotString_DarkTheme() {
        DataFrame df = DataFrame.foldByRow("y_col", "x_col").of(14, "C");

        String js1 = ECharts.x("x_col").plotString(df);
        assertFalse(js1.contains("), 'dark');"), js1);

        String js2 = ECharts.x("x_col").darkTheme().plotString(df);
        assertTrue(js2.contains("), 'dark');"), js2);
    }

    @Test
    void testPlotString_echartsJsUrl() {
        DataFrame df = DataFrame.foldByRow("y_col", "x_col").of(14, "C");

        String js1 = ECharts.x("x_col").plotString(df);
        assertTrue(js1.contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js'></script>"), js1);

        String js2 = ECharts.x("x_col").echartsJsUrl("https://example.org/x.js").plotString(df);
        assertTrue(js2.contains("<script type='text/javascript' src='https://example.org/x.js'></script>"), js2);
    }

    @Test
    void testPlotString_sizePixels() {
        DataFrame df = DataFrame.foldByRow("y_col", "x_col").of(14, "C");

        String js1 = ECharts.x("x_col").plotString(df);
        assertTrue(js1.contains("' style='width: 600px;height:400px;'></div>"), js1);

        String js2 = ECharts.x("x_col").sizePixels(20, 10).plotString(df);
        assertTrue(js2.contains("' style='width: 20px;height:10px;'></div>"), js2);
    }
}
