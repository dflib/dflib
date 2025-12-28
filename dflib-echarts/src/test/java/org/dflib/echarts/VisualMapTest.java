package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df1;
import static org.dflib.echarts.EChartTestDatasets.df4;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VisualMapTest {


    @Test
    public void visualMap() {
        String s1 = ECharts.chart()
                .visualMap(VisualMap.ofContinuous())
                .plot(df4, "_tid").getChartScript();

        assertTrue(s1.contains("visualMap: ["), s1);
        assertTrue(s1.contains("type: 'continuous'"), s1);

        String s2 = ECharts.chart()
                .visualMap(VisualMap.ofPiecewise())
                .plot(df4, "_tid").getChartScript();

        assertTrue(s2.contains("visualMap: ["), s2);
        assertTrue(s2.contains("type: 'piecewise'"), s2);
    }


    @Test
    public void min() {
        String s1 = ECharts.chart()
                .visualMap(VisualMap.ofContinuous().min(100))
                .plot(df4, "_tid").getChartScript();

        assertTrue(s1.contains("visualMap: ["), s1);
        assertTrue(s1.contains("min: 100"), s1);
    }

    @Test
    public void max() {
        String s1 = ECharts.chart()
                .visualMap(VisualMap.ofContinuous().max(100))
                .plot(df4, "_tid").getChartScript();

        assertTrue(s1.contains("visualMap: ["), s1);
        assertTrue(s1.contains("max: 100"), s1);
    }

    @Test
    public void orient() {
        String s1 = ECharts.chart()
                .visualMap(VisualMap.ofContinuous())
                .plot(df4, "_tid").getChartScript();


        assertTrue(s1.contains("visualMap: ["), s1);
        assertFalse(s1.contains("orient:"), s1);

        String s2 = ECharts.chart()
                .visualMap(VisualMap.ofContinuous().vertical())
                .plot(df4, "_tid").getChartScript();

        assertTrue(s2.contains("visualMap: ["), s2);
        assertTrue(s2.contains("orient: 'vertical'"), s2);
    }

    @Test
    public void left() {

        String s1 = ECharts.chart().visualMap(VisualMap.ofContinuous()).plot(df1, "_tid").getChartScript();
        assertTrue(s1.contains("visualMap: ["), s1);
        assertFalse(s1.contains("left:"), s1);

        String s2 = ECharts.chart().visualMap(VisualMap.ofContinuous().leftLeft()).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("left: 'left'"), s2);

        String s3 = ECharts.chart().visualMap(VisualMap.ofContinuous().leftCenter()).plot(df1, "_tid").getChartScript();
        assertTrue(s3.contains("left: 'center'"), s3);

        String s4 = ECharts.chart().visualMap(VisualMap.ofContinuous().leftPx(1)).plot(df1, "_tid").getChartScript();
        assertTrue(s4.contains("left: 1"), s4);

        String s5 = ECharts.chart().visualMap(VisualMap.ofContinuous().leftPct(10.1)).plot(df1, "_tid").getChartScript();
        assertTrue(s5.contains("left: '10.1%'"), s5);
    }

    @Test
    public void itemWidth() {

        String s1 = ECharts.chart().visualMap(VisualMap.ofContinuous()).plot(df1, "_tid").getChartScript();
        assertTrue(s1.contains("visualMap: ["), s1);
        assertFalse(s1.contains("itemWidth:"), s1);

        String s4 = ECharts.chart().visualMap(VisualMap.ofContinuous().itemWidthPx(1)).plot(df1, "_tid").getChartScript();
        assertTrue(s4.contains("itemWidth: 1"), s4);

        String s5 = ECharts.chart().visualMap(VisualMap.ofContinuous().itemWidthPct(10.1)).plot(df1, "_tid").getChartScript();
        assertTrue(s5.contains("itemWidth: '10.1%'"), s5);
    }

    @Test
    public void outOfRange() {

        String s1 = ECharts.chart().visualMap(VisualMap.ofContinuous()).plot(df1, "_tid").getChartScript();
        assertTrue(s1.contains("visualMap: ["), s1);
        assertFalse(s1.contains("outOfRange"), s1);

        String s2 = ECharts.chart().visualMap(VisualMap
                        .ofContinuous()
                        .outOfRange(VisualChannels.of().symbolSize(5).color("red"))).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("outOfRange: "), s2);
        assertTrue(s2.contains("symbolSize: 5,"), s2);
        assertTrue(s2.contains("color: 'red',"), s2);

        String s3 = ECharts.chart().visualMap(VisualMap
                .ofPiecewise()
                .outOfRange(VisualChannels.of().symbol(LineSymbol.circle).opacity(0.6))).plot(df1, "_tid").getChartScript();
        assertTrue(s3.contains("outOfRange: "), s3);
        assertTrue(s3.contains("symbol: 'circle',"), s3);
        assertTrue(s3.contains("opacity: 0.6,"), s3);
    }
}
