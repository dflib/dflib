package org.dflib.csv.parser.format;

import java.util.Objects;

/**
 * Per-column CSV parsing settings.
 * This format allows overriding global configuration defined in the {@link CsvFormat}.
 *
 * @see CsvFormat#columnFormat() builder method
 * @see org.dflib.csv.CsvLoader#colFormat(int, CsvColumnFormat)
 * @see org.dflib.csv.CsvLoader#colFormat(String, CsvColumnFormat)
 * @since 2.0.0
 */
public class CsvColumnFormat {

    // these fields are from the CsvFormat with per-field override
    final Quote quote;
    final Trim trim;
    final Escape escape;
    final char escapeChar;
    final char[] nullString;

    private CsvColumnFormat(Builder builder) {
        this.quote = builder.quote;
        this.trim = builder.trim;
        this.escape = builder.escape;
        this.escapeChar = builder.escapeChar;
        this.nullString = builder.nullString;
    }

    /**
     * Utility method that merges a missing or partial column format with the global format settings.
     */
    static CsvColumnFormat mergeWithConfig(CsvColumnFormat columnFormat, CsvFormat format) {
        if (columnFormat == null) {
            return new Builder()
                    .quote(format.quote())
                    .trim(format.trim())
                    .escape(format.escape())
                    .escape(format.escapeChar())
                    .nullString(format.nullString())
                    .build();
        }
        return new Builder()
                .quote(columnFormat.quote() == null ? format.quote() : columnFormat.quote())
                .trim(columnFormat.trim() == null ? format.trim() : columnFormat.trim())
                .escape(columnFormat.escape() == null ? format.escape() : columnFormat.escape())
                .escape(columnFormat.escapeChar() == 0 ? format.escapeChar() : columnFormat.escapeChar())
                .nullString(columnFormat.nullString() == null
                        ? format.nullString() == null ? null : format.nullString().toCharArray()
                        : columnFormat.nullString())
                .build();
    }

    /**
     * Returns quote handling override, or {@code null} to use global settings.
     */
    public Quote quote() {
        return quote;
    }

    /**
     * Returns trim override, or {@code null} to use global settings.
     */
    public Trim trim() {
        return trim;
    }

    public Escape escape() {
        return escape;
    }

    public char escapeChar() {
        return escapeChar;
    }

    /**
     * Returns column-specific null token, or {@code null} for default behavior.
     */
    public char[] nullString() {
        return nullString;
    }

    /**
     * Fluent builder for {@link CsvColumnFormat}.
     */
    public static class Builder {

        Quote quote;
        Trim trim;
        Escape escape;
        char escapeChar;
        char[] nullString;

        Builder() {
        }

        /**
         * Sets quote handling override.
         */
        public Builder quote(Quote quote) {
            this.quote = Objects.requireNonNull(quote);
            return this;
        }

        /**
         * Sets trim override.
         */
        public Builder trim(Trim trim) {
            this.trim = Objects.requireNonNull(trim);
            return this;
        }

        /**
         * Sets escape mode.
         * <p>
         * {@link Escape#DOUBLE} applies to quoted values.
         * {@link Escape#BACKSLASH} and {@link Escape#CUSTOM} also allow escaping delimiters in unquoted values.
         */
        public Builder escape(Escape escape) {
            this.escape = Objects.requireNonNull(escape);
            if (escape != Escape.CUSTOM) {
                escapeChar = 0;
            }
            return this;
        }

        /**
         * Sets a custom escape char (equivalent to {@link Escape#CUSTOM}).
         * <p>
         * If the char matches the quote char, escape mode falls back to {@link Escape#DOUBLE}.
         */
        public Builder escape(char escapeChar) {
            this.escape = Escape.CUSTOM;
            this.escapeChar = escapeChar;
            return this;
        }

        /**
         * Set null string that will be parsed as {@code null} value.
         */
        public Builder nullString(String nullString) {
            this.nullString = nullString == null ? null : nullString.toCharArray();
            return this;
        }

        /**
         * Set null string that will be parsed as {@code null} value.
         */
        public Builder nullString(char[] nullString) {
            this.nullString = nullString;
            return this;
        }

        /**
         * Builds immutable {@link CsvColumnFormat}.
         */
        public CsvColumnFormat build() {
            return new CsvColumnFormat(this);
        }
    }
}
