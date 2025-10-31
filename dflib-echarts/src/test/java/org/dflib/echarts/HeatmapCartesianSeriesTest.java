package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df4;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeatmapCartesianSeriesTest {

    @Test
    public void type() {

        String s1 = ECharts.chart().plot(df4, "_tid").getChartScript();
        assertFalse(s1.contains("type: 'heatmap'"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofHeatmap(), "x1", "y1", "y2").plot(df4, "_tid").getChartScript();
        assertTrue(s2.contains("type: 'heatmap'"), s2);
    }

    @Test
    public void name() {

        String s2 = ECharts.chart().series(SeriesOpts.ofHeatmap(), "x1", "y1", "y2").plot(df4, "_tid").getChartScript();
        assertFalse(s2.contains("name: 'Y2',"), s2);
        assertTrue(s2.contains("name: 'heatmap',"), s2);

        String s3 = ECharts.chart().series(SeriesOpts.ofHeatmap().name("Y2"), "x1", "y1", "y2").plot(df4, "_tid").getChartScript();
        assertTrue(s3.contains("name: 'Y2',"), s3);
        assertFalse(s3.contains("name: 'y2',"), s3);
    }

    @Test
    public void data() {

        String s1 = ECharts.chart().series(SeriesOpts.ofHeatmap(), "x1", "y1", "y2").plot(df4, "_tid").getChartScript();

        assertFalse(s1.contains("dataset"), s1);
        assertFalse(s1.contains("encode"), s1);

        assertTrue(s1.contains("data: ["), s1);
        assertTrue(s1.contains("['x1','y1','y2'],"), s1);
        assertTrue(s1.contains("['A',10,20],"), s1);
        assertTrue(s1.contains("['B',11,25],"), s1);
        assertTrue(s1.contains("['C',14,28]"), s1);
    }

    @Test
    public void coordinateSystem() {
        String s3 = ECharts.chart().series(SeriesOpts.ofHeatmap(), "x1", "y1", "y2").plot(df4, "_tid").getChartScript();
        assertTrue(s3.contains("coordinateSystem: 'cartesian2d'"), s3);
    }

    @Test
    public void xAxisIndex() {

        String s1 = ECharts.chart()
                .xAxis("x1")
                .xAxis("x2")
                .series(SeriesOpts.ofHeatmap().xAxisIndex(1), "x1", "y1", "y2")
                .plot(df4, "_tid").getChartScript();
        assertTrue(s1.contains("xAxisIndex: 1,"), s1);
        assertFalse(s1.contains("encode"));
    }

    @Test
    public void yAxisIndex() {
        String s1 = ECharts.chart().series(SeriesOpts.ofHeatmap().yAxisIndex(2), "x1", "y1", "y2").plot(df4, "_tid").getChartScript();
        assertTrue(s1.contains("yAxisIndex: 2,"), s1);
    }
}
