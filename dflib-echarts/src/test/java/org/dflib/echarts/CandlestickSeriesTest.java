package org.dflib.echarts;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CandlestickSeriesTest {

    protected static final DataFrame df = DataFrame.foldByRow("open", "close", "low", "high", "on", "on2").of(
            15, 19, 14, 21, "2024-08-12", "2024-07-12",
            18, 11, 11, 19, "2024-08-13", "2024-07-13",
            15, 20, 13, 22, "2024-08-14", "2024-07-14");

    @Test
    public void xAxisIndex() {

        String s1 = ECharts.chart()
                .series(SeriesOpts.ofCandlestick(), "open", "close", "low", "high")
                .generateScriptHtml("_tid", df);
        assertFalse(s1.contains("xAxisIndex"), s1);

        String s2 = ECharts.chart()
                .xAxis("on")
                .xAxis("on2")
                .series(SeriesOpts.ofCandlestick().xAxisIndex(1), "open", "close", "low", "high")
                .generateScriptHtml("_tid", df);
        assertTrue(s2.contains("['L0','2024-08-12','2024-08-13','2024-08-14'],"), s2);
        assertTrue(s2.contains("['L1','2024-07-12','2024-07-13','2024-07-14']"), s2);
        assertTrue(s2.contains("type: 'candlestick'"), s2);
        assertTrue(s2.contains("xAxisIndex: 1,"), s2);
        assertTrue(s2.contains("x: 1,"), s2);
        assertTrue(s2.contains("y: [2,3,4,5]"), s2);
    }

    @Test
    public void data() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df);
        assertTrue(s1.contains("dataset"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofCandlestick(), "open", "close", "low", "high").generateScriptHtml("_tid", df);
        assertTrue(s2.contains("dataset"), s2);
        assertTrue(s2.contains("['L0',1,2,3]"), s2);
        assertTrue(s2.contains("['open',15,18,15],"), s2);
        assertTrue(s2.contains("['close',19,11,20],"), s2);
        assertTrue(s2.contains("['low',14,11,13],"), s2);
        assertTrue(s2.contains("['high',21,19,22]"), s2);

        assertTrue(s2.contains("x: 0,"), s2);
        assertTrue(s2.contains("y: [1,2,3,4]"), s2);
    }
}
