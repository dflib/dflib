package org.dflib.csv.parser.format;

/**
 * Supported logical CSV column types.
 * <p>
 * Use {@link CsvColumnType#OTHER} + a custom value mapper for any type that is not directly supported.
 * @since 2.0.0
 */
public enum CsvColumnType {
    STRING,
    BOOLEAN,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    BIG_INTEGER,
    BIG_DECIMAL,
    OTHER
}
