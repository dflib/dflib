package org.dflib.echarts;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoxplotSeriesTest {

    protected static final DataFrame df = DataFrame.foldByRow("min", "q1", "median", "q3", "max", "on", "on2").of(
            15, 18, 19, 20, 21, "2024-08-12", "2024-07-12",
            18, 19, 20, 25, 28, "2024-08-13", "2024-07-13",
            15, 20, 21, 27, 30, "2024-08-14", "2024-07-14");

    @Test
    public void xAxisIndex() {

        String s1 = ECharts.chart()
                .series(SeriesOpts.ofBoxplot(), "min", "q1", "median", "q3", "max")
                .plot(df, "_tid").getChartScript();
        assertFalse(s1.contains("xAxisIndex"), s1);

        String s2 = ECharts.chart()
                .xAxis("on")
                .xAxis("on2")
                .series(SeriesOpts.ofBoxplot().xAxisIndex(1), "min", "q1", "median", "q3", "max")
                .plot(df, "_tid").getChartScript();
        assertTrue(s2.contains("['L0','2024-08-12','2024-08-13','2024-08-14'],"), s2);
        assertTrue(s2.contains("['L1','2024-07-12','2024-07-13','2024-07-14']"), s2);
        assertTrue(s2.contains("type: 'boxplot'"), s2);
        assertTrue(s2.contains("xAxisIndex: 1,"), s2);
        assertTrue(s2.contains("x: 1,"), s2);
        assertTrue(s2.contains("y: [2,3,4,5,6]"), s2);
    }

    @Test
    public void itemStyle() {

        BoxplotItemStyle style = BoxplotItemStyle.of()
                .color("#ffffff")
                .borderColor("#eeeeeee")
                .borderWidth(2)
                .borderType(LineType.dotted)
                .opacity(0.55);

        String s1 = ECharts.chart()
                .series(SeriesOpts.ofBoxplot(), "min", "q1", "median", "q3", "max")
                .plot(df, "_tid").getChartScript();
        assertFalse(s1.contains("itemStyle:"), s1);

        String s2 = ECharts.chart()
                .xAxis("on")
                .xAxis("on2")
                .series(SeriesOpts.ofBoxplot().itemStyle(style), "min", "q1", "median", "q3", "max")
                .plot(df, "_tid").getChartScript();

        assertTrue(s2.contains("type: 'boxplot'"), s2);
        assertTrue(s2.contains("itemStyle"), s2);
        assertTrue(s2.contains("color: '#ffffff',"), s2);
        assertTrue(s2.contains("borderColor: '#eeeeeee',"), s2);
        assertTrue(s2.contains("borderWidth: 2,"), s2);
        assertTrue(s2.contains("borderType: 'dotted'"), s2);
        assertTrue(s2.contains("opacity: 0.55"), s2);
    }

    @Test
    public void data() {

        String s1 = ECharts.chart().plot(df, "_tid").getChartScript();
        assertTrue(s1.contains("dataset"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofBoxplot(), "min", "q1", "median", "q3", "max").plot(df, "_tid").getChartScript();
        assertTrue(s2.contains("dataset"), s2);
        assertTrue(s2.contains("['L0',1,2,3]"), s2);
        assertTrue(s2.contains("['min',15,18,15],"), s2);
        assertTrue(s2.contains("['q1',18,19,20],"), s2);
        assertTrue(s2.contains("['median',19,20,21],"), s2);
        assertTrue(s2.contains("['q3',20,25,27],"), s2);
        assertTrue(s2.contains("['max',21,28,30]"), s2);

        assertTrue(s2.contains("x: 0,"), s2);
        assertTrue(s2.contains("y: [1,2,3,4,5]"), s2);
    }

    @Test
    // #551 - two plot series using the same column
    public void boxplotAndLine() {

        String s2 = ECharts.chart()
                .series(SeriesOpts.ofBoxplot(), "min", "q1", "median", "q3", "max")
                .series(SeriesOpts.ofLine(), "median")
                .plot(df, "_tid").getChartScript();

        assertTrue(s2.contains("dataset"), s2);
        assertTrue(s2.contains("['L0',1,2,3]"), s2);
        assertTrue(s2.contains("['min',15,18,15],"), s2);
        assertTrue(s2.contains("['q1',18,19,20],"), s2);
        assertTrue(s2.contains("['median',19,20,21],"), s2);
        assertTrue(s2.contains("['q3',20,25,27],"), s2);
        assertTrue(s2.contains("['max',21,28,30]"), s2);

        int firstMedian = s2.indexOf("['median',19,20,21],");
        int secondMedian = s2.indexOf("['median',19,20,21]", firstMedian + 10);
        assertTrue(secondMedian > 0, s2);

        assertTrue(s2.contains("x: 0,"), s2);
        assertTrue(s2.contains("y: [1,2,3,4,5]"), s2);
    }
}
