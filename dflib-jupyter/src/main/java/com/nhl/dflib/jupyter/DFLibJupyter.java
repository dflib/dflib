package com.nhl.dflib.jupyter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.jupyter.render.DataFrameRenderer;
import com.nhl.dflib.jupyter.render.MutableTabularPrinter;
import com.nhl.dflib.jupyter.render.SeriesRenderer;
import io.github.spencerpark.jupyter.kernel.BaseKernel;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.Renderer;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;
import org.slf4j.LoggerFactory;

/**
 * The main class of the DFLib Jupyter plugin that bootstraps DFLib renderers into a Jupyter notebook under
 * one of the "jupyter-jvm-basekernel" kernels, like "iJava", etc. To use with say <code>iJava</code>, you might put the
 * following code in the notebook:
 *
 * <pre>
 * %maven com.nhl.dflib:dflib-jupyter:x.x
 * DFLibJupyter.init(getKernelInstance());
 * </pre>
 *
 * @see "https://github.com/SpencerPark/jupyter-jvm-basekernel"
 * @since 0.6
 */
public class DFLibJupyter {

    private static DFLibJupyter instance;

    private MutableTabularPrinter printer;

    public DFLibJupyter(MutableTabularPrinter printer) {
        this.printer = printer;
    }

    public static void init(BaseKernel kernel) {
        DFLibJupyter.initLogging();

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

    public static void setLogLevelToDebug() {
        getRootLogger().setLevel(Level.DEBUG);
    }

    public static void setLogLevelToInfo() {
        getRootLogger().setLevel(Level.INFO);
    }

    public static void setLogLevelToWarn() {
        getRootLogger().setLevel(Level.WARN);
    }

    public static void setLogLevelToError() {
        getRootLogger().setLevel(Level.ERROR);
    }

    private static void initLogging() {

        Logger rootLogger = getRootLogger();
        rootLogger.setLevel(Level.WARN);

        // redirect console appender to STDERR
        ConsoleAppender<ILoggingEvent> appender = (ConsoleAppender<ILoggingEvent>) rootLogger.getAppender("console");
        if (appender != null) {
            appender.setTarget("System.err");

            if (appender.isStarted()) {
                appender.stop();
                appender.start();
            }
        }
    }

    private static Logger getRootLogger() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
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
