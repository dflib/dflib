package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvColumnsBuilder;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.csv.parser.mappers.QuoteProcessor;

import java.util.function.Function;

/**
 * Listener that generates the CSV header info based on the first row content.
 *
 * @param config CSV loader configuration.
 * @param columnsBuilder columns info gathered so far.
 */
record ColumnGeneratorListener(CsvParserConfig config, CsvColumnsBuilder columnsBuilder) implements ColumnDetectionListener {

    @Override
    public void columnsDetected(DataSlice[] data) {
        if (config.excludeHeaderValues()) {
            // use header values as columns names
            Function<DataSlice, String> unescape = QuoteProcessor.forFormat(config.csvFormat(), config.csvFormat().quote());
            for (int i = 0; i < data.length; i++) {
                columnsBuilder.merge(CsvColumnMapping.column(i).name(unescape.apply(data[i])));
            }
        } else {
            // just generate column names
            for (int i = 0; i < data.length; i++) {
                columnsBuilder.merge(CsvColumnMapping.column(i).name("c" + i));
            }
        }
        columnsBuilder.merge(config.columnMappings());
    }
}
