package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df4;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScatterSingleAxisSeriesTest {

    @Test
    public void coordinateSystem() {
        String s3 = ECharts.chart().series(SeriesOpts.ofScatterSingleAxis(), "y1").plot(df4, "_tid").getChartScript();
        assertTrue(s3.contains("coordinateSystem: 'singleAxis'"), s3);
    }

    @Test
    public void singleAxisIndex() {
        String s3 = ECharts.chart().series(SeriesOpts.ofScatterSingleAxis().singleAxisIndex(1), "y1").plot(df4, "_tid").getChartScript();
        assertTrue(s3.contains("singleAxisIndex: 1"), s3);
    }

    @Test
    public void data() {
        String s1 = ECharts.chart().series(SeriesOpts.ofScatterSingleAxis(), "y1")
                .plot(df4, "_tid")
                .getChartScript();
        assertTrue(s1.contains("['y1',10,11,14]"), s1);
        assertTrue(s1.contains("encode: {"), s1);
        assertTrue(s1.contains("single: 0,"), s1);
        assertTrue(s1.contains("value: 0,"), s1);

        String s2 = ECharts.chart()
                .series(SeriesOpts.ofLine(), "y1")
                .series(SeriesOpts.ofScatterSingleAxis(), "y2").plot(df4, "_tid").getChartScript();
        assertTrue(s2.contains("['y2',20,25,28]"), s2);
        assertTrue(s2.contains("encode: {"), s2);
        assertTrue(s2.contains("single: 2,"), s2);
        assertTrue(s2.contains("value: 2,"), s2);

        String s3 = ECharts.chart()
                .singleAxis("x1")
                .series(SeriesOpts.ofScatterSingleAxis(), "y2").plot(df4, "_tid").getChartScript();
        assertTrue(s3.contains("['L0','A','B','C']"), s3);
        assertTrue(s3.contains("['y2',20,25,28]"), s3);
        assertTrue(s3.contains("type: 'category'"), s3);
        assertTrue(s3.contains("encode: {"), s3);
        assertTrue(s3.contains("single: 0,"), s3);
        assertTrue(s3.contains("value: 1,"), s3);
    }

}
