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
}
