package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.builder.DataFrameAppender;
import org.dflib.builder.DataFrameByRowBuilder;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnFormat;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.csv.parser.mappers.ExtractorBuilder;

import java.util.List;

/**
 * Utility class that drives the result DataFrame build process.
 */
class DataFrameBuilder {

    private final CsvParserConfig config;
    DataFrameAppender<DataSlice[]> appender;

    DataFrameBuilder(CsvParserConfig config) {
        this.config = config;
    }

    public DataFrame buildDataFrame() {
        return appender.toDataFrame();
    }

    DataFrameAppender<DataSlice[]> buildAppender(List<CsvColumnMapping> effectiveColumns) {
        Index csvHeader = csvIndex(effectiveColumns);
        CsvSchemaFactory csvSchemaFactory = config.schemaFactory();
        CsvSchema schema = csvSchemaFactory.schema(csvHeader);

        Extractor<DataSlice[], ?>[] extractors = ExtractorBuilder
                .buildExtractors(config.csvFormat(), effectiveColumns, schema);

        DataFrameByRowBuilder<DataSlice[], ?> builder = DataFrame
                .byRow(extractors)
                .columnIndex(schema.getDfHeader());
        if (config.rowSampleSize() > 0) {
            builder.sampleRows(config.rowSampleSize(), config.rowsSampleRandom());
        }
        if (config.rowCondition() != null) {
            builder.selectRows(config.rowCondition());
        }
        return appender = builder.appender();
    }

    private static Index csvIndex(List<CsvColumnMapping> columns) {
        return Index.ofDeduplicated(columns.stream()
                .map(CsvColumnMapping::name)
                .toArray(String[]::new)
        );
    }
}
