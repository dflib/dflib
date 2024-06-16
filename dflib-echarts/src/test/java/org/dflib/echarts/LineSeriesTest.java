package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LineSeriesTest extends GenerateScriptHtmlTest {

    @Test
    public void areaStyle() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("areaStyle"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofLine().areaStyle(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("areaStyle: {}"), s2);
    }

    @Test
    public void stack() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("stack:"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofLine().stack(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("stack: 'total'"), s2);
    }

    @Test
    public void smooth() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("smooth"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofLine().smooth(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("smooth: true,"), s2);
    }

    @Test
    public void showSymbol() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("showSymbol"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofLine().showSymbol(false), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("showSymbol: false,"), s2);
    }

    @Test
    public void label() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("label:"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofLine().label(Label.ofInside()), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("label: {"), s2);
        assertTrue(s2.contains("position: 'inside'"), s2);
    }

    @Test
    public void label_Show() {

        String s1 = ECharts.chart().series("y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("label:"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofLine().label(Label.ofLeft().show(false)), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("label: {"), s2);
        assertTrue(s2.contains("show: false"), s2);
    }
}
