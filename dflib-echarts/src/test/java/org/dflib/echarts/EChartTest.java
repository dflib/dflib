package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EChartTest extends GenerateScriptHtmlTest {

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
    public void darkTheme() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("'dark'"), s1);

        String s2 = ECharts.chart().darkTheme().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("'dark',"), s2);
    }

    @Test
    public void svgRenderer() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }

    @Test
    public void svgRenderer_DarkTheme() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("'dark'"), s1);
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().darkTheme().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("'dark',"), s2);
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }
}
