package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df1;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeoTest {

    @Test
    public void geo() {
        String s1 = ECharts.chart().plot(df1, "_tid").renderChartScript();
        assertFalse(s1.contains("geo:"), s1);

        String s2 = ECharts.chart().geo(Geo.of("world")).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("geo: {"), s2);
        assertTrue(s2.contains("map: 'world'"), s2);
    }

    @Test
    public void show() {
        String s1 = ECharts.chart().geo(Geo.of("world")).plot(df1, "_tid").renderChartScript();
        assertTrue(s1.contains("geo: {"), s1);
        assertFalse(s1.contains("show:"), s1);

        String s2 = ECharts.chart().geo(Geo.of("world").show(false)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("show: false"), s2);

        String s3 = ECharts.chart().geo(Geo.of("world").show(true)).plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("show: true"), s3);
    }

    @Test
    public void zoom() {
        String s1 = ECharts.chart().geo(Geo.of("world")).plot(df1, "_tid").renderChartScript();
        assertTrue(s1.contains("geo: {"), s1);
        assertFalse(s1.contains("zoom:"), s1);

        String s2 = ECharts.chart().geo(Geo.of("world").zoom(2)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("zoom: 2"), s2);
    }

    @Test
    public void roam() {
        String s1 = ECharts.chart().geo(Geo.of("world")).plot(df1, "_tid").renderChartScript();
        assertTrue(s1.contains("geo: {"), s1);
        assertFalse(s1.contains("roam:"), s1);

        String s2 = ECharts.chart().geo(Geo.of("world").roam(true)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("roam: true"), s2);

        String s3 = ECharts.chart().geo(Geo.of("world").roam(false)).plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("roam: false"), s3);
    }

    @Test
    public void center() {
        String s1 = ECharts.chart().geo(Geo.of("world")).plot(df1, "_tid").renderChartScript();
        assertTrue(s1.contains("geo: {"), s1);
        assertFalse(s1.contains("center:"), s1);

        String s2 = ECharts.chart()
                .geo(Geo.of("world").centerLat(40.7128).centerLon(-74.0060))
                .plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("center: [-74.006,40.7128]"), s2);
    }

    @Test
    public void centerPct() {
        String s1 = ECharts.chart()
                .geo(Geo.of("world").centerLatPct(50.0).centerLonPct(50.0))
                .plot(df1, "_tid").renderChartScript();
        assertTrue(s1.contains("center: ['50.0%','50.0%']"), s1);

        String s2 = ECharts.chart()
                .geo(Geo.of("world").centerLatPct(25.5).centerLonPct(75.5))
                .plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("center: ['75.5%','25.5%']"), s2);
    }

    @Test
    public void aspectScale() {
        String s1 = ECharts.chart().geo(Geo.of("world")).plot(df1, "_tid").renderChartScript();
        assertTrue(s1.contains("geo: {"), s1);
        assertFalse(s1.contains("aspectScale:"), s1);

        String s2 = ECharts.chart().geo(Geo.of("world").aspectScale(0.75)).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("aspectScale: 0.75"), s2);

        String s3 = ECharts.chart().geo(Geo.of("world").aspectScale(1.5)).plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("aspectScale: 1.5"), s3);
    }

    @Test
    public void label() {
        String s1 = ECharts.chart().geo(Geo.of("world")).plot(df1, "_tid").renderChartScript();
        assertTrue(s1.contains("geo: {"), s1);
        assertFalse(s1.contains("label:"), s1);

        String s2 = ECharts.chart().geo(Geo.of("world").label(Label.ofTop())).plot(df1, "_tid").renderChartScript();
        assertTrue(s2.contains("label: {"), s2);
        assertTrue(s2.contains("position: 'top'"), s2);

        String s3 = ECharts.chart().geo(Geo.of("world").label(Label.ofInside().show(false))).plot(df1, "_tid").renderChartScript();
        assertTrue(s3.contains("label: {"), s3);
        assertTrue(s3.contains("show: false"), s3);
        assertTrue(s3.contains("position: 'inside'"), s3);

        String s4 = ECharts.chart().geo(Geo.of("world").label(Label.ofRight().formatter("{b}"))).plot(df1, "_tid").renderChartScript();
        assertTrue(s4.contains("label: {"), s4);
        assertTrue(s4.contains("formatter: '{b}'"), s4);
        assertTrue(s4.contains("position: 'right'"), s4);
    }
}
