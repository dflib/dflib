package org.dflib.echarts;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.dflib.echarts.EChartTestDatasets.*;

public class MapSeriesTest {

    @Test
    public void encode() {
        DataFrame df = DataFrame.foldByRow("state", "population")
                .of("CA", 39500000, "TX", 29000000, "FL", 21500000);

        String s1 = ECharts.chart()
                .geo(Geo.of("usa"))
                .series(SeriesOpts.ofMap().itemNameData("state"), "population")
                .plot(df, "_tid")
                .renderChartScript();

        assertTrue(s1.contains("encode: {"), s1);
        assertTrue(s1.contains("itemName:"), s1);
        assertTrue(s1.contains("value:"), s1);
        assertTrue(s1.contains("coordinateSystem: 'geo'"), s1);
        assertTrue(s1.contains("type: 'map'"), s1);
    }

    @Test
    public void dataset() {

        String s1 = ECharts.chart()
                .geo(Geo.of("usa"))
                .series(SeriesOpts.ofMap().itemNameData("x"), "y1")
                .plot(df2, "_tid")
                .renderChartScript();

        assertTrue(s1.contains("dataset"), s1);
        assertTrue(s1.contains("seriesLayoutBy: 'column'"), s1);

        // Column layout: first row is column headers, then one row per data point.
        // itemName column gets an auto-generated label ('L0') as it is added as an unnamed row
        assertTrue(s1.contains("['L0','y1']"), s1);
        assertTrue(s1.contains("['A',10]"), s1);
        assertTrue(s1.contains("['B',11]"), s1);
        assertTrue(s1.contains("['C',14]"), s1);

        // Row layout must NOT be used
        assertFalse(s1.contains("['L0','A','B','C']"), s1);
        assertFalse(s1.contains("['y1',10,11,14]"), s1);

        // Encode references column indices
        assertTrue(s1.contains("itemName: 0,"), s1);
        assertTrue(s1.contains("value: 1"), s1);
    }

    @Test
    public void geoIndex() {
        DataFrame df = DataFrame.foldByRow("state", "population")
                .of("CA", 39500000);

        String s1 = ECharts.chart()
                .geo(Geo.of("usa"))
                .series(SeriesOpts.ofMap().itemNameData("state"), "population")
                .plot(df, "_tid")
                .renderChartScript();

        // Default geoIndex should not appear
        assertFalse(s1.contains("geoIndex:"), s1);

        String s2 = ECharts.chart()
                .geo(Geo.of("usa"))
                .series(SeriesOpts.ofMap().itemNameData("state").geoIndex(1), "population")
                .plot(df, "_tid")
                .renderChartScript();

        assertTrue(s2.contains("geoIndex: 1"), s2);
    }
}
