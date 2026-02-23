package org.dflib.csv.parser.format;

import org.dflib.ValueMapper;

public class CsvColumnMapping {

    CsvColumnFormat format;
    int idx;
    String name;
    boolean skip;
    boolean compact;
    CsvColumnType type;
    boolean nullable;
    boolean nullableDefined;
    Object defaultValue;
    ValueMapper<String, ?> mapper;

    CsvColumnMapping(Builder builder) {
        this.format = builder.format;
        this.idx = builder.idx;
        this.name = builder.name;
        this.skip = builder.skip;
        this.compact = builder.compact;
        this.type = builder.type;
        this.mapper = builder.mapper;
        this.nullable = builder.nullable;
        this.nullableDefined = builder.nullableDefined;
        this.defaultValue = builder.defaultValue;
    }

    /**
     * Creates a column builder bound to a column index.
     */
    public static Builder column(int idx) {
        return new Builder().index(idx);
    }

    /**
     * Creates a column builder bound to a column name.
     */
    public static Builder column(String name) {
        return new Builder().name(name);
    }

    public CsvColumnFormat format() {
        return format;
    }

    /**
     * Returns zero-based column index, or {@code -1} when configured by name.
     */
    public int index() {
        return idx;
    }

    /**
     * Returns column name, or {@code null} when configured by index.
     */
    public String name() {
        return name;
    }

    /**
     * Returns whether this column is skipped.
     */
    public boolean skip() {
        return skip;
    }

    /**
     * Returns whether compact parsing is enabled.
     */
    public boolean compact() {
        return compact;
    }

    /**
     * Returns target column type.
     */
    public CsvColumnType type() {
        return type;
    }

    /**
     * Returns custom value mapper, or {@code null} when type-based mapping is used.
     */
    public ValueMapper<String, ?> mapper() {
        return mapper;
    }

    public boolean nullable() {
        return nullable;
    }

    public Object defaultValue() {
        return defaultValue;
    }

    /**
     * Fluent builder for {@link CsvColumnFormat}.
     */
    public static class Builder {

        CsvColumnFormat format;

        int idx;
        String name;
        boolean skip;
        boolean compact;
        CsvColumnType type;
        ValueMapper<String, ?> mapper;
        boolean nullableDefined;
        boolean nullable;
        Object defaultValue;

        Builder() {
            this.idx = -1;
            this.type = CsvColumnType.STRING;
        }

        /**
         * Merges values from another builder.
         */
        public Builder merge(Builder with) {
            if (with == null) {
                return this;
            }
            return merge(with.build());
        }

        public Builder merge(CsvColumnMapping with) {
            if (with == null) {
                return this;
            }

            if (with.format != null) {
                format(with.format);
            }
            if (with.idx != -1) {
                index(with.idx);
            }
            if (with.name != null) {
                name(with.name);
            }
            if (with.skip) {
                skip();
            }
            if (with.compact) {
                compact();
            }
            if (with.type != null) {
                type(with.type);
            }
            if (with.mapper != null) {
                mapper(with.mapper);
            }
            if (with.nullableDefined) {
                nullableWithDefault(with.nullable, with.defaultValue);
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

        public Builder format(CsvColumnFormat format) {
            this.format = format;
            return this;
        }

        public Builder format(CsvColumnFormat.Builder format) {
            return format(format.build());
        }

        /**
         * Enables or disables nullable values.
         */
        public Builder nullable(boolean nullable) {
            this.nullable = nullable;
            this.nullableDefined = true;
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
         * Builds immutable {@link CsvColumnFormat}.
         */
        public CsvColumnMapping build() {
            if (this.type == null) {
                this.type = CsvColumnType.STRING;
            }
            return new CsvColumnMapping(this);
        }

        /**
         * Returns whether this builder marks the column as skipped.
         */
        public boolean isSkipped() {
            return skip;
        }
    }

}
