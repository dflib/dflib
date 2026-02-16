package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.geoDf1;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScatterGeoSeriesTest {

    @Test
    public void coordinateSystem() {
        String s1 = ECharts.chart()
                .geo(Geo.of("world"))
                .series(SeriesOpts.ofScatterGeo().coordinates("lon", "lat"), "val")
                .plot(geoDf1, "_tid")
                .renderChartScript();

        assertTrue(s1.contains("coordinateSystem: 'geo'"), s1);
    }

    @Test
    public void data() {
        String s1 = ECharts.chart()
                .geo(Geo.of("world"))
                .series(SeriesOpts.ofScatterGeo().coordinates("lon", "lat"), "val")
                .plot(geoDf1, "_tid")
                .renderChartScript();

        assertTrue(s1.contains("['L0',-21.9348415,-19.028531,-17.089925]"), s1);
        assertTrue(s1.contains("['L1',64.1334671,63.710241,65.37887072]"), s1);
        assertTrue(s1.contains("['val',10,20,30]"), s1);
        assertTrue(s1.contains("encode: {"), s1);
        assertTrue(s1.contains("lat: 0,"), s1);
        assertTrue(s1.contains("lng: 1"), s1);
        assertTrue(s1.contains("value: 2"), s1);
    }
}
