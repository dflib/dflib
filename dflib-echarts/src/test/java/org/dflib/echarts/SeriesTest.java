package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeriesTest extends GenerateScriptHtmlTest {

    @Test
    public void series() {

        String s2 = ECharts.chart().series("y2").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("['labels',1,2,3],"), s2);
        assertTrue(s2.contains("['y2',20,25,28]"), s2);

        String s3 = ECharts.chart().series("y2", "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("['y2',20,25,28],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
    }

    @Test
    public void empty() {
        String s1 = ECharts.chart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("series: ["), s1);
    }


    @Test
    public void timeSeries() {

        String s2 = ECharts.chart().series("y2").xAxis("t", XAxis.ofTime()).generateScriptHtml("_tid", df3);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("['t','2022-01-01','2022-02-01','2022-03-01'],"), s2);
        assertTrue(s2.contains("['y2',20,25,28]"), s2);

        String s3 = ECharts.chart().series("y2", "y1").xAxis("t", XAxis.ofTime()).generateScriptHtml("_tid", df3);
        assertTrue(s3.contains("['t','2022-01-01','2022-02-01','2022-03-01'],"), s3);
        assertTrue(s3.contains("['y2',20,25,28],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
    }

    @Test
    public void seriesType() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("type: 'line'"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.bar(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("type: 'bar'"), s2);

        String s3 = ECharts.chart().defaultSeriesOpts(SeriesOpts.bar()).series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("type: 'bar'"), s3);
    }

    @Test
    public void areaStyle() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("areaStyle"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().areaStyle(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("areaStyle: {}"), s2);
    }

    @Test
    public void stack() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("stack:"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().stack(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("stack: 'total'"), s2);
    }

    @Test
    public void smooth() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("smooth"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().smooth(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("smooth: true,"), s2);
    }

    @Test
    public void showSymbol() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("showSymbol"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().showSymbol(false), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("showSymbol: false,"), s2);
    }

    @Test
    public void label() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("label:"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().label(Label.inside()), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("label: {"), s2);
        assertTrue(s2.contains("position: 'inside'"), s2);
    }

}
