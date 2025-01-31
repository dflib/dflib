package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df1;
import static org.dflib.echarts.EChartTestDatasets.df4;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CalendarCoordTest {


    @Test
    public void calendar() {
        String s1 = ECharts.chart()
                .calendar("x")
                .plot(df4, "_tid").getChartScript();

        assertTrue(s1.contains("calendar: ["), s1);
        assertTrue(s1.contains("cellSize: ['auto',20]"), s1);
    }

    @Test
    public void cellSize() {
        String s1 = ECharts.chart()
                .calendar("x", CalendarCoords.ofLast12Months().cellHeightAuto())
                .plot(df4, "_tid").getChartScript();

        assertTrue(s1.contains("calendar: ["), s1);
        assertTrue(s1.contains("cellSize: ['auto','auto']"), s1);

        String s2 = ECharts.chart()
                .calendar("x", CalendarCoords.ofLast12Months().cellHeightAuto().cellWidth(78))
                .plot(df4, "_tid").getChartScript();

        assertTrue(s2.contains("calendar: ["), s2);
        assertTrue(s2.contains("cellSize: [78,'auto']"), s2);
    }

    @Test
    public void range() {
        String s1 = ECharts.chart()
                .calendar("x", CalendarCoords.ofMonth(2024, 6))
                .plot(df4, "_tid").getChartScript();

        assertTrue(s1.contains("calendar: ["), s1);
        assertTrue(s1.contains("range: ['2024-06-01','2024-06-30']"), s1);
    }

    @Test
    public void orient() {
        String s1 = ECharts.chart()
                .calendar("x")
                .plot(df4, "_tid").getChartScript();

        assertTrue(s1.contains("calendar: ["), s1);
        assertFalse(s1.contains("orient:"), s1);

        String s2 = ECharts.chart()
                .calendar("x", CalendarCoords.ofLast12Months().vertical())
                .plot(df4, "_tid").getChartScript();

        assertTrue(s2.contains("calendar: ["), s2);
        assertTrue(s2.contains("orient: 'vertical'"), s2);
    }

    @Test
    public void left() {

        String s1 = ECharts.chart().calendar("x", CalendarCoords.ofLast12Months()).plot(df1, "_tid").getChartScript();
        assertTrue(s1.contains("calendar: ["), s1);
        assertFalse(s1.contains("left:"), s1);

        String s2 =  ECharts.chart().calendar("x", CalendarCoords.ofLast12Months().leftLeft()).plot(df1, "_tid").getChartScript();
        assertTrue(s2.contains("left: 'left'"), s2);

        String s3 =  ECharts.chart().calendar("x", CalendarCoords.ofLast12Months().leftCenter()).plot(df1, "_tid").getChartScript();
        assertTrue(s3.contains("left: 'center'"), s3);

        String s4 =  ECharts.chart().calendar("x", CalendarCoords.ofLast12Months().leftPx(1)).plot(df1, "_tid").getChartScript();
        assertTrue(s4.contains("left: 1"), s4);

        String s5 =  ECharts.chart().calendar("x", CalendarCoords.ofLast12Months().leftPct(10.1)).plot(df1, "_tid").getChartScript();
        assertTrue(s5.contains("left: '10.1%'"), s5);
    }
}
