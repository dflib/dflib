package org.dflib.echarts;

import org.dflib.echarts.render.util.ElementIdGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.dflib.echarts.EChartTestDatasets.*;

public class EChartHtmlSaverTest {

    @Test
    void standardTemplate() throws IOException {
        ElementIdGenerator idGenerator = ElementIdGenerator.sequential();

        EChartHtml p1 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y1"), "y1").plot(df2);
        EChartHtml p2 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y2"), "y2").plot(df2);

        StringWriter out = new StringWriter();
        ECharts.saver().title("Test Charts").save(out, p1, p2);


        String expected = new String(getClass().getResourceAsStream("charts1.html").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expected, out.toString());
    }

    @Test
    void customTemplateSeparateCharts() throws IOException {
        ElementIdGenerator idGenerator = ElementIdGenerator.sequential();

        EChartHtml p1 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y1"), "y1").plot(df2);
        EChartHtml p2 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y2"), "y2").plot(df2);

        StringWriter out = new StringWriter();
        ECharts.saver().title("Templated charts 1")
                .htmlTemplate(getClass().getResource("charts-template1.mustache"))
                .save(out, p1, p2);

        String expected = new String(getClass().getResourceAsStream("charts-template1.html").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expected, out.toString());
    }

    @Test
    void customTemplateListOfCharts() throws IOException {
        ElementIdGenerator idGenerator = ElementIdGenerator.sequential();

        EChartHtml p1 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y1"), "y1").plot(df2);
        EChartHtml p2 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y2"), "y2").plot(df2);

        StringWriter out = new StringWriter();
        ECharts.saver().title("Templated charts 2")
                .htmlTemplate(getClass().getResource("charts-template2.mustache"))
                .save(out, p1, p2);

        String expected = new String(getClass().getResourceAsStream("charts-template2.html").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expected, out.toString());
    }

    @Test
    void customTemplateFromPath() throws IOException, URISyntaxException {
        ElementIdGenerator idGenerator = ElementIdGenerator.sequential();

        EChartHtml p1 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y1"), "y1").plot(df2);
        EChartHtml p2 = ECharts
                .chart(idGenerator)
                .xAxis("x")
                .series(SeriesOpts.ofLine().name("Y2"), "y2").plot(df2);

        StringWriter out = new StringWriter();
        ECharts.saver().title("Templated charts 2")
                .htmlTemplate(Path.of(getClass().getResource("charts-template2.mustache").toURI()))
                .save(out, p1, p2);

        String expected = new String(getClass().getResourceAsStream("charts-template2.html").readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(expected, out.toString());
    }
}
