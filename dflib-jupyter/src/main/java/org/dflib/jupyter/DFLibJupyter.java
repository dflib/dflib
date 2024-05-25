package org.dflib.jupyter;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.echarts.EChartHtml;
import org.dflib.jupyter.render.DataFrameRenderer;
import org.dflib.jupyter.render.EChartRenderer;
import org.dflib.jupyter.render.MutableTabularPrinter;
import org.dflib.jupyter.render.SeriesRenderer;
import io.github.spencerpark.jupyter.kernel.BaseKernel;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.Renderer;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;

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
    public static void init(BaseKernel kernel) {
        MutableTabularPrinter printer = new MutableTabularPrinter(10, 50);
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
     * Configures notebook DFLib printer's max width of a column in characters for a DataFrame or Series.
     */
    public static void setMaxDisplayColumnWidth(int w) {
        instance.printer.setMaxDisplayColumnWidth(w);
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
