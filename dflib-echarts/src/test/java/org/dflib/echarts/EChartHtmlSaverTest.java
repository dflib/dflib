package org.dflib.echarts;

import org.dflib.echarts.render.util.ElementIdGenerator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EChartHtmlSaverTest extends GenerateScriptHtmlTest {

    @Test
    void test() throws IOException {
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
}
