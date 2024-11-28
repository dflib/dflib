package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df2;
import static org.dflib.echarts.EChartTestDatasets.df4;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeatmapSeriesTest {

    @Test
    public void type() {

        String s1 = ECharts.chart().generateScript("_tid", df2);
        assertFalse(s1.contains("type: 'heatmap'"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofHeatmap(), "y1").generateScript("_tid", df2);
        assertTrue(s2.contains("type: 'heatmap'"), s2);
    }

    @Test
    public void name() {

        String s2 = ECharts.chart().series(SeriesOpts.ofHeatmap(), "y2").generateScript("_tid", df2);
        assertFalse(s2.contains("name: 'Y2',"), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);

        String s3 = ECharts.chart().series(SeriesOpts.ofHeatmap().name("Y2"), "y2").generateScript("_tid", df2);
        assertTrue(s3.contains("name: 'Y2',"), s3);
        assertFalse(s3.contains("name: 'y2',"), s3);
    }

    @Test
    public void dataCartesian() {

        // heatmap doesn't properly as of ECHarts 5.5.1, so we are encoding data inside the series instead of
        // using a dataset

        String s1 = ECharts.chart().series(SeriesOpts.ofHeatmap().cartesian2D(), "y1", "y2").generateScript("_tid", df2);

        assertFalse(s1.contains("dataset"), s1);
        assertFalse(s1.contains("encode"), s1);

        assertTrue(s1.contains("data: ["), s1);
        assertTrue(s1.contains("['L0','y1','y2'],"), s1);
        assertTrue(s1.contains("[1,10,20],"), s1);
        assertTrue(s1.contains("[2,11,25]"), s1);
        assertTrue(s1.contains("[3,14,28]"), s1);
    }

    @Test
    public void dataCalendar() {

        // heatmap doesn't properly as of ECHarts 5.5.1, so we are encoding data inside the series instead of
        // using a dataset

        String s1 = ECharts.chart().series(SeriesOpts.ofHeatmap().calendar(), "y1").generateScript("_tid", df2);

        assertFalse(s1.contains("dataset"), s1);
        assertFalse(s1.contains("encode"), s1);

        assertTrue(s1.contains("data: ["), s1);
        assertTrue(s1.contains("['L0','y1'],"), s1);
        assertTrue(s1.contains("[1,10],"), s1);
        assertTrue(s1.contains("[2,11]"), s1);
        assertTrue(s1.contains("[3,14]"), s1);
    }

    @Test
    public void coordinateSystem() {
        String s1 = ECharts.chart().series(SeriesOpts.ofHeatmap(), "y1").generateScript("_tid", df2);
        assertFalse(s1.contains("coordinateSystem:"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofHeatmap().calendar(), "y1").generateScript("_tid", df2);
        assertTrue(s2.contains("coordinateSystem: 'calendar'"), s2);

        String s3 = ECharts.chart().series(SeriesOpts.ofHeatmap().cartesian2D(), "y1").generateScript("_tid", df2);
        assertTrue(s3.contains("coordinateSystem: 'cartesian2d'"), s3);
    }

    @Test
    public void xAxisIndex() {

        String s1 = ECharts.chart()
                .xAxis("x1")
                .xAxis("x2")
                .series(SeriesOpts.ofHeatmap().xAxisIndex(1), "y1")
                .generateScript("_tid", df4);
        assertTrue(s1.contains("xAxisIndex: 1,"), s1);
        assertFalse(s1.contains("encode"));
    }

    @Test
    public void yAxisIndex() {
        String s1 = ECharts.chart().series(SeriesOpts.ofHeatmap().yAxisIndex(2), "y1").generateScript("_tid", df2);
        assertTrue(s1.contains("yAxisIndex: 2,"), s1);
    }

    @Test
    public void calendarIndex() {
        String s1 = ECharts.chart().series(SeriesOpts.ofHeatmap().calendarIndex(2), "y1").generateScript("_tid", df2);
        assertTrue(s1.contains("calendarIndex: 2,"), s1);
    }
}
