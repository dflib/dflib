package org.dflib.echarts;

import org.dflib.echarts.render.util.ElementIdGenerator;

import java.io.File;
import java.nio.file.Path;

/**
 * An entry point to EChart builder. The builder would create HTML/JS code that renders DataFrame data using ECharts
 * library.
 */
public class ECharts {

    /**
     * Starts a builder for a new chart.
     */
    public static EChart chart() {
        return chart(ElementIdGenerator.random());
    }

    // intentionally non-public for now. Any reason to expose ID generator API?
    static EChart chart(ElementIdGenerator idGenerator) {
        return new EChart(idGenerator);
    }

    /**
     * Starts a builder for a new named chart.
     */
    public static EChart chart(String title) {
        return chart().title(title);
    }

    /**
     * @since 2.0.0
     */
    public static EChartHtmlSaver saver() {
        return new EChartHtmlSaver();
    }

    public static void save(Path path, EChartHtml... charts) {
        saver().save(path, charts);
    }

    public void save(String file, EChartHtml... charts) {
        saver().save(file, charts);
    }

    /**
     * @since 2.0.0
     */
    public static void save(File file, EChartHtml... charts) {
        saver().save(file, charts);
    }

    /**
     * @since 2.0.0
     */
    public static void save(Appendable out, EChartHtml... charts) {
        saver().save(out, charts);
    }
}
