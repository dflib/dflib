package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.dflib.echarts.EChartTestDatasets.*;

public class OptionTest {

    @Test
    public void title_default() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("title: {"), s1);
        assertFalse(s1.contains("text: 'My chart'"), s1);

        String s2 = ECharts.chart().title("My chart").plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("title: {"), s2);
        assertTrue(s2.contains("text: 'My chart'"), s2);

        String s3 = ECharts.chart("My chart").plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("title: {"), s3);
        assertTrue(s3.contains("text: 'My chart'"), s3);
    }

    @Test
    public void title() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("title: {"), s1);

        String s2 = ECharts.chart().title(
                Title.of("My chart").subtext("about my chart").bottomPct(4.5).leftLeft().topPx(10)
        ).plot(df1, "_tid").renderChartScript();

        assertTrue(s2.contains("title: {"), s2);
        assertTrue(s2.contains("text: 'My chart'"), s2);
        assertTrue(s2.contains("subtext: 'about my chart'"), s2);
        assertTrue(s2.contains("bottom: '4.5%'"), s2);
        assertTrue(s2.contains("left: 'left'"), s2);
        assertTrue(s2.contains("top: 10"), s2);
    }

    @Test
    public void legend_default() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("legend:"), s1);

        String s2 = ECharts.chart().legend().plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("legend: {"), s2);
        assertTrue(s2.contains("type: 'plain'"), s2);
    }

    @Test
    public void legend() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("legend:"), s1);

        String s2 = ECharts.chart().legend(
                Legend.ofScroll().vertical().bottomPct(4.5).leftLeft().topPx(10).unselected("y")
        ).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("legend: {"), s2);
        assertTrue(s2.contains("type: 'scroll'"), s2);
        assertTrue(s2.contains("orient: 'vertical'"), s2);
        assertTrue(s2.contains("bottom: '4.5%'"), s2);
        assertTrue(s2.contains("left: 'left'"), s2);
        assertTrue(s2.contains("top: 10"), s2);
        assertTrue(s2.contains("selected: {"), s2);
        assertTrue(s2.contains("'y': false"), s2);
    }

    @Test
    public void toolbox() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("toolbox: {"), s1);

        String s2 = ECharts.chart().toolbox(Toolbox.of().featureSaveAsImage()).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("toolbox: {"), s2);
        assertTrue(s2.contains("saveAsImage: {"), s2);
    }

    @Test
    public void toolbox_saveAsImage() {

        String s2 = ECharts.chart().toolbox(Toolbox.of().featureSaveAsImage()).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("toolbox: {"), s2);
        assertTrue(s2.contains("saveAsImage: {"), s2);
        assertFalse(s2.contains("pixelRatio: 2"), s2);

        String s3 = ECharts.chart().toolbox(Toolbox.of().featureSaveAsImage(SaveAsImage.of().pixelRatio(2))).plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("toolbox: {"), s3);
        assertTrue(s3.contains("saveAsImage: {"), s3);
        assertTrue(s3.contains("pixelRatio: 2"), s3);
    }

    @Test
    public void tooltip() {

        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("tooltip: {"), s1);

        String s2 = ECharts.chart().tooltip(Tooltip.ofAxis().axisPointerCross()).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("tooltip: {"), s2);
        assertTrue(s2.contains("trigger: 'axis'"), s2);
        assertTrue(s2.contains("axisPointer: {"), s2);
        assertTrue(s2.contains("type: 'cross',"), s2);
    }
}
