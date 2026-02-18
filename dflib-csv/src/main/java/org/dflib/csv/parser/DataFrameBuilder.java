package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.builder.DataFrameAppender;
import org.dflib.builder.DataFrameByRowBuilder;
import org.dflib.csv.CsvSchema;
import org.dflib.csv.CsvSchemaFactory;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnFormat;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.mappers.ExtractorBuilder;

import java.util.List;

/**
 * Utility class that drives the result DataFrame build process.
 */
class DataFrameBuilder {

    private final CsvFormat format;
    DataFrameAppender<DataSlice[]> appender;

    DataFrameBuilder(CsvFormat format) {
        this.format = format;
    }

    public DataFrame buildDataFrame() {
        return appender.toDataFrame();
    }

    DataFrameAppender<DataSlice[]> buildAppender(List<CsvColumnFormat> effectiveColumns) {
        Index csvHeader = csvIndex(effectiveColumns);
        CsvSchemaFactory csvSchemaFactory = format.schemaFactory();
        CsvSchema schema = csvSchemaFactory.schema(csvHeader);

        Extractor<DataSlice[], ?>[] extractors = ExtractorBuilder
                .buildExtractors(format, effectiveColumns, schema);

        DataFrameByRowBuilder<DataSlice[], ?> builder = DataFrame
                .byRow(extractors)
                .columnIndex(schema.getDfHeader());
        if (format.rowSampleSize() > 0) {
            builder.sampleRows(format.rowSampleSize(), format.rowsSampleRandom());
        }
        if (format.rowCondition() != null) {
            builder.selectRows(format.rowCondition());
        }
        return appender = builder.appender();
    }

    private static Index csvIndex(List<CsvColumnFormat> columns) {
        return Index.ofDeduplicated(columns.stream()
                .map(CsvColumnFormat::name)
                .toArray(String[]::new)
        );
    }
}
