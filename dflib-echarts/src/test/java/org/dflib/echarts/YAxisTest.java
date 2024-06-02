package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YAxisTest extends GenerateScriptHtmlTest {

    @Test
    public void yAxis() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertTrue(s1.contains("yAxis: ["), s1);

        String s2 = ECharts.chart().yAxis(YAxis.ofDefault()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("yAxis: ["), s2);
    }

    @Test
    public void yAxes() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertTrue(s1.contains("yAxis: ["), s1);

        String s2 = ECharts.chart().yAxes(
                YAxis.ofDefault(),
                YAxis.ofTime()
        ).generateScriptHtml("_tid", df1);

        assertTrue(s2.contains("yAxis: ["), s2);
        assertTrue(s2.contains("type: 'value'"), s2);
        assertTrue(s2.contains("type: 'time'"), s2);
    }

    @Test
    public void name() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("name:"), s1);

        String s2 = ECharts.chart().yAxis(YAxis.ofDefault().name("I'm a Y")).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("name: 'I\\'m a Y'"), s2);
    }

    @Test
    public void formatter() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("axisLabel: {"), s1);
        assertFalse(s1.contains("formatter:"), s1);

        String s2 = ECharts.chart().yAxis(YAxis.ofDefault().label(AxisLabel.of().formatter("{value} cm"))).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("axisLabel: {"), s2);
        assertTrue(s2.contains("formatter: '{value} cm'"), s2);
    }

    @Test
    public void position() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("position:"), s1);

        String s2 = ECharts.chart().yAxis(YAxis.ofDefault().left()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("position: 'left'"), s2);
    }

    @Test
    public void offset() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("offset:"), s1);

        String s2 = ECharts.chart().yAxis(YAxis.ofDefault().offset(-6)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("offset: -6"), s2);
    }

    @Test
    public void alignTicks() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("alignTicks:"), s1);

        String s2 = ECharts.chart().yAxis(YAxis.ofDefault().alignTicks(true)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("alignTicks: true,"), s2);

        String s3 = ECharts.chart().yAxis(YAxis.ofDefault().alignTicks(false)).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("alignTicks: false,"), s3);
    }

    @Test
    public void axisLine() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("axisLine:"), s1);
        assertFalse(s1.contains("show:"), s1);

        String s2 = ECharts.chart().yAxis(YAxis.ofDefault().line(AxisLine.of())).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("axisLine: {"), s2);
        assertTrue(s2.contains("show: true"), s2);

        String s3 = ECharts.chart().yAxis(YAxis.ofDefault().line(AxisLine.of().show(false))).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("axisLine: {"), s3);
        assertTrue(s3.contains("show: false"), s3);
    }
}
