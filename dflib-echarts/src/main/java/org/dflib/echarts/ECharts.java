package org.dflib.echarts;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.model.ChartModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

/**
 * A build of an ECharts-based chart.
 *
 * @since 1.0.0-M21
 */
public class ECharts {

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

    private final String x;
    private String[] ys;
    private boolean darkMode;

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

    public ECharts darkMode() {
        this.darkMode = true;
        return this;
    }

    public HTML plot(DataFrame dataFrame) {
        return new HTML(plotString(dataFrame));
    }

    protected String plotString(DataFrame dataFrame) {
        return TEMPLATE.execute(new StringWriter(), createModel(dataFrame)).toString();
    }

    protected ChartModel createModel(DataFrame dataFrame) {
        Random rnd = new SecureRandom();

        int w = ys.length;
        Series[] ySeries = new Series[w];
        for (int i = 0; i < w; i++) {
            ySeries[i] = dataFrame.getColumn(ys[i]);
        }

        return new ChartModel(
                "dfl_ech_" + Math.abs(rnd.nextInt(10_000)),
                dataFrame.getColumn(x),
                ySeries,
                this.darkMode
        );
    }
}
