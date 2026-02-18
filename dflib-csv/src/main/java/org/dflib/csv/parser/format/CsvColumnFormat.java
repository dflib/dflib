package org.dflib.csv.parser.format;

import org.dflib.ValueMapper;

/**
 * Per-column CSV parsing settings.
 * @since 2.0.0
 */
public class CsvColumnFormat {

    final int idx;
    final String name;
    final boolean skip;
    final boolean compact;
    final CsvColumnType type;
    final ValueMapper<String, ?> mapper;
    final Quote quote;
    final Trim trim;
    final boolean nullable;
    final char[] nullValue;
    final Object defaultValue;

    private CsvColumnFormat(Builder builder) {
        this.idx = builder.idx;
        this.name = builder.name;
        this.skip = builder.skip;
        this.compact = builder.compact;
        this.type = builder.type;
        this.mapper = builder.mapper;
        this.quote = builder.quote;
        this.trim = builder.trim;
        this.nullable = builder.nullable;
        this.nullValue = builder.nullValue;
        this.defaultValue = builder.defaultValue;
    }

    /**
     * Returns column name, or {@code null} when configured by index.
     */
    public String name() {
        return name;
    }

    /**
     * Returns zero-based column index, or {@code -1} when configured by name.
     */
    public int index() {
        return idx;
    }

    /**
     * Returns target column type.
     */
    public CsvColumnType type() {
        return type;
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

    /**
     * Returns whether this column is skipped.
     */
    public boolean skip() {
        return skip;
    }

    /**
     * Returns whether null values are allowed for this column.
     */
    public boolean nullable() {
        return nullable;
    }

    /**
     * Returns column-specific null token, or {@code null} for default behavior.
     */
    public char[] nullValue() {
        return nullValue;
    }

    /**
     * Returns custom value mapper, or {@code null} when type-based mapping is used.
     */
    public ValueMapper<String, ?> mapper() {
        return mapper;
    }

    /**
     * Returns default value used when nulls are mapped with defaults.
     */
    public Object defaultValue() {
        return defaultValue;
    }

    /**
     * Returns whether compact parsing is enabled.
     */
    public boolean compact() {
        return compact;
    }

    /**
     * Fluent builder for {@link CsvColumnFormat}.
     */
    public static class Builder {

        int idx;
        String name;
        boolean skip;
        boolean compact;
        CsvColumnType type;
        ValueMapper<String, ?> mapper;
        Quote quote;
        Trim trim;
        boolean nullable;
        char[] nullValue;
        Object defaultValue;
        boolean nullableDefined;

        Builder() {
            this.quote = null;
            this.trim = null;
            this.idx = -1;
        }

        /**
         * Merges values from another builder.
         */
        public Builder merge(Builder with) {
            if (with == null) {
                return this;
            }
            if (with.name != null) {
                name(with.name);
            }
            if (with.idx != -1) {
                index(with.idx);
            }
            if (with.skip) {
                skip();
            }
            if (with.compact) {
                compact();
            }
            if (with.trim != null) {
                trim(with.trim);
            }
            if (with.quote != null) {
                quote(with.quote);
            }
            if (with.type != null) {
                type(with.type);
            }
            if (with.mapper != null) {
                mapper(with.mapper);
            }
            if (with.nullableDefined) {
                nullable = with.nullable;
                nullValue = with.nullValue;
                defaultValue = with.defaultValue;
                nullableDefined = true;
            }
            return this;
        }

        /**
         * Sets column name.
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the target column type.
         */
        public Builder type(CsvColumnType type) {
            this.type = type;
            return this;
        }

        /**
         * Sets quote handling override.
         */
        public Builder quote(Quote quote) {
            this.quote = quote;
            return this;
        }

        /**
         * Sets trim override.
         */
        public Builder trim(Trim trim) {
            this.trim = trim;
            return this;
        }

        /**
         * Marks this column to be skipped.
         */
        public Builder skip() {
            this.skip = true;
            return this;
        }

        /**
         * Sets zero-based column index.
         */
        public Builder index(int idx) {
            this.idx = idx;
            return this;
        }

        /**
         * Enables or disables nullable values.
         */
        public Builder nullable(boolean nullable) {
            this.nullable = nullable;
            this.nullValue = null;
            nullableDefined = true;
            return this;
        }

        /**
         * Sets nullable mode and default mapped value.
         */
        public Builder nullableWithDefault(boolean nullable, Object defaultValue) {
            this.defaultValue = defaultValue;
            return nullable(nullable);
        }

        /**
         * Enables or disables nullable values and sets a null token.
         */
        public Builder nullable(boolean nullable, String nullValue) {
            this.nullable = nullable;
            this.nullValue = nullValue != null ? nullValue.toCharArray() : null;
            nullableDefined = true;
            return this;
        }

        /**
         * Sets nullable mode, null token, and default mapped value.
         */
        public Builder nullableWithDefault(boolean nullable, String nullValue, Object defaultValue) {
            this.defaultValue = defaultValue;
            return nullable(nullable, nullValue);
        }

        /**
         * Sets custom value mapper and switches type to {@link CsvColumnType#OTHER}.
         */
        public Builder mapper(ValueMapper<String, ?> mapper) {
            this.mapper = mapper;
            this.type = CsvColumnType.OTHER;
            return this;
        }

        /**
         * Enables compact parsing mode.
         */
        public Builder compact() {
            this.compact = true;
            return this;
        }

        /**
         * Builds immutable {@link CsvColumnFormat}.
         */
        public CsvColumnFormat build() {
            if (this.type == null) {
                this.type = CsvColumnType.STRING;
            }
            return new CsvColumnFormat(this);
        }

        /**
         * Returns whether this builder marks the column as skipped.
         */
        public boolean isSkipped() {
            return skip;
        }
    }
}
