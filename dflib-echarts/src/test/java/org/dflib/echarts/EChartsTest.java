package org.dflib.echarts;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EChartsTest {

    static final DataFrame df1 = DataFrame.foldByRow("y", "x").of(14, "C");

    static final DataFrame df2 = DataFrame.foldByRow("y1", "y2", "x").of(
            10, 20, "A",
            11, 25, "B",
            14, 28, "C");

    @Test
    public void plot() {
        EChart ch = ECharts.lineChart().xAxis("x").series("y1", "y2").plot(df2);
        assertTrue(ch.getExternalScript().contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js'></script>"), ch.getExternalScript());
        assertTrue(ch.getContainer().contains("<div id='dfl_ech_"), ch.getContainer());
        assertTrue(ch.getScript().contains("data: ['A','B','C']"), ch.getScript());
        assertTrue(ch.getScript().contains("data: [10,11,14]"), ch.getScript());
        assertTrue(ch.getScript().contains("data: [20,25,28]"), ch.getScript());
    }

    @Test
    public void generateExternalScriptHtml() {

        String s1 = ECharts.lineChart().generateExternalScriptHtml();
        assertTrue(s1.contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js'></script>"), s1);

        String s2 = ECharts.lineChart().scriptUrl("https://example.org/x.js").generateExternalScriptHtml();
        assertTrue(s2.contains("<script type='text/javascript' src='https://example.org/x.js'></script>"), s2);
    }

    @Test
    public void generateContainerHtml() {

        String s1 = ECharts.lineChart().generateContainerHtml("_tid");
        assertEquals("<div id='_tid' style='width: 600px;height:400px;'></div>\n", s1);

        String s2 = ECharts.lineChart().sizePixels(20, 10).generateContainerHtml("_tid");
        assertEquals("<div id='_tid' style='width: 20px;height:10px;'></div>\n", s2);
    }

    @Test
    public void generateScriptHtml_Title() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("title: {"), s1);
        assertFalse(s1.contains("text: 'My chart'"), s1);

        String s2 = ECharts.lineChart().title("My chart").generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("title: {"), s2);
        assertTrue(s2.contains("text: 'My chart'"), s2);

        String s3 = ECharts.lineChart("My chart").generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("title: {"), s3);
        assertTrue(s3.contains("text: 'My chart'"), s3);
    }

    @Test
    public void generateScriptHtml_DarkTheme() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("), 'dark');"), s1);

        String s2 = ECharts.lineChart().darkTheme().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("), 'dark');"), s2);
    }

    @Test
    public void generateScriptHtml_xAxisData() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("data: ['1','2','3']"), s1);

        String s2 = ECharts.lineChart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("data: ['1','2','3']"), s2);

        String s3 = ECharts.lineChart().xAxis("x").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("data: ['A','B','C']"), s3);
    }

    @Test
    public void generateScriptHtml_xAxisBoundaryGap() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("boundaryGap: false,"), s1);

        String s2 = ECharts.lineChart().xAxis("x", AxisOpts.create().boundaryGap(false)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("boundaryGap: false,"), s2);

        String s3 = ECharts.lineChart().xAxis("x", AxisOpts.create().boundaryGap(true)).generateScriptHtml("_tid", df1);
        assertFalse(s3.contains("boundaryGap: false,"), s3);
    }

    @Test
    public void generateScriptHtml_yAxis() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df1);
        assertTrue(s1.contains("yAxis: {"), s1);

        String s2 = ECharts.lineChart().yAxisOpts(AxisOpts.create()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("yAxis: {"), s2);
    }

    @Test
    public void generateScriptHtml_yAxis_Formatter() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("axisLabel: {"), s1);
        assertFalse(s1.contains("formatter:"), s1);

        String s2 = ECharts.lineChart().yAxisOpts(AxisOpts.create().axisLabel(AxisLabelOpts.create().formatter("{value} cm"))).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("axisLabel: {"), s2);
        assertTrue(s2.contains("formatter: '{value} cm'"), s2);
    }

    @Test
    public void generateScriptHtml_Legend() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("legend: {}"), s1);

        String s2 = ECharts.lineChart().legend().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("legend: {}"), s2);
    }

    @Test
    public void generateScriptHtml_Data() {

        String s1 = ECharts.lineChart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("series: ["), s1);

        String s2 = ECharts.lineChart().series("y2").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("data: [20,25,28],"), s2);

        String s3 = ECharts.lineChart().series("y2", "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("data: [20,25,28],"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
        assertTrue(s3.contains("data: [10,11,14],"), s3);
    }

    @Test
    public void generateScriptHtml_SeriesType() {

        String s1 = ECharts.lineChart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("type: 'line'"), s1);

        String s2 = ECharts.lineChart().series("y1", SeriesOpts.bar()).generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("type: 'bar'"), s2);

        String s3 = ECharts.barChart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("type: 'bar'"), s3);
    }

    @Test
    public void generateScriptHtml_SeriesAreaStyle() {

        String s1 = ECharts.lineChart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("areaStyle"), s1);

        String s2 = ECharts.lineChart().series("y1", SeriesOpts.line().areaStyle()).generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("areaStyle: {}"), s2);
    }

    @Test
    public void generateScriptHtml_SeriesStack() {

        String s1 = ECharts.lineChart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("stack:"), s1);

        String s2 = ECharts.lineChart().series("y1", SeriesOpts.line().stack()).generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("stack: 'total'"), s2);
    }

    @Test
    public void generateScriptHtml_SeriesSmooth() {

        String s1 = ECharts.lineChart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("smooth"), s1);

        String s2 = ECharts.lineChart().series("y1", SeriesOpts.line().smooth()).generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("smooth: true,"), s2);
    }

}
