package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.dflib.echarts.EChartTestDatasets.*;

public class CartesianSeriesTest {

    @Test
    public void xAxisIndex() {

        String s1 = ECharts.chart().series("y1").generateScript("_tid", df2);
        assertFalse(s1.contains("xAxisIndex"), s1);
        assertTrue(s1.contains("x: 0,"), s1);
        assertTrue(s1.contains("y: 1"), s1);

        String s2 = ECharts.chart()
                .xAxis("x1")
                .xAxis("x2")
                .series(SeriesOpts.ofLine().xAxisIndex(1), "y1")
                .generateScript("_tid", df4);
        assertTrue(s2.contains("xAxisIndex: 1,"), s2);
        assertTrue(s2.contains("x: 1,"), s2);
        assertTrue(s2.contains("y: 2"), s2);
    }

    @Test
    public void yAxisIndex() {

        String s1 = ECharts.chart().series("y1").generateScript("_tid", df2);
        assertFalse(s1.contains("yAxisIndex"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofLine().yAxisIndex(2), "y1").generateScript("_tid", df2);
        assertTrue(s2.contains("yAxisIndex: 2,"), s2);
    }
}
