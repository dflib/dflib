package com.nhl.dflib;

import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.print.Printer;
import com.nhl.dflib.print.TabularPrinter;

public class Printers {

    public static final Printer inline = new InlinePrinter();
    public static final Printer tabular = new TabularPrinter();

}
