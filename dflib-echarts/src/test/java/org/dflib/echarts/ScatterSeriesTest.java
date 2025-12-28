package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.dflib.echarts.EChartTestDatasets.*;

public class ScatterSeriesTest {

    @Test
    public void symbol() {

        String s1 = ECharts.chart().series("y1").plot(df2, "_tid").getChartScript();
        assertFalse(s1.contains("symbol"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatter().symbol(Symbol.triangle), "y1").plot(df2, "_tid").getChartScript();
        assertTrue(s2.contains("symbol: 'triangle',"), s2);
    }

    @Test
    public void symbolSize() {

        String s1 = ECharts.chart().series(SeriesOpts.ofScatter(), "y1").plot(df2, "_tid").getChartScript();
        assertFalse(s1.contains("symbolSize"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatter().symbolSize(56), "y1").plot(df2, "_tid").getChartScript();
        assertTrue(s2.contains("symbolSize: 56,"), s2);
    }

    @Test
    public void symbolSizeData() {

        String s1 = ECharts.chart()
                .series(SeriesOpts.ofScatter().symbolSizeData("y2"), "y1")
                .plot(df2, "_tid").getChartScript();
        assertTrue(s1.contains("symbolSize: function (vals) { return vals[1]; },"), s1);
    }

    @Test
    public void itemStyle() {

        ScatterItemStyle style = ScatterItemStyle.of()
                .color("#ffffff")
                .borderColor("#eeeeeee")
                .borderWidth(2)
                .borderType(LineType.dotted)
                .opacity(0.55);

        String s1 = ECharts.chart().series(SeriesOpts.ofScatter(), "y1").plot(df2, "_tid").getChartScript();
        assertFalse(s1.contains("itemStyle"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatter().itemStyle(style), "y1").plot(df2, "_tid").getChartScript();

        assertTrue(s2.contains("itemStyle"), s2);
        assertTrue(s2.contains("color: '#ffffff',"), s2);
        assertTrue(s2.contains("borderColor: '#eeeeeee',"), s2);
        assertTrue(s2.contains("borderWidth: 2,"), s2);
        assertTrue(s2.contains("borderType: 'dotted'"), s2);
        assertTrue(s2.contains("opacity: 0.55"), s2);
    }

    @Test
    public void itemStyleDynamicColor() {

        ScatterItemStyle style = ScatterItemStyle.of()
                .colorData("y2")
                .borderColor("#eeeeeee")
                .borderWidth(2)
                .borderType(LineType.dotted)
                .opacity(0.55);

        String s1 = ECharts.chart().series(SeriesOpts.ofScatter(), "y1").plot(df2, "_tid").getChartScript();
        assertFalse(s1.contains("itemStyle"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatter().itemStyle(style), "y1").plot(df2, "_tid").getChartScript();

        assertTrue(s2.contains("itemStyle"), s2);
        assertTrue(s2.contains("color: function (o) { return o.data[1]; },"), s2);
        assertTrue(s2.contains("borderColor: '#eeeeeee',"), s2);
        assertTrue(s2.contains("borderWidth: 2,"), s2);
        assertTrue(s2.contains("borderType: 'dotted'"), s2);
        assertTrue(s2.contains("opacity: 0.55"), s2);
    }

    @Test
    public void label() {

        String s1 = ECharts.chart().series(SeriesOpts.ofScatter(), "y1").plot(df2, "_tid").getChartScript();
        assertFalse(s1.contains("label:"), s1);
        assertFalse(s1.contains("['L1','A','B','C']"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatter().label("x"), "y1").plot(df2, "_tid").getChartScript();
        assertFalse(s2.contains("label:"), s2);
        assertTrue(s2.contains("['L1','A','B','C'],"), s2);

        String s3 = ECharts.chart().series(SeriesOpts.ofScatter().label("x", Label.ofTop().show(true)), "y1").plot(df2, "_tid").getChartScript();
        assertTrue(s3.contains("label:"), s3);
        assertTrue(s3.contains("['L1','A','B','C'],"), s3);
        assertTrue(s3.contains("formatter: '{@[1]}',"), s3);

        String s4 = ECharts.chart().series(SeriesOpts.ofScatter().label("x", Label.ofTop().formatter("{b}").show(true)), "y1").plot(df2, "_tid").getChartScript();
        assertTrue(s4.contains("label:"), s4);
        assertTrue(s4.contains("['L1','A','B','C'],"), s4);
        assertFalse(s4.contains("formatter: '{@[1]}',"), s4);
        assertTrue(s4.contains("formatter: '{b}',"), s4);

    }
}
