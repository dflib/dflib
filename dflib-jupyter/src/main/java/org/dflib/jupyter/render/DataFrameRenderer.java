package org.dflib.jupyter.render;

import org.dflib.DataFrame;
import org.dflib.jjava.jupyter.kernel.display.RenderContext;
import org.dflib.jjava.jupyter.kernel.display.RenderFunction;
import org.dflib.jjava.jupyter.kernel.display.mime.MIMEType;
import org.dflib.print.Printer;

public class DataFrameRenderer implements RenderFunction<DataFrame> {

    private final Printer printer;

    public DataFrameRenderer(Printer printer) {
        this.printer = printer;
    }

    @Override
    public void render(DataFrame df, RenderContext context) {
        context.renderIfRequested(MIMEType.TEXT_PLAIN, () -> printer.print(df));
    }
}
