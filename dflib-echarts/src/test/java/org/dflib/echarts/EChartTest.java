package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.dflib.echarts.EChartTestDatasets.*;

public class EChartTest {

    @Deprecated
    @Test
    public void plot_getExternalScript() {
        EChartHtml ch = ECharts.chart().xAxis("x").series("y1", "y2").plot(df2);
        assertTrue(ch.getExternalScript().contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@6.0.0/dist/echarts.min.js'></script>"), ch.getExternalScript());
    }

    @Test
    public void plot() {
        EChartHtml ch = ECharts.chart().xAxis("x").series("y1", "y2").plot(df2);
        assertTrue(ch.renderChartDiv().contains("<div id='dfl_ech_"), ch.renderChartDiv());
        assertTrue(ch.renderChartScript().contains("['L0','A','B','C'],"), ch.renderChartScript());
        assertTrue(ch.renderChartScript().contains("['y1',10,11,14],"), ch.renderChartScript());
        assertTrue(ch.renderChartScript().contains("['y2',20,25,28]"), ch.renderChartScript());
        assertTrue(ch.renderChartScript().contains("name: 'y1',"), ch.renderChartScript());
        assertTrue(ch.renderChartScript().contains("name: 'y2',"), ch.renderChartScript());
    }

    @Test
    public void plotWithId() {
        EChartHtml ch = ECharts.chart().xAxis("x").series("y1", "y2").plot(df2, "dfl_t_123");
        assertEquals("""
                <div id='dfl_t_123' class='dfl_ech' style='width: 600px;height:400px;'></div>""", ch.renderChartDiv());
        assertEquals("""
                var chart_dfl_t_123 = echarts.init(document.getElementById('dfl_t_123'),null,);\
                var option_dfl_t_123 = {dataset: {source: [['L0','A','B','C'],['y1',10,11,14],['y2',20,25,28]]},\
                xAxis: [{type: 'category'},],yAxis: [{type: 'value'},],series: [{name: 'y1',encode: {x: 0,y: 1,},\
                seriesLayoutBy: 'row',type: 'line'},{name: 'y2',encode: {x: 0,y: 2,},\
                seriesLayoutBy: 'row',type: 'line'},]};\
                option_dfl_t_123 && chart_dfl_t_123.setOption(option_dfl_t_123);""", ch.renderChartScript());
    }

    @Test
    public void plotDoNotMinifyJS() {
        EChartHtml ch = ECharts.chart().xAxis("x").series("y1", "y2").plot(df2, "dfl_t_123").minifyJS(false);
        assertEquals("""
                <div id='dfl_t_123' class='dfl_ech' style='width: 600px;height:400px;'></div>""", ch.renderChartDiv());
        assertEquals("""
                var chart_dfl_t_123 = echarts.init(
                        document.getElementById('dfl_t_123'),
                        null,
                   );
                    var option_dfl_t_123 = {
                        dataset: {
                            source: [
                                ['L0','A','B','C'],
                                ['y1',10,11,14],
                                ['y2',20,25,28]
                            ]
                        },
                        xAxis: [
                        {
                            type: 'category'
                        },
                        ],
                        yAxis: [
                        {
                            type: 'value'
                        },
                        ],
                        series: [
                            {
                                name: 'y1',
                                encode: {
                                    x: 0,
                                    y: 1,
                                },
                                seriesLayoutBy: 'row',
                                type: 'line'
                            },
                            {
                                name: 'y2',
                                encode: {
                                    x: 0,
                                    y: 2,
                                },
                                seriesLayoutBy: 'row',
                                type: 'line'
                            },
                        ]
                    };
                    option_dfl_t_123 && chart_dfl_t_123.setOption(option_dfl_t_123);
                """, ch.renderChartScript());
    }

    @Test
    public void scriptUrl() {

        assertEquals("https://cdn.jsdelivr.net/npm/echarts@6.0.0/dist/echarts.min.js", ECharts.chart().echartsUrl());
        assertEquals("https://example.org/x.js", ECharts.chart().scriptUrl("https://example.org/x.js").echartsUrl());
    }

    @Deprecated
    @Test
    public void generateExternalScriptHtml() {

        String s1 = ECharts.chart().generateExternalScriptHtml();
        assertTrue(s1.contains("<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/echarts@6.0.0/dist/echarts.min.js'></script>"), s1);

        String s2 = ECharts.chart().scriptUrl("https://example.org/x.js").generateExternalScriptHtml();
        assertTrue(s2.contains("<script type='text/javascript' src='https://example.org/x.js'></script>"), s2);
    }

    @Deprecated
    @Test
    public void generateContainerHtml() {

        String s1 = ECharts.chart().generateContainerHtml("_tid");
        assertEquals("<div id='_tid' class='dfl_ech' style='width: 600px;height:400px;'></div>", s1);

        String s2 = ECharts.chart().sizePx(20, 10).generateContainerHtml("_tid");
        assertEquals("<div id='_tid' class='dfl_ech' style='width: 20px;height:10px;'></div>", s2);
    }

    @Test
    public void darkTheme() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("'dark'"), s1);

        String s2 = ECharts.chart().darkTheme().plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("'dark',"), s2);
    }

    @Test
    public void svgRenderer() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }

    @Test
    public void svgRenderer_DarkTheme() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("'dark'"), s1);
        assertFalse(s1.contains("renderer: 'svg'"), s1);

        String s2 = ECharts.chart().renderAsSvg().darkTheme().plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("'dark',"), s2);
        assertTrue(s2.contains("renderer: 'svg'"), s2);
    }
}
