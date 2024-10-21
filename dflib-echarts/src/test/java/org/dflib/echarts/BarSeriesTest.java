package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BarSeriesTest extends GenerateScriptHtmlTest {

    @Test
    public void test() {

        String s2 = ECharts.chart().series(SeriesOpts.ofBar(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("type: 'bar'"), s2);

        String s3 = ECharts.chart().series(SeriesOpts.ofBar(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("type: 'bar'"), s3);
    }

    @Test
    public void stack() {

        String s2 = ECharts.chart().series(SeriesOpts.ofBar(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s2.contains("stack: "), s2);

        String s3 = ECharts.chart().series(SeriesOpts.ofBar().stack(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("stack: 'total'"), s3);
    }

    @Test
    public void itemStyle() {

        BarItemStyle style = BarItemStyle.of()
                .color("#ffffff")
                .borderColor("#eeeeeee")
                .borderWidth(2)
                .borderRadius(6, 5, 4, 7);

        String s1 = ECharts.chart().series(SeriesOpts.ofBar(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("itemStyle"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofBar().itemStyle(style), "y1").generateScriptHtml("_tid", df2);

        assertTrue(s2.contains("type: 'bar'"), s2);
        assertTrue(s2.contains("itemStyle"), s2);
        assertTrue(s2.contains("color: '#ffffff',"), s2);
        assertTrue(s2.contains("borderColor: '#eeeeeee',"), s2);
        assertTrue(s2.contains("borderRadius: [6,5,4,7],"), s2);
        assertTrue(s2.contains("borderWidth: 2"), s2);
    }
}
