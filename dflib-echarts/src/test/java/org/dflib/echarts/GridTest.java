package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.dflib.echarts.EChartTestDatasets.*;

public class GridTest {

    @Test
    public void grid() {

        String s1 = ECharts.chart().generateScriptHtml("_tid", df1);
        assertFalse(s1.contains("grid:"), s1);

        String s2 = ECharts.chart().grid(Grid.of()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("grid: ["), s2);

        String s3 =  ECharts.chart().grid(Grid.of().leftLeft()).grid(Grid.of().heightPx(1)).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("grid: ["), s3);
        assertTrue(s3.contains("left: 'left'"), s3);
        assertTrue(s3.contains("height: 1"), s3);
    }

    @Test
    public void gridLeft() {

        String s1 = ECharts.chart().grid(Grid.of()).generateScriptHtml("_tid", df1);
        assertTrue(s1.contains("grid: ["), s1);
        assertFalse(s1.contains("left:"), s1);

        String s2 =  ECharts.chart().grid(Grid.of().leftLeft()).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("left: 'left'"), s2);

        String s3 =  ECharts.chart().grid(Grid.of().leftCenter()).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("left: 'center'"), s3);

        String s4 =  ECharts.chart().grid(Grid.of().leftPx(1)).generateScriptHtml("_tid", df1);
        assertTrue(s4.contains("left: 1"), s4);

        String s5 =  ECharts.chart().grid(Grid.of().leftPct(10.1)).generateScriptHtml("_tid", df1);
        assertTrue(s5.contains("left: '10.1%'"), s5);
    }

    @Test
    public void gridRight() {

        String s1 = ECharts.chart().grid(Grid.of()).generateScriptHtml("_tid", df1);
        assertTrue(s1.contains("grid: ["), s1);
        assertFalse(s1.contains("right:"), s1);

        String s2 =  ECharts.chart().grid(Grid.of().rightPx(1)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("right: 1"), s2);

        String s3 =  ECharts.chart().grid(Grid.of().rightPct(10.1)).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("right: '10.1%'"), s3);
    }

    @Test
    public void gridWidth() {

        String s1 = ECharts.chart().grid(Grid.of()).generateScriptHtml("_tid", df1);
        assertTrue(s1.contains("grid: ["), s1);
        assertFalse(s1.contains("width:"), s1);

        String s2 =  ECharts.chart().grid(Grid.of().widthPx(1)).generateScriptHtml("_tid", df1);
        assertTrue(s2.contains("width: 1"), s2);

        String s3 =  ECharts.chart().grid(Grid.of().widthPct(10.1)).generateScriptHtml("_tid", df1);
        assertTrue(s3.contains("width: '10.1%'"), s3);
    }
}
