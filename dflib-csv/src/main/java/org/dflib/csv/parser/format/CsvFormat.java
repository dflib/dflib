package org.dflib.csv.parser.format;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * CSV file format specification.
 * <p>
 * Usage: <pre>{@code
 *
 * CsvFormat myFormat = CsvFormat.defaultFormat()
 *      .skipEmptyRows()
 *      .delimiter(";")
 *      .enableComments("#")
 *      .allowEmptyColumns()
 *      .build();
 *
 * DataFrame df = Csv.loader().format(myFormat).load(Path.of("./my.csv"));
 *
 * }</pre>
 *
 * @since 2.0.0
 */
public class CsvFormat {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFormat.class);

    // global parser rules
    final Delimiter delimiter;
    final boolean trailingDelimiter;
    final LineBreak lineBreak;
    final String comment;
    final boolean skipEmptyRows;
    final boolean allowEmptyColumns;

    // could be overridden on a column level
    final Trim trim;
    final Quote quote;
    final Escape escape;
    final char escapeChar;
    final String nullString;

    private CsvFormat(Builder builder) {
        this.delimiter = builder.delimiter;
        this.lineBreak = builder.lineBreak;
        this.trim = builder.trim;
        this.quote = builder.quote;
        this.escape = builder.escape;
        this.escapeChar = builder.escapeChar;
        this.comment = builder.comment;
        this.skipEmptyRows = builder.skipEmptyRows;
        this.allowEmptyColumns = builder.allowEmptyColumns;
        this.nullString = builder.nullString;
        this.trailingDelimiter = builder.trailingDelimiter;
    }

    /**
     * Creates a builder with default CSV settings.
     */
    public static Builder defaultFormat() {
        return new Builder();
    }

    public static CsvColumnFormat.Builder columnFormat() {
        return new CsvColumnFormat.Builder();
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
     * Returns a token recognized as null, or {@code null} for default behavior.
     */
    public String nullString() {
        return nullString;
    }

    /**
     * Returns whether a trailing delimiter at the end of each row is ignored.
     */
    public boolean trailingDelimiter() {
        return trailingDelimiter;
    }

    /**
     * Fluent builder for {@link CsvFormat}.
     */
    public static class Builder {
        Delimiter delimiter;
        LineBreak lineBreak;
        Trim trim;
        Quote quote;
        Escape escape;
        char escapeChar;
        String comment;
        boolean skipEmptyRows;
        boolean allowEmptyColumns;
        String nullString;
        boolean trailingDelimiter;

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
            nullString = null;
            trailingDelimiter = false;
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
         * String value that should be converted to {@code null}
         */
        public Builder nullString(String nullString) {
            this.nullString = nullString;
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
                this.nullString = nullString;
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
        public Builder copyFrom(CsvFormat format) {
            Objects.requireNonNull(format);

            this.delimiter = format.delimiter;
            this.lineBreak = format.lineBreak;
            this.trim = format.trim;
            this.quote = format.quote;
            this.escape = format.escape;
            this.escapeChar = format.escapeChar;
            this.comment = format.comment;
            this.skipEmptyRows = format.skipEmptyRows;
            this.allowEmptyColumns = format.allowEmptyColumns;
            this.nullString = format.nullString;
            this.trailingDelimiter = format.trailingDelimiter;

            return this;
        }

        /**
         * Builds immutable {@link CsvFormat}.
         */
        public CsvFormat build() {
            validate();
            return new CsvFormat(this);
        }

        private void validate() {
            new CsvFormatValidator(this).validate();
        }
    }
}
