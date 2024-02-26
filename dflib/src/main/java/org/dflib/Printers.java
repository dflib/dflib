package org.dflib;

import org.dflib.print.InlinePrinter;
import org.dflib.print.Printer;
import org.dflib.print.TabularPrinter;

public class Printers {

    public static final Printer inline = new InlinePrinter();
    public static final Printer tabular = new TabularPrinter();

    /**
     * @since 1.0.0-M20
     */
    public static Printer tabular() {
        return tabular;
    }

    /**
     * @since 1.0.0-M20
     */
    public static Printer tabular(int maxDisplayRows, int maxDisplayColumnWidth) {
        return new TabularPrinter(maxDisplayRows, maxDisplayColumnWidth);
    }
}
