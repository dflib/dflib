package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PieSeriesTest extends GenerateScriptHtmlTest {

    @Test
    public void type() {
        String s2 = EChart.chart().series(SeriesOpts.ofPie(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("type: 'pie'"), s2);

        String s3 = EChart.chart().defaultSeriesOpts(SeriesOpts.ofPie()).series("y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("type: 'pie'"), s3);
    }

    @Test
    public void label() {

        String s1 = EChart.chart().series(SeriesOpts.ofPie(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("label:"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofPie().label(PieLabel.ofInside()), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("label: {"), s2);
        assertTrue(s2.contains("position: 'inside'"), s2);
    }

    @Test
    public void radius() {

        String s1 = EChart.chart().series(SeriesOpts.ofPie(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("radius:"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofPie().radiusPct(10, 12.5), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("radius: ['10.0%','12.5%'],"), s2);

        String s3 = EChart.chart().series(SeriesOpts.ofPie().radiusPixels(10, 12), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("radius: [10,12],"), s3);
    }

    @Test
    public void center() {

        String s1 = EChart.chart().series(SeriesOpts.ofPie(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("center:"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofPie().centerPct(10, 12.5), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("center: ['10.0%','12.5%'],"), s2);

        String s3 = EChart.chart().series(SeriesOpts.ofPie().centerPixels(10, 12), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("center: [10,12],"), s3);
    }

    @Test
    public void angle() {

        String s1 = EChart.chart().series(SeriesOpts.ofPie(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("startAngle:"), s1);
        assertFalse(s1.contains("endAngle:"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofPie().startAngle(15), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("startAngle: 15,"), s2);
        assertFalse(s2.contains("endAngle:"), s2);

        String s3 = EChart.chart().series(SeriesOpts.ofPie().startAngle(15).endAngle(30), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("startAngle: 15,"), s3);
        assertTrue(s3.contains("endAngle: 30"), s3);
    }

    @Test
    public void roseType() {

        String s1 = EChart.chart().series(SeriesOpts.ofPie(), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s1.contains("roseType:"), s1);

        String s2 = EChart.chart().series(SeriesOpts.ofPie().roseType(RoseType.radius), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("roseType: 'radius',"), s2);

        String s3 = EChart.chart().series(SeriesOpts.ofPie().roseType(RoseType.area), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s3.contains("roseType: 'area',"), s3);
    }

    @Test
    public void data() {
        String s2 = EChart.chart().series(SeriesOpts.ofPie(), "y1").generateScriptHtml("_tid", df2);
        assertTrue(s2.contains("['labels',1,2,3],"), s2);
        assertTrue(s2.contains("['y1',10,11,14]"), s2);
        assertTrue(s2.contains("encode: {"), s2);
        assertTrue(s2.contains("value: 1,"), s2);
        assertTrue(s2.contains("itemName: 0,"), s2);

        String s3 = EChart.chart().series(SeriesOpts.ofPie().label("x"), "y1").generateScriptHtml("_tid", df2);
        assertFalse(s3.contains("['labels',1,2,3],"), s3);
        assertTrue(s3.contains("['x','A','B','C'],"), s3);
        assertTrue(s3.contains("['y1',10,11,14]"), s3);
        assertTrue(s3.contains("encode: {"), s3);
        assertTrue(s3.contains("value: 1,"), s3);
        assertTrue(s3.contains("itemName: 0,"), s3);
    }
}
