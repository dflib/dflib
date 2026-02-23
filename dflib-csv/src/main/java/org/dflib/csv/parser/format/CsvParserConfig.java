package org.dflib.csv.parser.format;

import org.apache.commons.csv.CSVFormat;
import org.dflib.RowPredicate;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.CsvSchemaFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Per-file configuration of the parser.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 */
public class CsvParserConfig {

    // input options
    final Charset encoding;
    final Codec compressionCodec;
    final boolean checkByteOrderMark;

    // parsing rules
    final CsvFormat csvFormat;
    final CsvSchemaFactory schemaFactory;
    final boolean autoColumns;
    final boolean excludeHeaderValues;
    final boolean nullable;
    final List<CsvColumnMapping> mappings;

    // data sampling
    final int limit;
    final int offset;
    final RowPredicate rowCondition;
    final int rowSampleSize;
    final Random rowsSampleRandom;


    CsvParserConfig(Builder builder, CsvFormat format, List<CsvColumnMapping> columnFormats) {
        this.csvFormat = format;
        this.mappings = columnFormats;
        this.nullable = builder.nullable;

        this.encoding = builder.encoding;
        this.compressionCodec = builder.compressionCodec;
        this.checkByteOrderMark = builder.checkByteOrderMark;

        this.schemaFactory = builder.schemaFactory;
        this.autoColumns = builder.autoColumns;
        this.excludeHeaderValues = builder.excludeHeaderValues;

        this.limit = builder.limit;
        this.offset = builder.offset;
        this.rowCondition = builder.rowCondition;
        this.rowSampleSize = builder.rowSampleSize;
        this.rowsSampleRandom = builder.rowsSampleRandom;
    }

    /**
     * Creates a loader config builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns CSV format settings.
     */
    public CsvFormat csvFormat() {
        return csvFormat;
    }

    public Charset encoding() {
        return encoding;
    }

    public Codec compressionCodec() {
        return compressionCodec;
    }

    public boolean checkByteOrderMark() {
        return checkByteOrderMark;
    }

    /**
     * Returns max number of rows to read, or negative for no limit.
     */
    public int limit() {
        return limit;
    }

    /**
     * Returns number of initial rows to skip.
     */
    public int offset() {
        return offset;
    }

    /**
     * Returns a schema factory used to build output schema.
     */
    public CsvSchemaFactory schemaFactory() {
        return schemaFactory;
    }

    /**
     * Returns row filter predicate, or {@code null} when no filter is set.
     */
    public RowPredicate rowCondition() {
        return rowCondition;
    }

    /**
     * Returns number of rows to sample for inference.
     */
    public int rowSampleSize() {
        return rowSampleSize;
    }

    /**
     * Returns random source for row sampling, or {@code null} when default is used.
     */
    public Random rowsSampleRandom() {
        return rowsSampleRandom;
    }

    /**
     * Returns whether missing columns are inferred automatically.
     */
    public boolean autoColumns() {
        return autoColumns;
    }

    /**
     * Returns whether header values are excluded from type inference.
     */
    public boolean excludeHeaderValues() {
        return excludeHeaderValues;
    }

    /**
     * Returns whether null values are allowed.
     */
    public boolean nullable() {
        return nullable;
    }

    /**
     * Returns configured per-column builders.
     */
    public List<CsvColumnMapping> columnMappings() {
        return mappings;
    }

    public static class Builder {
        CsvFormat.Builder csvFormatBuilder;
        Charset encoding;
        Codec compressionCodec;
        boolean checkByteOrderMark;
        int limit;
        int offset;
        CsvSchemaFactory schemaFactory;
        RowPredicate rowCondition;
        int rowSampleSize;
        Random rowsSampleRandom;
        boolean nullable;
        boolean autoColumns;
        boolean excludeHeaderValues;
        boolean explicitAutoColumns;
        List<CsvColumnMapping.Builder> columnBuilders;

        Builder() {
            this.csvFormatBuilder = CsvFormat.defaultFormat();
            this.encoding = Charset.defaultCharset();
            this.compressionCodec = null;
            this.checkByteOrderMark = false;
            this.limit = -1;
            this.offset = 0;
            this.schemaFactory = CsvSchemaFactory.all();
            this.rowCondition = null;
            this.rowSampleSize = 0;
            this.rowsSampleRandom = null;
            this.nullable = false;
            this.excludeHeaderValues = true;
            this.autoColumns = true;
            this.columnBuilders = new ArrayList<>();
        }

        /**
         * Sets CSV format settings.
         */
        public Builder csvFormat(CsvFormat csvFormat) {
            this.csvFormatBuilder().copyFrom(csvFormat);
            return this;
        }

        /**
         * Sets CSV format settings.
         *
         * @see CsvFormat#defaultFormat()
         */
        public Builder csvFormat(CsvFormat.Builder csvFormat) {
            this.csvFormatBuilder().copyFrom(csvFormat.build());
            return this;
        }

        /**
         * @param format to copy from
         * @return this builder
         * @deprecated since 2.0.0, only for the backward compatibility with the commons-csv parser
         */
        @Deprecated(forRemoval = true)
        public Builder csvFormat(CSVFormat format) {
            this.csvFormatBuilder().copyFrom(format);
            return this;
        }

        public CsvFormat.Builder csvFormatBuilder() {
            return csvFormatBuilder;
        }

        /**
         * Allows changing the encoding from the platform default.
         * Ignored if the DataFrame is loaded from a Reader.
         */
        public Builder encoding(Charset encoding) {
            this.encoding = Objects.requireNonNull(encoding);
            return this;
        }

        /**
         * Sets a compression codec for this saver. If not set, the saver will try to determine compression preferences
         * using the target file extension. So this method is especially useful if the target is not a file.
         */
        public Builder compressionCodec(Codec compressionCodec) {
            this.compressionCodec = compressionCodec;
            return this;
        }

        public Builder checkByteOrderMark(boolean checkByteOrderMark) {
            this.checkByteOrderMark = checkByteOrderMark;
            return this;
        }

        /**
         * Sets max number of rows to read.
         */
        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Sets number of initial rows to skip.
         */
        public Builder offset(int offset) {
            this.offset = offset;
            return this;
        }

        /**
         * Sets schema factory for the resulting frame.
         */
        public Builder schemaFactory(CsvSchemaFactory schemaFactory) {
            this.schemaFactory = Objects.requireNonNull(schemaFactory);
            return this;
        }

        /**
         * Sets row filter condition.
         */
        public Builder rowCondition(RowPredicate rowCondition) {
            this.rowCondition = Objects.requireNonNull(rowCondition);
            return this;
        }

        /**
         * Sets the number of rows to sample for inference.
         */
        public Builder rowSampleSize(int rowSampleSize) {
            this.rowSampleSize = rowSampleSize;
            return this;
        }

        /**
         * Sets the random source for row sampling.
         */
        public Builder rowsSampleRandom(Random rowsSampleRandom) {
            this.rowsSampleRandom = Objects.requireNonNull(rowsSampleRandom);
            return this;
        }

        /**
         * Adds a per-column format.
         */
        public Builder column(CsvColumnMapping.Builder columnBuilder) {
            this.columnBuilders.add(columnBuilder);
            return this;
        }

        /**
         * Enables or disables nullable values.
         */
        public Builder nullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        /**
         * Enables or disables automatic column inference.
         */
        public Builder autoColumns(boolean autoColumns) {
            this.explicitAutoColumns = true;
            this.autoColumns = autoColumns;
            return this;
        }

        /**
         * Controls whether header row values are excluded from type inference.
         */
        public Builder excludeHeaderValues(boolean excludeHeaderValues) {
            this.excludeHeaderValues = excludeHeaderValues;
            return this;
        }

        /**
         * Builds immutable {@link CsvParserConfig}.
         */
        public CsvParserConfig build() {
            validate();
            CsvFormat csvFormat = csvFormatBuilder.build();

            List<CsvColumnMapping> columnMappings = columnBuilders.stream()
                    .peek(cb -> cb.format(CsvColumnFormat.mergeWithConfig(cb.format, csvFormat)))
                    .map(CsvColumnMapping.Builder::build)
                    .toList();

            this.encoding = encoding != null
                    ? encoding
                    : Charset.defaultCharset();
            return new CsvParserConfig(this, csvFormat, columnMappings);
        }

        private void validate() {
            new CsvParserConfigValidator(this).validate();
        }
    }
}
