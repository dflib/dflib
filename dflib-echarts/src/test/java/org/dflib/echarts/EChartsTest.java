package org.dflib.echarts;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EChartsTest {

    static final DataFrame df1 = DataFrame.foldByRow("y", "x").of(14, "C");

    static final DataFrame df2 = DataFrame.foldByRow("y1", "y2", "x").of(
            10, 20, "A",
            11, 25, "B",
            14, 28, "C");

    static final DataFrame df3 = DataFrame.foldByRow("t", "y1", "y2", "x").of(
            LocalDate.of(2022, 1, 1), 10, 20, "A",
            LocalDate.of(2022, 2, 1), 11, 25, "B",
            LocalDate.of(2022, 3, 1), 14, 28, "C");

    @Test
    public void plot() {
        EChartHtml ch = ECharts.chart().xAxis("x").series("y1", "y2").plot(df2);
        assertTrue(ch.getExternalScript().contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js'></script>"), ch.getExternalScript());
        assertTrue(ch.getContainer().contains("<div id='dfl_ech_"), ch.getContainer());
        assertTrue(ch.getScript().contains("['x','A','B','C'],"), ch.getScript());
        assertTrue(ch.getScript().contains("['y1',10,11,14],"), ch.getScript());
        assertTrue(ch.getScript().contains("['y2',20,25,28]"), ch.getScript());
        assertTrue(ch.getScript().contains("name: 'y1',"), ch.getScript());
        assertTrue(ch.getScript().contains("name: 'y2',"), ch.getScript());
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
        assertFalse(s1.contains("'dark'"), s1);

        String s2 = ECharts.chart().darkTheme().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("'dark',"), s2);
    }

    @Test
    public void generateScriptHtml_SvgRenderer() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }

    @Test
    public void generateScriptHtml_SvgRenderer_DarkTheme() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("'dark'"), s1);
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().darkTheme().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("'dark',"), s2);
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }

    @Test
    public void generateScriptHtml_xAxisData() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("['labels',1,2,3]"), s1);
        assertTrue(s1.contains("type: 'category'"), s1);

        String s2 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("['labels',1,2,3]"), s2);
        assertTrue(s2.contains("type: 'category'"), s2);

        String s3 = ECharts.chart().xAxis("x").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("['x','A','B','C']"), s3);
        assertTrue(s3.contains("type: 'category'"), s3);
    }

    @Test
    public void generateScriptHtml_xAxisBoundaryGap() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("boundaryGap: false,"), s1);

        String s2 = ECharts.chart().xAxis("x", Axis.defaultX().boundaryGap(false)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("boundaryGap: false,"), s2);

        String s3 = ECharts.chart().xAxis("x", Axis.defaultX().boundaryGap(true)).generateScriptHtml("_tid", df1);
        assertFalse(s3.contains("boundaryGap: false,"), s3);
    }

    @Test
    public void generateScriptHtml_yAxis() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertTrue(s1.contains("yAxis: {"), s1);

        String s2 = ECharts.chart().yAxis(Axis.defaultY()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("yAxis: {"), s2);
    }

    @Test
    public void generateScriptHtml_yAxis_Formatter() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("axisLabel: {"), s1);
        assertFalse(s1.contains("formatter:"), s1);

        String s2 = ECharts.chart().yAxis(Axis.defaultY().label(AxisLabel.create().formatter("{value} cm"))).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("axisLabel: {"), s2);
        assertTrue(s2.contains("formatter: '{value} cm'"), s2);
    }

    @Test
    public void generateScriptHtml_Legend() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("legend: {}"), s1);

        String s2 = ECharts.chart().legend().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("legend: {}"), s2);
    }

    @Test
    public void generateScriptHtml_SeriesEmpty() {
        String s1 = ECharts.chart().generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("series: ["), s1);
    }

    @Test
    public void generateScriptHtml_Series() {

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
    public void generateScriptHtml_TimeSeries() {

        String s2 = ECharts.chart().series("y2").xAxis("t", Axis.time()).generateScriptHtml("_tid", df3);
        assertTrue(s2.contains("series: ["), s2);
        assertTrue(s2.contains("name: 'y2',"), s2);
        assertTrue(s2.contains("['t','2022-01-01','2022-02-01','2022-03-01'],"), s2);
        assertTrue(s2.contains("['y2',20,25,28]"), s2);

        String s3 = ECharts.chart().series("y2", "y1").xAxis("t", Axis.time()).generateScriptHtml("_tid", df3);
        assertTrue(s3.contains("['t','2022-01-01','2022-02-01','2022-03-01'],"), s3);
        assertTrue(s3.contains("['y2',20,25,28],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("series: ["), s3);
        assertTrue(s3.contains("name: 'y2',"), s3);
        assertTrue(s3.contains("name: 'y1',"), s3);
    }

    @Test
    public void generateScriptHtml_SeriesType() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s1.contains("type: 'line'"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.bar(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("type: 'bar'"), s2);

        String s3 = ECharts.chart().defaultSeriesOpts(SeriesOpts.bar()).series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("type: 'bar'"), s3);
    }

    @Test
    public void generateScriptHtml_SeriesAreaStyle() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("areaStyle"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().areaStyle(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("areaStyle: {}"), s2);
    }

    @Test
    public void generateScriptHtml_SeriesStack() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("stack:"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().stack(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("stack: 'total'"), s2);
    }

    @Test
    public void generateScriptHtml_SeriesSmooth() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("smooth"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.line().smooth(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("smooth: true,"), s2);
    }

    @Test
    public void generateScriptHtml_toolbox() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("toolbox: {"), s1);

        String s2 = ECharts.chart().toolbox(Toolbox.create().featureSaveAsImage()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("toolbox: {"), s2);
        assertTrue(s2.contains("saveAsImage: {"), s2);
    }

    @Test
    public void generateScriptHtml_toolbox_saveAsImage() {

        String s2 = ECharts.chart().toolbox(Toolbox.create().featureSaveAsImage()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("toolbox: {"), s2);
        assertTrue(s2.contains("saveAsImage: {"), s2);
        assertFalse(s2.contains("pixelRatio: 2"), s2);

        String s3 = ECharts.chart().toolbox(Toolbox.create().featureSaveAsImage(SaveAsImage.create().pixelRatio(2))).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("toolbox: {"), s3);
        assertTrue(s3.contains("saveAsImage: {"), s3);
        assertTrue(s3.contains("pixelRatio: 2"), s3);
    }

}
