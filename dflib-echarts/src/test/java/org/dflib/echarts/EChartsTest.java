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
        EChart ch = ECharts.chart().xAxis("x").data("y1", "y2").plot(df2);
        assertTrue(ch.getExternalScript().contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js'></script>"), ch.getExternalScript());
        assertTrue(ch.getContainer().contains("<div id='dfl_ech_"), ch.getContainer());
        assertTrue(ch.getScript().contains("data: ['A','B','C']"), ch.getScript());
        assertTrue(ch.getScript().contains("data: [10,11,14]"), ch.getScript());
        assertTrue(ch.getScript().contains("data: [20,25,28]"), ch.getScript());
    }

    @Test
    public void generateExternalScriptHtml() {

        String s1 = ECharts.chart().generateExternalScriptHtml();
        assertTrue(s1.contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js'></script>"), s1);

        String s2 = ECharts.chart().scriptUrl("https://example.org/x.js").generateExternalScriptHtml();
        assertTrue(s2.contains("<script type='text/javascript' src='https://example.org/x.js'></script>"), s2);
    }

    @Test
    public void generateContainerHtml() {

        String s1 = ECharts.chart().generateContainerHtml("_tid");
        assertEquals("<div id='_tid' style='width: 600px;height:400px;'></div>\n", s1);

        String s2 = ECharts.chart().sizePixels(20, 10).generateContainerHtml("_tid");
        assertEquals("<div id='_tid' style='width: 20px;height:10px;'></div>\n", s2);
    }

    @Test
    public void generateScriptHtml_Title() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("title: {"), s1);
        assertFalse(s1.contains("text: 'My chart'"), s1);

        String s2 = ECharts.chart().title("My chart").generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("title: {"), s2);
        assertTrue(s2.contains("text: 'My chart'"), s2);

        String s3 = ECharts.chart("My chart").generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("title: {"), s3);
        assertTrue(s3.contains("text: 'My chart'"), s3);
    }

    @Test
    public void generateScriptHtml_DarkTheme() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("), 'dark');"), s1);

        String s2 = ECharts.chart().darkTheme().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("), 'dark');"), s2);
    }

    @Test
    public void generateScriptHtml_xAxisData() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("data: ['1','2','3']"), s1);

        String s2 = ECharts.chart().data("y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("data: ['1','2','3']"), s2);

        String s3 = ECharts.chart().xAxis("x").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("data: ['A','B','C']"), s3);
    }

    @Test
    public void generateScriptHtml_xAxisNoBoundaryGap() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("boundaryGap: false,"), s1);

        String s2 = ECharts.chart().xAxisNoBoundaryGap().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("boundaryGap: false,"), s2);
    }

    @Test
    public void generateScriptHtml_Legend() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("legend: {}"), s1);

        String s2 = ECharts.chart().legend().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("legend: {}"), s2);
    }

    @Test
    public void generateScriptHtml_Data() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("series: ["), s1);

        String s2 = ECharts.chart().data("y2").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("data: [20,25,28],"), s2);

        String s3 = ECharts.chart().data("y2", "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("data: [20,25,28],"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
        assertTrue(s3.contains("data: [10,11,14],"), s3);
    }

    @Test
    public void generateScriptHtml_SeriesChartType() {

        String s1 = ECharts.chart().data("y1").generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("type: 'line'"), s1);

        String s2 = ECharts.chart().data("y1").chartType(EChartType.bar).generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("type: 'bar'"), s2);

        String s3 = ECharts.chart().data("y1").chartType("y1", EChartType.bar).generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("type: 'bar'"), s3);

        String s4 = ECharts.chart().data("y1").chartType("XX", EChartType.bar).generateScriptHtml("_tid", df2);
        assertTrue(s4.contains("type: 'line'"), s4);
    }

    @Test
    public void generateScriptHtml_SeriesAreaStyle() {

        String s1 = ECharts.chart().data("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("areaStyle"), s1);

        String s2 = ECharts.chart().data("y1").areaStyle().generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("areaStyle: {}"), s2);

        String s3 = ECharts.chart().data("y1").areaStyle("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("areaStyle: {}"), s3);

        String s4 = ECharts.chart().data("y1").areaStyle("XX").generateScriptHtml("_tid", df2);
        assertFalse(s4.contains("areaStyle"), s4);
    }

    @Test
    public void generateScriptHtml_SeriesStack() {

        String s1 = ECharts.chart().data("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("stack:"), s1);

        String s2 = ECharts.chart().data("y1").stack().generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("stack: 'total'"), s2);

        String s3 = ECharts.chart().data("y1").stack("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("stack: 'total'"), s3);

        String s4 = ECharts.chart().data("y1").stack("XX").generateScriptHtml("_tid", df2);
        assertFalse(s4.contains("stack"), s4);
    }

    @Test
    public void generateScriptHtml_SeriesSmooth() {

        String s1 = ECharts.chart().data("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("smooth"), s1);

        String s2 = ECharts.chart().data("y1").smooth().generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("smooth: true,"), s2);

        String s3 = ECharts.chart().data("y1").smooth("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("smooth: true,"), s3);

        String s4 = ECharts.chart().data("y1").smooth("XX").generateScriptHtml("_tid", df2);
        assertFalse(s4.contains("smooth"), s4);
    }

}
