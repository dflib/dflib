package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvColumnsBuilder;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.csv.parser.mappers.ExtractorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Utility class that drives the result DataFrame build process.
 */
class DataFrameBuilder implements DataCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFrameBuilder.class);

    private final CsvParserConfig config;
    private final CsvColumnsBuilder columnsBuilder;
    CsvRowAppender appender;

    // the current columns, updated by addColumns()
    private int csvWidth;
    private Index dfHeader;
    private Extractor<DataSlice[], ?>[] extractors;

    private int initialDfWidth;

    DataFrameBuilder(CsvParserConfig config, CsvColumnsBuilder columnsBuilder) {
        this.config = config;
        this.columnsBuilder = columnsBuilder;
    }

    public DataFrame buildDataFrame() {
        if (dfHeader.size() > initialDfWidth) {
            LOGGER.warn("Some rows have more values than the header ({} columns), the extra values were loaded as " +
                            "columns {}. If this is unexpected, check for the rows above the header " +
                            "(see CsvLoader.offset(..)), or for the trailing delimiters",
                    initialDfWidth,
                    Arrays.toString(Arrays.copyOfRange(dfHeader.toArray(), initialDfWidth, dfHeader.size())));
        }

        return appender.toDataFrame();
    }

    @Override
    public void onNewRow(DataSlice[] row) {
        if (row.length > csvWidth) {
            addColumns(row.length);
        }

        try {
            appender.append(row);
        } catch (RuntimeException e) {
            throw conversionError(row, e);
        }
    }

    CsvRowAppender buildAppender(List<CsvColumnMapping> columns) {
        resolveColumns(columns);
        this.initialDfWidth = dfHeader.size();
        return appender = new CsvRowAppender(dfHeader, extractors, config);
    }

    private void resolveColumns(List<CsvColumnMapping> columns) {
        CsvSchema schema = config.schemaFactory().schema(csvIndex(columns));
        this.csvWidth = columns.size();
        this.dfHeader = schema.getDfHeader();
        this.extractors = ExtractorBuilder.buildExtractors(config.csvFormat(), columns, schema);
    }

    /**
     * Adds columns for a row wider than the known columns, unless the schema excludes them (e.g., explicitly selected
     * columns).
     */
    private void addColumns(int width) {
        for (int i = csvWidth; i < width; i++) {
            columnsBuilder.merge(CsvColumnMapping.column(i));
        }

        int dfWidth = dfHeader.size();
        resolveColumns(columnsBuilder.build(config));
        if (dfHeader.size() > dfWidth) {
            appender.addColumns(dfHeader, extractors);
        }
    }

    /**
     * Rechecks the row to find the column that can't be converted.
     */
    private RuntimeException conversionError(DataSlice[] row, RuntimeException e) {
        for (int i = 0; i < extractors.length; i++) {
            try {
                extract(extractors[i], row);
            } catch (RuntimeException columnError) {
                return new IllegalStateException("Can't convert value of column '" + dfHeader.get(i) + "': "
                        + columnError.getMessage(), e);
            }
        }
        return e;
    }

    private static <T> void extract(Extractor<DataSlice[], T> extractor, DataSlice[] row) {
        extractor.extractAndStore(row, extractor.createHolder());
    }

    private static Index csvIndex(List<CsvColumnMapping> columns) {
        return Index.ofDeduplicated(columns.stream()
                .map(CsvColumnMapping::name)
                .toArray(String[]::new)
        );
    }
}
