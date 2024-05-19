package org.dflib.echarts;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.model.EChartModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

/**
 * A builder of HTML/JS code that renders DataFrame data using ECharts library.
 *
 * @since 1.0.0-M21
 */
public class ECharts {

    private static final String DEFAULT_ECHARTS_JS_URL = "https://cdn.jsdelivr.net/npm/echarts@5.5.0/dist/echarts.min.js";
    private static final Mustache TEMPLATE = loadTemplate("cell.mustache");

    static Mustache loadTemplate(String name) {
        try (InputStream in = ECharts.class.getResourceAsStream(name)) {

            if (in == null) {
                throw new RuntimeException("ECharts 'cell.mustache' template is not found");
            }

            // not providing an explicit resolver of subtemplates.. assuming a single flat template for now
            try (Reader reader = new InputStreamReader(in)) {
                return new DefaultMustacheFactory().compile(reader, name);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading muustache template: " + name, e);
        }
    }

    private String echartsJsUrl;
    private final String x;
    private String[] ys;
    private String theme;
    private String title;
    private Integer width;
    private Integer height;

    public static ECharts x(String xAxisColumn) {
        return new ECharts(xAxisColumn);
    }

    protected ECharts(String x) {
        this.x = Objects.requireNonNull(x);
    }

    public ECharts y(String... yAxisColumns) {
        this.ys = yAxisColumns;
        return this;
    }

    public ECharts echartsJsUrl(String url) {
        this.echartsJsUrl = url;
        return this;
    }

    public ECharts title(String title) {
        this.title = title;
        return this;
    }

    public ECharts sizePixels(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public ECharts theme(String theme) {
        this.theme = theme;
        return this;
    }

    public ECharts darkTheme() {
        return theme("dark");
    }

    public EChart plot(DataFrame dataFrame) {
        return new EChart(plotString(dataFrame));
    }

    protected String plotString(DataFrame dataFrame) {
        return TEMPLATE.execute(new StringWriter(), createModel(dataFrame)).toString();
    }

    protected EChartModel createModel(DataFrame dataFrame) {
        Random rnd = new SecureRandom();

        int w = ys != null ? ys.length : 0;
        Series[] ySeries = new Series[w];
        for (int i = 0; i < w; i++) {
            ySeries[i] = dataFrame.getColumn(ys[i]);
        }

        return new EChartModel(
                "dfl_ech_" + Math.abs(rnd.nextInt(10_000)),
                this.echartsJsUrl != null ? this.echartsJsUrl : DEFAULT_ECHARTS_JS_URL,
                title,
                dataFrame.getColumn(x),
                ySeries,
                theme,
                this.width != null ? this.width : 600,
                this.height != null ? this.height : 400
        );
    }
}
