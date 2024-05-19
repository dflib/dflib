package org.dflib.echarts;

/**
 * A rendered EChart.
 *
 * @since 1.0.0-M21
 */
public class EChart {

    private final String content;

    public EChart(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
