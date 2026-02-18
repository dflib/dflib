package org.dflib.csv.parser.format;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;
import org.dflib.RowPredicate;
import org.dflib.csv.CsvSchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * CSV parsing configuration with global and per-column options.
 *
 * @since 2.0.0
 */
public class CsvFormat {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFormat.class);

    final int sizeHint;
    final Delimiter delimiter;
    final LineBreak lineBreak;
    final Trim trim;
    final Quote quote;
    final Escape escape;
    final char escapeChar;
    final String comment;
    final boolean skipEmptyRows;
    final boolean allowEmptyColumns;
    final boolean nullable;
    final String nullValue;
    final List<CsvColumnFormat.Builder> columnFormats;
    final boolean autoColumns;
    final boolean excludeHeaderValues;
    final boolean trailingDelimiter;
    final int limit;
    final int offset;
    final CsvSchemaFactory schemaFactory;
    final RowPredicate rowCondition;
    final int rowSampleSize;
    final Random rowsSampleRandom;

    private CsvFormat(Builder builder) {
        this.sizeHint = builder.sizeHint;
        this.delimiter = builder.delimiter;
        this.lineBreak = builder.lineBreak;
        this.trim = builder.trim;
        this.quote = builder.quote;
        this.escape = builder.escape;
        this.escapeChar = builder.escapeChar;
        this.comment = builder.comment;
        this.skipEmptyRows = builder.skipEmptyRows;
        this.allowEmptyColumns = builder.allowEmptyColumns;
        this.nullable = builder.nullable;
        this.nullValue = builder.nullValue;
        this.columnFormats = builder.columnFormats;
        this.autoColumns = builder.autoColumns;
        this.excludeHeaderValues = builder.excludeHeaderValues;
        this.trailingDelimiter = builder.trailingDelimiter;
        this.limit = builder.limit;
        this.offset = builder.offset;
        this.schemaFactory = builder.schemaFactory;
        this.rowCondition = builder.rowCondition;
        this.rowSampleSize = builder.rowSampleSize;
        this.rowsSampleRandom = builder.rowsSampleRandom;
    }

    /**
     * Creates a builder with default CSV settings.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the default CSV format.
     */
    public static CsvFormat defaultFormat() {
        return builder().build();
    }

    /**
     * Creates a column builder bound to a column index.
     */
    public static CsvColumnFormat.Builder column(int idx) {
        return new CsvColumnFormat.Builder()
                .index(idx)
                .type(CsvColumnType.STRING);
    }

    /**
     * Creates a column builder bound to a column name.
     */
    public static CsvColumnFormat.Builder column(String name) {
        return new CsvColumnFormat.Builder()
                .name(name)
                .type(CsvColumnType.STRING);
    }

    /**
     * Returns a field delimiter.
     */
    public Delimiter delimiter() {
        return delimiter;
    }

    /**
     * Returns a configured line break strategy.
     */
    public LineBreak lineBreak() {
        return lineBreak;
    }

    /**
     * Returns a configured trimming mode.
     */
    public Trim trim() {
        return trim;
    }

    /**
     * Returns quote handling settings.
     */
    public Quote quote() {
        return quote;
    }

    /**
     * Returns an escape mode.
     */
    public Escape escape() {
        return escape;
    }

    /**
     * Returns a custom escape char, or {@code 0} when unused.
     */
    public char escapeChar() {
        return escapeChar;
    }

    /**
     * Returns a comment prefix, or {@code null} when comments are disabled.
     */
    public String comment() {
        return comment;
    }

    /**
     * Returns whether empty rows are ignored.
     */
    public boolean skipEmptyRows() {
        return skipEmptyRows;
    }

    /**
     * Returns whether empty columns are allowed.
     */
    public boolean allowEmptyColumns() {
        return allowEmptyColumns;
    }

    /**
     * Returns whether null values are allowed.
     */
    public boolean nullable() {
        return nullable;
    }

    /**
     * Returns a token recognized as null, or {@code null} for default behavior.
     */
    public String nullValue() {
        return nullValue;
    }

    /**
     * Returns configured per-column builders.
     */
    public List<CsvColumnFormat.Builder> columnBuilders() {
        return columnFormats;
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
     * Returns whether a trailing delimiter at the end of each row is ignored.
     */
    public boolean trailingDelimiter() {
        return trailingDelimiter;
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
     * Returns an optional size hint for internal allocations.
     */
    public int sizeHint() {
        return sizeHint;
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
     * Fluent builder for {@link CsvFormat}.
     */
    public static class Builder {
        int sizeHint;
        Delimiter delimiter;
        LineBreak lineBreak;
        Trim trim;
        Quote quote;
        Escape escape;
        char escapeChar;
        String comment;
        boolean skipEmptyRows;
        boolean allowEmptyColumns;
        boolean nullable;
        String nullValue;
        List<CsvColumnFormat.Builder> columnFormats;
        boolean autoColumns;
        boolean excludeHeaderValues;
        boolean trailingDelimiter;
        int limit;
        int offset;
        CsvSchemaFactory schemaFactory;
        boolean explicitAutoColumns;
        final List<CsvColumnFormat.Builder> columnBuilders;
        RowPredicate rowCondition;
        int rowSampleSize;
        Random rowsSampleRandom;

        private Builder() {
            delimiter = new Delimiter(",");
            lineBreak = LineBreak.AUTO;
            trim = Trim.NONE;
            quote = Quote.optionalOf('"');
            escape = Escape.DOUBLE;
            escapeChar = 0;
            comment = null;
            skipEmptyRows = true;
            allowEmptyColumns = false;
            nullable = false;
            nullValue = null;
            excludeHeaderValues = true;
            autoColumns = true;
            trailingDelimiter = false;
            limit = -1;
            offset = 0;
            sizeHint = 0;
            schemaFactory = CsvSchemaFactory.all();
            columnBuilders = new ArrayList<>();
        }

        /**
         * Sets field delimiter.
         */
        public Builder delimiter(String delimiter) {
            this.delimiter = new Delimiter(delimiter);
            return this;
        }

        /**
         * Sets line break detection mode.
         */
        public Builder lineBreak(LineBreak lineBreak) {
            this.lineBreak = lineBreak;
            return this;
        }

        /**
         * Sets trimming mode.
         */
        public Builder trim(Trim trim) {
            this.trim = trim;
            return this;
        }

        /**
         * Sets quote handling mode.
         */
        public Builder quote(Quote quote) {
            this.quote = quote;
            if (this.escape == Escape.CUSTOM
                    && this.escapeChar == quote.quoteChar()) {
                this.escape = Escape.DOUBLE;
                this.escapeChar = 0;
            }
            return this;
        }

        /**
         * Sets escape mode.
         * <p>
         * {@link Escape#DOUBLE} applies to quoted values.
         * {@link Escape#BACKSLASH} and {@link Escape#CUSTOM} also allow escaping delimiters in unquoted values.
         */
        public Builder escape(Escape escape) {
            this.escape = escape;
            if (escape != Escape.CUSTOM) {
                this.escapeChar = 0;
            } else if (this.quote != null
                    && this.escapeChar == this.quote.quoteChar()) {
                this.escape = Escape.DOUBLE;
                this.escapeChar = 0;
            }
            return this;
        }

        /**
         * Sets a custom escape char (equivalent to {@link Escape#CUSTOM}).
         * <p>
         * If the char matches the quote char, escape mode falls back to {@link Escape#DOUBLE}.
         */
        public Builder escape(char escapeChar) {
            if (this.quote != null && escapeChar == this.quote.quoteChar()) {
                this.escape = Escape.DOUBLE;
                this.escapeChar = 0;
            } else {
                this.escape = Escape.CUSTOM;
                this.escapeChar = escapeChar;
            }
            return this;
        }

        /**
         * Enables comment lines with a custom prefix.
         */
        public Builder enableComments(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Enables comment lines with {@code #} prefix.
         */
        public Builder enableComments() {
            return enableComments("#");
        }

        /**
         * Skips empty rows.
         */
        public Builder skipEmptyRows() {
            this.skipEmptyRows = true;
            return this;
        }

        /**
         * Allows empty columns in parsed rows.
         */
        public Builder allowEmptyColumns() {
            this.allowEmptyColumns = true;
            return this;
        }

        /**
         * Enables or disables nullable values.
         */
        public Builder nullable(boolean nullable) {
            this.nullable = nullable;
            this.nullValue = null;
            return this;
        }

        /**
         * Enables or disables nullable values and sets a null token.
         */
        public Builder nullable(boolean nullable, String nullValue) {
            this.nullable = nullable;
            this.nullValue = nullValue;
            return this;
        }

        /**
         * Adds a per-column format.
         */
        public Builder column(CsvColumnFormat.Builder columnBuilder) {
            this.columnBuilders.add(columnBuilder);
            return this;
        }

        /**
         * Enables or disables trailing delimiter handling.
         * When enabled, a delimiter at the end of each row is ignored rather than treated as an empty final column.
         */
        public Builder trailingDelimiter(boolean trailingDelimiter) {
            this.trailingDelimiter = trailingDelimiter;
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
         * Enables or disables automatic column inference.
         */
        public Builder autoColumns(boolean autoColumns) {
            this.autoColumns = autoColumns;
            explicitAutoColumns = true;
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
         * Sets optional size hint for internal allocations.
         */
        public Builder sizeHint(int sizeHint) {
            this.sizeHint = sizeHint;
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
         * Sets row filter condition.
         */
        public Builder rowCondition(RowPredicate rowCondition) {
            this.rowCondition = Objects.requireNonNull(rowCondition);
            return this;
        }

        /**
         * Copies compatible settings from Apache Commons CSV format.
         */
        public void copyFrom(CSVFormat format) {
            validateCommonsFormat(format);

            String delimiter = format.getDelimiterString();
            if (delimiter != null && !delimiter.isEmpty()) {
                this.delimiter = new Delimiter(delimiter);
            }

            this.lineBreak = switch (format.getRecordSeparator()) {
                case "\r" -> LineBreak.CR;
                case "\n" -> LineBreak.LF;
                default -> LineBreak.AUTO;
            };

            Character commentMarker = format.getCommentMarker();
            if (commentMarker != null) {
                this.comment = (String.valueOf(commentMarker));
            }

            if (format.getIgnoreEmptyLines()) {
                this.skipEmptyRows = true;
            }

            if (format.getIgnoreSurroundingSpaces() || format.getTrim()) {
                this.trim = Trim.FULL;
            }

            String nullString = format.getNullString();
            if (nullString != null) {
                this.nullable = true;
                this.nullValue = nullString;
            }

            Character quoteChar = format.getQuoteCharacter();
            if (quoteChar == null || format.getQuoteMode() == QuoteMode.NONE) {
                this.quote = Quote.none();
            } else {
                this.quote = Quote.optionalOf(quoteChar);
            }

            Character escapeChar = format.getEscapeCharacter();
            if (escapeChar == null) {
                if (quoteChar == null) {
                    this.escape = Escape.NONE;
                } else {
                    this.escape = Escape.DOUBLE;
                }
            } else if (escapeChar == '\\') {
                this.escape = Escape.BACKSLASH;
            } else {
                this.escape = Escape.CUSTOM;
                this.escapeChar = escapeChar;
            }

            if (format.getTrailingDelimiter()) {
                this.trailingDelimiter = true;
            }
        }

        private void validateCommonsFormat(CSVFormat format) {
            Objects.requireNonNull(format);
            DuplicateHeaderMode duplicateHeaderMode = format.getDuplicateHeaderMode();
            if (duplicateHeaderMode != null && duplicateHeaderMode != DuplicateHeaderMode.ALLOW_ALL) {
                LOGGER.warn("CSVFormat.duplicateHeaderMode ({}) is not supported by CsvFormat and will be ignored", duplicateHeaderMode);
            }

            if (format.getIgnoreHeaderCase()) {
                LOGGER.warn("CSVFormat.ignoreHeaderCase=true is not supported by CsvFormat and will be ignored");
            }

            if (!format.getAllowMissingColumnNames()) {
                LOGGER.warn("CSVFormat.allowMissingColumnNames=false is not supported by CsvFormat; missing column names are always allowed");
            }

            if (!format.getLenientEof()) {
                LOGGER.warn("CSVFormat.lenientEof=false is not supported by CsvFormat; lenient EOF handling is always enabled");
            }

            if (format.getTrailingData()) {
                LOGGER.warn("CSVFormat.trailingData=true is not supported by CsvFormat and will be ignored");
            }
        }

        /**
         * Copies settings from another {@link CsvFormat}.
         */
        public void copyFrom(CsvFormat format) {
            Objects.requireNonNull(format);

            this.sizeHint = format.sizeHint;
            this.delimiter = format.delimiter;
            this.lineBreak = format.lineBreak;
            this.trim = format.trim;
            this.quote = format.quote;
            this.escape = format.escape;
            this.escapeChar = format.escapeChar;
            this.comment = format.comment;
            this.skipEmptyRows = format.skipEmptyRows;
            this.allowEmptyColumns = format.allowEmptyColumns;
            this.nullable = format.nullable;
            this.nullValue = format.nullValue;
            this.autoColumns = format.autoColumns;
            this.explicitAutoColumns = true;
            this.excludeHeaderValues = format.excludeHeaderValues;
            this.trailingDelimiter = format.trailingDelimiter;
            this.limit = format.limit;
            this.offset = format.offset;
            this.schemaFactory = format.schemaFactory;
            this.rowCondition = format.rowCondition;
            this.rowSampleSize = format.rowSampleSize;
            this.rowsSampleRandom = format.rowsSampleRandom;

            this.columnBuilders.clear();
            format.columnFormats.forEach(cb -> this.columnBuilders.add(new CsvColumnFormat.Builder().merge(cb)));
        }

        /**
         * Builds immutable {@link CsvFormat}.
         */
        public CsvFormat build() {
            validate();
            this.columnFormats = columnBuilders.stream()
                    .peek(cb -> {
                        if (cb.quote == null) {
                            cb.quote(this.quote);
                        }
                        if (cb.trim == null) {
                            cb.trim(this.trim);
                        }
                        if (!cb.nullableDefined) {
                            cb.nullable = this.nullable;
                            cb.nullValue = this.nullValue == null
                                    ? null
                                    : this.nullValue.toCharArray();
                        } else {
                            if (cb.nullable && cb.nullValue == null) {
                                cb.nullValue = this.nullValue == null
                                        ? null
                                        : this.nullValue.toCharArray();
                            }
                        }
                    })
                    .collect(Collectors.toList());
            return new CsvFormat(this);
        }

        private void validate() {
            new CsvFormatValidator(this).validate();
        }
    }
}
