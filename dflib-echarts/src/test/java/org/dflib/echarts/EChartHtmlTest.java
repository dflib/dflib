package org.dflib.echarts;

import org.junit.jupiter.api.Test;

import static org.dflib.echarts.EChartTestDatasets.df2;
import static org.junit.jupiter.api.Assertions.*;

public class EChartHtmlTest {


    @Test
    public void plotWithDivId() {
        EChartHtml p1 = ECharts.chart().plot(df2, "_tid");
        assertTrue(p1.renderChartScript().contains("_tid"));
        assertTrue(p1.renderChartDiv().contains("_tid"));

        EChartHtml p2 = p1.plotWithDivId("_xid");
        assertNotSame(p1, p2);

        assertTrue(p1.renderChartScript().contains("_tid"));
        assertTrue(p1.renderChartDiv().contains("_tid"));

        assertFalse(p2.renderChartScript().contains("_tid"));
        assertFalse(p2.renderChartDiv().contains("_tid"));
        assertTrue(p2.renderChartScript().contains("_xid"));
        assertTrue(p2.renderChartDiv().contains("_xid"));
    }
}
