package org.dflib;

import org.dflib.print.InlinePrinter;
import org.dflib.print.Printer;
import org.dflib.print.TabularPrinter;

public class Printers {

    public static final Printer inline = new InlinePrinter();
    public static final Printer tabular = new TabularPrinter();

    /**
     * @deprecated in favor of {@link #tabular}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Printer tabular() {
        return tabular;
    }

    /**
     * @deprecated in favor of {@link #tabular(int, int, int)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Printer tabular(int maxRows, int maxValueChars) {
        // "100" matches BasePrinter.MAX_COLS
        return tabular(maxRows, 100, maxValueChars);
    }

    /**
     * Returns a new tabular printer with the specified display parameters.
     *
     * @since 2.0.0
     */
    public static Printer tabular(int maxRows, int maxCols, int maxValueChars) {
        return new TabularPrinter(maxRows, maxCols, maxValueChars);
    }

    /**
     * Returns a new inline printer with the specified display parameters.
     *
     * @since 2.0.0
     */
    public static Printer inline(int maxRows, int maxCols, int maxValueChars) {
        return new InlinePrinter(maxRows, maxCols, maxValueChars);
    }
}
