package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.dflib.echarts.EChartTestDatasets.*;

public class SeriesTest {

    @Test
    public void series() {

        String s2 = ECharts.chart().series("y2").generateScript("_tid", df2);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("['L0',1,2,3],"), s2);
        assertTrue(s2.contains("['y2',20,25,28]"), s2);

        String s3 = ECharts.chart().series("y2", "y1").generateScript("_tid", df2);
        assertTrue(s3.contains("['y2',20,25,28],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
    }

    @Test
    public void empty() {
        String s1 = ECharts.chart().generateScript("_tid", df2);
        assertTrue(s1.contains("series: ["), s1);
    }


    @Test
    public void timeSeries() {

        String s2 = ECharts.chart().series("y2").xAxis("t", XAxis.ofTime()).generateScript("_tid", df3);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("['L0','2022-01-01','2022-02-01','2022-03-01'],"), s2);
        assertTrue(s2.contains("['y2',20,25,28]"), s2);

        String s3 = ECharts.chart().series("y2", "y1").xAxis("t", XAxis.ofTime()).generateScript("_tid", df3);
        assertTrue(s3.contains("['L0','2022-01-01','2022-02-01','2022-03-01'],"), s3);
        assertTrue(s3.contains("['y2',20,25,28],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
    }

    @Test
    public void seriesType() {

        String s1 = ECharts.chart().series("y1").generateScript("_tid", df2);
        assertTrue(s1.contains("type: 'line'"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofBar(), "y1").generateScript("_tid", df2);
        assertTrue(s2.contains("type: 'bar'"), s2);

        String s3 = ECharts.chart().series(SeriesOpts.ofBar(), "y1").generateScript("_tid", df2);
        assertTrue(s3.contains("type: 'bar'"), s3);

        String s4 = ECharts.chart().series(SeriesOpts.ofPie(), "y1").generateScript("_tid", df2);
        assertTrue(s4.contains("type: 'pie'"), s4);
    }
}
