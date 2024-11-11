package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.dflib.echarts.EChartTestDatasets.*;

public class EChartTest {

    @Test
    public void plot() {
        EChartHtml ch = ECharts.chart().xAxis("x").series("y1", "y2").plot(df2);
        assertTrue(ch.getExternalScript().contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.1/dist/echarts.min.js'></script>"), ch.getExternalScript());
        assertTrue(ch.getChartDiv().contains("<div id='dfl_ech_"), ch.getChartDiv());
        assertTrue(ch.getChartScript().contains("['L0','A','B','C'],"), ch.getChartScript());
        assertTrue(ch.getChartScript().contains("['y1',10,11,14],"), ch.getChartScript());
        assertTrue(ch.getChartScript().contains("['y2',20,25,28]"), ch.getChartScript());
        assertTrue(ch.getChartScript().contains("name: 'y1',"), ch.getChartScript());
        assertTrue(ch.getChartScript().contains("name: 'y2',"), ch.getChartScript());
    }

    @Test
    public void scriptUrl() {

        assertEquals("https://cdn.jsdelivr.net/npm/echarts@5.5.1/dist/echarts.min.js", ECharts.chart().scriptUrl());
        assertEquals("https://example.org/x.js", ECharts.chart().scriptUrl("https://example.org/x.js").scriptUrl());
    }

    @Deprecated
    @Test
    public void generateExternalScriptHtml() {

        String s1 = ECharts.chart().generateExternalScriptHtml();
        assertTrue(s1.contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@5.5.1/dist/echarts.min.js'></script>"), s1);

        String s2 = ECharts.chart().scriptUrl("https://example.org/x.js").generateExternalScriptHtml();
        assertTrue(s2.contains("<script type='text/javascript' src='https://example.org/x.js'></script>"), s2);
    }

    @Test
    public void generateContainerHtml() {

        String s1 = ECharts.chart().generateContainerHtml("_tid");
        assertEquals("<div id='_tid' class='dfl_ech' style='width: 600px;height:400px;'></div>", s1);

        String s2 = ECharts.chart().sizePx(20, 10).generateContainerHtml("_tid");
        assertEquals("<div id='_tid' class='dfl_ech' style='width: 20px;height:10px;'></div>", s2);
    }

    @Test
    public void darkTheme() {

        String s1 = ECharts.chart().generateScript("_tid", df1);
        assertFalse(s1.contains("'dark'"), s1);

        String s2 = ECharts.chart().darkTheme().generateScript("_tid", df1);
        assertTrue(s2.contains("'dark',"), s2);
    }

    @Test
    public void svgRenderer() {

        String s1 = ECharts.chart().generateScript("_tid", df1);
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().generateScript("_tid", df1);
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }

    @Test
    public void svgRenderer_DarkTheme() {

        String s1 = ECharts.chart().generateScript("_tid", df1);
        assertFalse(s1.contains("'dark'"), s1);
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().darkTheme().generateScript("_tid", df1);
        assertTrue(s2.contains("'dark',"), s2);
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }
}
