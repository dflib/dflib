package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.dflib.echarts.EChartTestDatasets.*;

public class OptionTest {

    @Test
    public void title() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("title: {"), s1);
        assertFalse(s1.contains("text: 'My chart'"), s1);

        String s2 = ECharts.chart().title("My chart").generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("title: {"), s2);
        assertTrue(s2.contains("text: 'My chart'"), s2);

        String s3 = ECharts.chart("My chart").generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("title: {"), s3);
        assertTrue(s3.contains("text: 'My chart'"), s3);
    }

    @Test
    public void legend() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("legend: {}"), s1);

        String s2 = ECharts.chart().legend().generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("legend: {}"), s2);
    }


    @Test
    public void toolbox() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("toolbox: {"), s1);

        String s2 = ECharts.chart().toolbox(Toolbox.of().featureSaveAsImage()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("toolbox: {"), s2);
        assertTrue(s2.contains("saveAsImage: {"), s2);
    }

    @Test
    public void toolbox_saveAsImage() {

        String s2 = ECharts.chart().toolbox(Toolbox.of().featureSaveAsImage()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("toolbox: {"), s2);
        assertTrue(s2.contains("saveAsImage: {"), s2);
        assertFalse(s2.contains("pixelRatio: 2"), s2);

        String s3 = ECharts.chart().toolbox(Toolbox.of().featureSaveAsImage(SaveAsImage.of().pixelRatio(2))).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("toolbox: {"), s3);
        assertTrue(s3.contains("saveAsImage: {"), s3);
        assertTrue(s3.contains("pixelRatio: 2"), s3);
    }

    @Test
    public void tooltip() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("tooltip: {"), s1);

        String s2 = ECharts.chart().tooltip(Tooltip.ofAxis().axisPointerCross()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("tooltip: {"), s2);
        assertTrue(s2.contains("trigger: 'axis'"), s2);
        assertTrue(s2.contains("axisPointer: {"), s2);
        assertTrue(s2.contains("type: 'cross',"), s2);
    }
}
