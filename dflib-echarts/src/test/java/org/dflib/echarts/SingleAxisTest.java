package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SingleAxisTest {

    @Test
    public void data() {

        String s1 = ECharts.chart().plot(df2, "_tid").getChartScript();
        assertFalse(s1.contains("['labels',1,2,3]"), s1);
        assertTrue(s1.contains("type: 'category'"), s1);

        String s2 = ECharts.chart().series(SeriesOpts.ofScatterSingleAxis(), "y1").plot(df2, "_tid").getChartScript();
        assertTrue(s2.contains("['y1',10,11,14]"), s2);
        assertTrue(s2.contains("singleAxis: ["), s2);
        assertTrue(s2.contains("type: 'value'"), s2);

        String s3 = ECharts.chart().singleAxis(SingleAxis.ofValue()).series(SeriesOpts.ofScatterSingleAxis(), "y1").plot(df2, "_tid").getChartScript();
        assertTrue(s2.contains("['y1',10,11,14]"), s2);
        assertTrue(s2.contains("singleAxis: ["), s3);
        assertTrue(s3.contains("type: 'value'"), s3);

        String s4 = ECharts.chart().singleAxis(SingleAxis.ofCategory()).series(SeriesOpts.ofScatterSingleAxis(), "y1").plot(df2, "_tid").getChartScript();
        assertTrue(s2.contains("['y1',10,11,14]"), s2);
        assertTrue(s2.contains("singleAxis: ["), s4);
        assertTrue(s4.contains("type: 'category'"), s4);
    }

    @Test
    public void boundaryGap() {

        String s1 = ECharts.chart().plot(df1, "_tid").getChartScript();
        assertFalse(s1.contains("boundaryGap: false,"), s1);

        String s2 = ECharts.chart().singleAxis(SingleAxis.ofValue().boundaryGap(false)).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("boundaryGap: false,"), s2);

        String s3 = ECharts.chart().singleAxis(SingleAxis.ofValue().boundaryGap(true)).plot(df1, "_tid").getChartScript();
        assertFalse(s3.contains("boundaryGap: false,"), s3);
    }

    @Test
    public void name() {

        String s1 = ECharts.chart().plot(df1, "_tid").getChartScript();
        assertFalse(s1.contains("name:"), s1);

        String s2 = ECharts.chart().singleAxis(SingleAxis.ofValue().name("I'm single")).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("name: 'I\\'m single'"), s2);
    }

    @Test
    public void left() {

        String s1 = ECharts.chart().singleAxis(SingleAxis.ofValue()).plot(df1, "_tid").getChartScript();
        assertTrue(s1.contains("singleAxis: ["), s1);
        assertFalse(s1.contains("left:"), s1);

        String s2 =  ECharts.chart().singleAxis(SingleAxis.ofValue().leftLeft()).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("left: 'left'"), s2);

        String s3 =  ECharts.chart().singleAxis(SingleAxis.ofValue().leftCenter()).plot(df1, "_tid").getChartScript();
        assertTrue(s3.contains("left: 'center'"), s3);

        String s4 =  ECharts.chart().singleAxis(SingleAxis.ofValue().leftPx(1)).plot(df1, "_tid").getChartScript();
        assertTrue(s4.contains("left: 1"), s4);

        String s5 =  ECharts.chart().singleAxis(SingleAxis.ofValue().leftPct(10.1)).plot(df1, "_tid").getChartScript();
        assertTrue(s5.contains("left: '10.1%'"), s5);
    }

    @Test
    public void right() {

        String s1 = ECharts.chart().singleAxis(SingleAxis.ofValue()).plot(df1, "_tid").getChartScript();
        assertTrue(s1.contains("singleAxis: ["), s1);
        assertFalse(s1.contains("right:"), s1);

        String s2 =  ECharts.chart().singleAxis(SingleAxis.ofValue().rightPx(1)).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("right: 1"), s2);

        String s3 =  ECharts.chart().singleAxis(SingleAxis.ofValue().rightPct(10.1)).plot(df1, "_tid").getChartScript();
        assertTrue(s3.contains("right: '10.1%'"), s3);
    }

    @Test
    public void width() {

        String s1 = ECharts.chart().singleAxis(SingleAxis.ofValue()).plot(df1, "_tid").getChartScript();
        assertTrue(s1.contains("singleAxis: ["), s1);
        assertFalse(s1.contains("width:"), s1);

        String s2 =  ECharts.chart().singleAxis(SingleAxis.ofValue().widthPct(10)).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("width: '10.0%'"), s2);

        String s3 =  ECharts.chart().singleAxis(SingleAxis.ofValue().widthPx(20)).plot(df1, "_tid").getChartScript();
        assertTrue(s3.contains("width: 20"), s3);
    }
}
