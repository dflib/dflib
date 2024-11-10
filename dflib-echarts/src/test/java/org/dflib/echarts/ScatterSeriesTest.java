package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.dflib.echarts.EChartTestDatasets.*;

public class ScatterSeriesTest {

    @Test
    public void symbolSize() {

        String s1 = ECharts.chart().series(SeriesOpts.ofScatter(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("symbolSize"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatter().symbolSize(56), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("symbolSize: 56,"), s2);
    }

    @Test
    public void itemStyle() {

        ScatterItemStyle style = ScatterItemStyle.of()
                .color("#ffffff")
                .borderColor("#eeeeeee")
                .borderWidth(2)
                .borderType(LineType.dotted)
                .opacity(0.55);

        String s1 = ECharts.chart().series(SeriesOpts.ofScatter(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("itemStyle"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatter().itemStyle(style), "y1").generateScriptHtml("_tid", df2);

        assertTrue(s2.contains("itemStyle"), s2);
        assertTrue(s2.contains("color: '#ffffff',"), s2);
        assertTrue(s2.contains("borderColor: '#eeeeeee',"), s2);
        assertTrue(s2.contains("borderWidth: 2,"), s2);
        assertTrue(s2.contains("borderType: 'dotted'"), s2);
        assertTrue(s2.contains("opacity: 0.55"), s2);
    }
}
