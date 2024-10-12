package org.dflib;

import org.dflib.print.InlinePrinter;
import org.dflib.print.Printer;
import org.dflib.print.TabularPrinter;

public class Printers {

    public static final Printer inline = new InlinePrinter();
    public static final Printer tabular = new TabularPrinter();

    public static Printer tabular() {
        return tabular;
    }

    public static Printer tabular(int maxDisplayRows, int maxDisplayColumnWidth) {
        return new TabularPrinter(maxDisplayRows, maxDisplayColumnWidth);
    }
}
