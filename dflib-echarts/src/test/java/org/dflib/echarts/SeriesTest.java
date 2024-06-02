package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeriesTest extends GenerateScriptHtmlTest {

    @Test
    public void series() {

        String s2 = EChart.chart().series("y2").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("['labels',1,2,3],"), s2);
        assertTrue(s2.contains("['y2',20,25,28]"), s2);

        String s3 = EChart.chart().series("y2", "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("['y2',20,25,28],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
    }

    @Test
    public void empty() {
        String s1 = EChart.chart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("series: ["), s1);
    }


    @Test
    public void timeSeries() {

        String s2 = EChart.chart().series("y2").xAxis("t", XAxis.ofTime()).generateScriptHtml("_tid", df3);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("['t','2022-01-01','2022-02-01','2022-03-01'],"), s2);
        assertTrue(s2.contains("['y2',20,25,28]"), s2);

        String s3 = EChart.chart().series("y2", "y1").xAxis("t", XAxis.ofTime()).generateScriptHtml("_tid", df3);
        assertTrue(s3.contains("['t','2022-01-01','2022-02-01','2022-03-01'],"), s3);
        assertTrue(s3.contains("['y2',20,25,28],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
    }

    @Test
    public void seriesType() {

        String s1 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("type: 'line'"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofBar(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("type: 'bar'"), s2);

        String s3 = EChart.chart().defaultSeriesOpts(SeriesOpts.ofBar()).series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("type: 'bar'"), s3);
    }

    @Test
    public void areaStyle() {

        String s1 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("areaStyle"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofLine().areaStyle(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("areaStyle: {}"), s2);
    }

    @Test
    public void stack() {

        String s1 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("stack:"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofLine().stack(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("stack: 'total'"), s2);
    }

    @Test
    public void smooth() {

        String s1 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("smooth"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofLine().smooth(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("smooth: true,"), s2);
    }

    @Test
    public void showSymbol() {

        String s1 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("showSymbol"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofLine().showSymbol(false), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("showSymbol: false,"), s2);
    }

    @Test
    public void label() {

        String s1 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("label:"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofLine().label(Label.ofInside()), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("label: {"), s2);
        assertTrue(s2.contains("position: 'inside'"), s2);
    }

    @Test
    public void yAxisIndex() {

        String s1 = EChart.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("yAxisIndex"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofLine().yAxisIndex(2), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("yAxisIndex: 2,"), s2);
    }


}
