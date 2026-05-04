package org.dflib.csv.printer;

/**
 * Factory for creating configured CSV printers.
 *
 * @since 2.0.0
 */
public interface CsvPrinterFactory {

    /**
     * Creates a printer for the supplied configuration.
     *
     * @param config printer configuration
     * @return a configured printer
     */
    CsvPrinter create(CsvPrinterConfig config);

    /**
     * Returns the default factory used by {@code CsvSaver}.
     *
     * @return default printer factory
     */
    static CsvPrinterFactory defaultFactory() {
        return DefaultCsvPrinter::new;
    }
}
