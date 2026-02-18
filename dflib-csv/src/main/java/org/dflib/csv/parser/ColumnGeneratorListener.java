package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnsBuilder;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.mappers.QuoteProcessor;

import java.util.function.Function;

/**
 * Listener that generates the CSV header info based on the first row content.
 *
 * @param format CSV file format defined by the loader.
 * @param columnsBuilder columns info gathered so far.
 */
record ColumnGeneratorListener(CsvFormat format, CsvColumnsBuilder columnsBuilder) implements ColumnDetectionListener {

    @Override
    public void columnsDetected(DataSlice[] data) {
        if (format.excludeHeaderValues()) {
            // use header values as columns names
            Function<DataSlice, String> unescape = QuoteProcessor.forFormat(format, format.quote(), char[]::new);
            for (int i = 0; i < data.length; i++) {
                columnsBuilder.merge(CsvFormat.column(i).name(unescape.apply(data[i])));
            }
        } else {
            // just generate column names
            for (int i = 0; i < data.length; i++) {
                columnsBuilder.merge(CsvFormat.column(i).name("c" + i));
            }
        }
        columnsBuilder.merge(format.columnBuilders());
    }
}
