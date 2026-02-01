package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.dflib.echarts.EChartTestDatasets.*;

public class XAxisTest {

    @Test
    public void data() {

        String s1 = ECharts.chart().plot(df2, "_tid").renderChartScript();
        assertFalse(s1.contains("['labels',1,2,3]"), s1);
        assertTrue(s1.contains("type: 'category'"), s1);

        String s2 = ECharts.chart().series("y1").plot(df2, "_tid").renderChartScript();
        assertTrue(s2.contains("['L0',1,2,3]"), s2);
        assertTrue(s2.contains("type: 'category'"), s2);

        String s3 = ECharts.chart().xAxis("x").series(SeriesOpts.ofLine(), "y1").plot(df2, "_tid").renderChartScript();
        assertTrue(s3.contains("['L0','A','B','C']"), s3);
        assertTrue(s3.contains("type: 'category'"), s3);

        String s4 = ECharts.chart().xAxis(XAxis.ofCategory()).series("y1").plot(df2, "_tid").renderChartScript();
        assertTrue(s4.contains("['L0',1,2,3]"), s4);
        assertTrue(s4.contains("type: 'category'"), s4);
    }

    @Test
    public void xAxes() {
        String s1 = ECharts.chart()
                .xAxis("x1")
                .xAxis("x2")
                .series("y1", "y2")
                .plot(df4, "_tid").renderChartScript();
        assertTrue(s1.contains("['L0','A','B','C'],"), s1);
        assertTrue(s1.contains("['L1','X','Y','Z'],"), s1);
        assertTrue(s1.contains("type: 'category'"), s1);
        assertTrue(s1.contains("x: 0,"), s1);
        assertFalse(s1.contains("x: 1,"), s1);

        String s2 = ECharts.chart()
                .xAxis("x1")
                .xAxis("x2")
                .series(SeriesOpts.ofLine().xAxisIndex(0), "y1")
                .series(SeriesOpts.ofLine().xAxisIndex(1), "y2")
                .plot(df4, "_tid").renderChartScript();
        assertTrue(s2.contains("['L0','A','B','C'],"), s1);
        assertTrue(s2.contains("['L1','X','Y','Z'],"), s1);
        assertTrue(s2.contains("type: 'category'"), s1);
        assertTrue(s2.contains("x: 0,"), s1);
        assertTrue(s2.contains("x: 1,"), s1);
    }

    @Test
    public void boundaryGap() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("boundaryGap: false,"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().boundaryGap(false)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("boundaryGap: false,"), s2);

        String s3 = ECharts.chart().xAxis("x", XAxis.ofDefault().boundaryGap(true)).plot(df1, "_tid").renderChartScript();
        assertFalse(s3.contains("boundaryGap: false,"), s3);
    }

    @Test
    public void name() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("name:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().name("I'm an X")).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("name: 'I\\'m an X'"), s2);
    }

    @Test
    public void position() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("position:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().top()).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("position: 'top'"), s2);
    }

    @Test
    public void offset() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("offset:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().offset(-5)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("offset: -5"), s2);
    }

    @Test
    public void gridIndex() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("gridIndex:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().gridIndex(2)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("gridIndex: 2,"), s2);
    }

    @Test
    public void alignTicks() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("alignTicks:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().alignTicks(true)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("alignTicks: true,"), s2);

        String s3 = ECharts.chart().xAxis("x", XAxis.ofDefault().alignTicks(false)).plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("alignTicks: false,"), s3);
    }

    @Test
    public void axisLine() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("axisLine:"), s1);
        assertFalse(s1.contains("show:"), s1);
        assertFalse(s1.contains("onZero:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().line(AxisLine.of())).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("axisLine: {"), s2);
        assertTrue(s2.contains("show: true"), s2);
        assertFalse(s2.contains("onZero:"), s2);

        String s3 = ECharts.chart().xAxis("x", XAxis.ofDefault().line(AxisLine.of().show(false))).plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("axisLine: {"), s3);
        assertTrue(s3.contains("show: false"), s3);
        assertFalse(s3.contains("onZero:"), s3);

        String s4 = ECharts.chart().xAxis("x", XAxis.ofDefault().line(AxisLine.of().onZero(false))).plot(df1, "_tid").renderChartScript();
        assertTrue(s4.contains("axisLine: {"), s4);
        assertTrue(s4.contains("show: true"), s4);
        assertTrue(s4.contains("onZero: false"), s4);

    }


    @Test
    public void label_formatter() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("axisLabel: {"), s1);
        assertFalse(s1.contains("formatter:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().label(AxisLabel.of().formatter("{value} cm"))).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("axisLabel: {"), s2);
        assertTrue(s2.contains("formatter: '{value} cm'"), s2);
    }

    @Test
    public void label_rotate() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("axisLabel: {"), s1);
        assertFalse(s1.contains("rotate:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().label(AxisLabel.of().rotate(-20))).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("axisLabel: {"), s2);
        assertTrue(s2.contains("rotate: -20"), s2);
    }

    @Test
    public void label_font() {
        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("axisLabel: {"), s1);
        assertFalse(s1.contains("fontSize:"), s1);
        assertFalse(s1.contains("fontStyle:"), s1);
        assertFalse(s1.contains("fontFamily:"), s1);
        assertFalse(s1.contains("fontWeight:"), s1);

        String s2 = ECharts.chart().xAxis("x", XAxis.ofDefault().label(AxisLabel.of()
                .fontSize(18)
                .fontStyle(FontStyle.italic)
                .fontFamily("monospace")
                .fontWeight(200))).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("axisLabel: {"), s2);
        assertTrue(s2.contains("fontSize: 18"), s2);
        assertTrue(s2.contains("fontStyle: 'italic'"), s2);
        assertTrue(s2.contains("fontFamily: 'monospace'"), s2);
        assertTrue(s2.contains("fontWeight: 200"), s2);
    }
}
