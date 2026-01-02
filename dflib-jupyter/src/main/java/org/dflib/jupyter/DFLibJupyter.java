package org.dflib.jupyter;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.EChartHtml;
import org.dflib.jjava.jupyter.Extension;
import org.dflib.jjava.jupyter.kernel.BaseKernel;
import org.dflib.jjava.jupyter.kernel.display.RenderFunction;
import org.dflib.jjava.jupyter.kernel.display.Renderer;
import org.dflib.jjava.jupyter.kernel.display.mime.MIMEType;
import org.dflib.jupyter.render.DataFrameRenderer;
import org.dflib.jupyter.render.EChartRenderer;
import org.dflib.jupyter.render.MutableTabularPrinter;
import org.dflib.jupyter.render.SeriesRenderer;

import java.util.Objects;

/**
 * A bridge between DFLib and Jupyter notebook environment, specifically JJava (or compatible) kernel. It should be
 * automatically loaded by JJava kernel using the {@link Extension} loading mechanism and will initialize DFLib
 * renderers into a notebook. It also provide explicit API to control notebook display defaults for DataFrames and
 * Series.
 */
public class DFLibJupyter implements Extension {

    private static final String STARTUP_SCRIPT = """
            import org.dflib.*;
            import org.dflib.http.*;
            import org.dflib.fs.*;
            import org.dflib.zip.*;
            import org.dflib.avro.*;
            import org.dflib.csv.*;
            import org.dflib.echarts.*;
            import org.dflib.excel.*;
            import org.dflib.jdbc.*;
            import org.dflib.json.*;
            import org.dflib.jupyter .*;
            import org.dflib.parquet.*;
            import static org.dflib.Exp.*;
            """;

    private static DFLibJupyter instance;

    private final MutableTabularPrinter printer;

    public DFLibJupyter() {
        // not passing explicit display parameters, relying on DFLib defaults instead
        this.printer = new MutableTabularPrinter();
    }

    /**
     * @deprecated in favor of Jupyter lifecycle-aware {@link #install(BaseKernel)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void init(BaseKernel kernel) {
        System.err.println("'DFLibJupyter.init(BaseKernel)' is deprecated and does nothing. DFLibJupyter should be loaded as a JJava Extension");
    }

    /**
     * Configures notebook DFLib printer's max number of display rows for a DataFrame or Series.
     */
    public static void setMaxDisplayRows(int rows) {
        nonNullInstance().printer.setMaxDisplayRows(rows);
    }

    /**
     * Configures notebook DFLib printer's max number of display rows for a DataFrame or Series.
     */
    public static void setMaxDisplayCols(int cols) {
        nonNullInstance().printer.setMaxDisplayCols(cols);
    }

    /**
     * Configures notebook DFLib printer's max width of a column in characters for a DataFrame or Series.
     *
     * @since 2.0.0
     */
    public static void setMaxDisplayValueWidth(int w) {
        nonNullInstance().printer.setMaxDisplayValueWidth(w);
    }

    /**
     * @deprecated in favor of {@link #setMaxDisplayValueWidth(int)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void setMaxDisplayColumnWidth(int w) {
        System.err.println("'DFLibJupyter.setMaxDisplayColumnWidth(int)' is deprecated in favor of 'setMaxDisplayValueWidth(int)'");
        nonNullInstance().printer.setMaxDisplayValueWidth(w);
    }

    private static DFLibJupyter nonNullInstance() {
        return Objects.requireNonNull(
                DFLibJupyter.instance,
                "DFLibJupyter was not initialized. It was either called outside of the notebook lifecycle, or kernel extension loading is disabled");
    }

    @Override
    public void install(BaseKernel kernel) {

        if (DFLibJupyter.instance != null) {
            throw new IllegalStateException("DFLibJupyter was already initialized within the notebook");
        }

        installRenderers(kernel);
        DFLibJupyter.instance = this;
        kernel.eval(STARTUP_SCRIPT);
    }

    @Override
    public void uninstall(BaseKernel kernel) {
        // TODO: remove renderers from the kernel?
        DFLibJupyter.instance = null;
    }

    private void installRenderers(BaseKernel kernel) {

        RenderFunction<EChartHtml> echartHtmlRenderer = new EChartRenderer();
        RenderFunction<DataFrame> dfRenderer = new DataFrameRenderer(printer);
        RenderFunction<Series> seriesRenderer = new SeriesRenderer(printer);

        Renderer renderer = kernel.getRenderer();

        renderer.createRegistration(DataFrame.class)
                .preferring(MIMEType.TEXT_PLAIN)
                .supporting(MIMEType.TEXT_PLAIN)
                .register(dfRenderer);

        renderer.createRegistration(Series.class)
                .preferring(MIMEType.TEXT_PLAIN)
                .supporting(MIMEType.TEXT_PLAIN)
                .register(seriesRenderer);

        renderer.createRegistration(EChartHtml.class)
                .preferring(MIMEType.TEXT_HTML)
                .supporting(MIMEType.TEXT_HTML, MIMEType.TEXT_PLAIN)
                .register(echartHtmlRenderer);
    }
}
