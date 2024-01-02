package org.dflib.jupyter;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.jupyter.render.DataFrameRenderer;
import org.dflib.jupyter.render.MutableTabularPrinter;
import org.dflib.jupyter.render.SeriesRenderer;
import io.github.spencerpark.jupyter.kernel.BaseKernel;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.Renderer;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;

/**
 * The main class of the DFLib Jupyter plugin that bootstraps DFLib renderers into a Jupyter notebook under
 * one of the "jupyter-jvm-basekernel" kernels, like "iJava", etc. To use with say <code>iJava</code>, you might put the
 * following code in the notebook:
 *
 * <pre>
 * %maven org.dflib:dflib-jupyter:x.x
 * DFLibJupyter.init(getKernelInstance());
 * </pre>
 *
 * @see "https://github.com/SpencerPark/jupyter-jvm-basekernel"
 */
public class DFLibJupyter {

    private static DFLibJupyter instance;

    private MutableTabularPrinter printer;

    public DFLibJupyter(MutableTabularPrinter printer) {
        this.printer = printer;
    }

    public static void init(BaseKernel kernel) {
        MutableTabularPrinter printer = new MutableTabularPrinter(10, 50);
        DFLibJupyter.instance = new DFLibJupyter(printer);
        DFLibJupyter.instance.doInit(kernel);
    }

    public static void setMaxDisplayRows(int rows) {
        instance.printer.setMaxDisplayRows(rows);
    }

    public static void setMaxDisplayColumnWidth(int w) {
        instance.printer.setMaxDisplayColumnWidth(w);
    }

    private void doInit(BaseKernel kernel) {
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
    }
}
