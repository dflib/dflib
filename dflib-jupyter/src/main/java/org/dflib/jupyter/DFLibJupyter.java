package org.dflib.jupyter;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.EChartHtml;
import org.dflib.jjava.jupyter.kernel.BaseKernel;
import org.dflib.jjava.jupyter.kernel.display.RenderFunction;
import org.dflib.jjava.jupyter.kernel.display.Renderer;
import org.dflib.jjava.jupyter.kernel.display.mime.MIMEType;
import org.dflib.jupyter.render.DataFrameRenderer;
import org.dflib.jupyter.render.EChartRenderer;
import org.dflib.jupyter.render.MutableTabularPrinter;
import org.dflib.jupyter.render.SeriesRenderer;

/**
 * A bridge between DFLib and Jupyter notebook environment. Bootstraps DFLib renderers into a Jupyter notebook under
 * one of the "jupyter-jvm-basekernel" kernels, like JJava. Usage:
 *
 * <pre>
 * %maven org.dflib:dflib-jupyter:x.x
 * DFLibJupyter.init(getKernelInstance());
 * </pre>
 */
public class DFLibJupyter {

    private static DFLibJupyter instance;

    private MutableTabularPrinter printer;

    public DFLibJupyter(MutableTabularPrinter printer) {
        this.printer = printer;
    }

    /**
     * This method should be explicitly invoked in the notebook to connect DFLib extensions to Jupyter environment.
     */
    // While this method may show as unused in an IDE, it is called inside the script in DFLibJupyterExtension
    public static void init(BaseKernel kernel) {

        // not passing explicit display parameters, relying on DFLib defaults instead
        MutableTabularPrinter printer = new MutableTabularPrinter();

        DFLibJupyter jupyterBridge = new DFLibJupyter(printer);
        jupyterBridge.doInit(kernel);

        DFLibJupyter.instance = jupyterBridge;
    }

    /**
     * Configures notebook DFLib printer's max number of display rows for a DataFrame or Series.
     */
    public static void setMaxDisplayRows(int rows) {
        instance.printer.setMaxDisplayRows(rows);
    }

    /**
     * Configures notebook DFLib printer's max number of display rows for a DataFrame or Series.
     */
    public static void setMaxDisplayCols(int cols) {
        instance.printer.setMaxDisplayCols(cols);
    }

    /**
     * Configures notebook DFLib printer's max width of a column in characters for a DataFrame or Series.
     *
     * @since 2.0.0
     */
    public static void setMaxDisplayValueWidth(int w) {
        instance.printer.setMaxDisplayValueWidth(w);
    }

    /**
     * @deprecated in favor of {@link #setMaxDisplayValueWidth(int)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void setMaxDisplayColumnWidth(int w) {
        System.err.println("'DFLibJupyter.setMaxDisplayColumnWidth(int)' is deprecated in favor of 'setMaxDisplayValueWidth(int)'");
        instance.printer.setMaxDisplayValueWidth(w);
    }

    private void doInit(BaseKernel kernel) {
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
