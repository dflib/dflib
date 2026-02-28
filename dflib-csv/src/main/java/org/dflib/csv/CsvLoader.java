package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.DataFrame;
import org.dflib.RowPredicate;
import org.dflib.ValueMapper;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.CsvParser;
import org.dflib.csv.parser.CsvSchemaFactory;
import org.dflib.csv.parser.format.CsvColumnFormat;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvColumnType;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.sample.Sampler;

import java.io.File;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A configurable loader of CSV files.
 */
public class CsvLoader {

    /**
     * Config builder
     */
    private final CsvParserConfig.Builder configBuilder;

    public CsvLoader() {
        this.configBuilder = CsvParserConfig.builder();
    }

    /**
     * Sets a compression codec for this saver. If not set, the saver will try to determine compression preferences
     * using the target file extension. So this method is especially useful if the target is not a file.
     *
     * @since 2.0.0
     */
    public CsvLoader compression(Codec codec) {
        this.configBuilder.compressionCodec(codec);
        return this;
    }

    /**
     * Checks the source leading bytes for "byte order mark" (BOM) placed there by certain CSV generators, and, if
     * present, uses it to determine content encoding. If the encoding is set explicitly by the user, the BOM is
     * stripped from the stream and is otherwise ignored. Without this setting, the BOM bytes will be treated as content
     * and usually prepended to the name of the first column in the resulting DataFrame. This is highly confusing, since
     * those symbols are invisible.
     *
     * <p>This setting is "safe" in the sense that it works with files with or without a BOM. It is not a default in
     * {@link CsvLoader} though, as it creates some minor overhead on load.
     */
    public CsvLoader checkByteOrderMark() {
        this.configBuilder.checkByteOrderMark(true);
        return this;
    }

    /**
     * @since 1.1.0
     */
    public CsvLoader encoding(String encoding) {
        return encoding(encoding != null ? Charset.forName(encoding) : null);
    }

    /**
     * Allows changing the encoding from the platform default. Ignored if the DataFrame is loaded from a Reader
     * (that performs character decoding on its own).
     *
     * @since 1.1.0
     */
    public CsvLoader encoding(Charset encoding) {
        this.configBuilder.encoding(encoding);
        return this;
    }

    /**
     * Skips the specified number of rows.
     */
    public CsvLoader offset(int len) {
        this.configBuilder.offset(len);
        return this;
    }

    /**
     * Limits the max number of rows to the provided value
     */
    public CsvLoader limit(int len) {
        this.configBuilder.limit(len);
        return this;
    }

    /**
     * Configures CSV loader to select a sample of the rows of a CSV. This method will prevent the full CSV from loading
     * in memory, and hence can be used on potentially very large CSVs. If you are executing multiple sampling runs in
     * parallel, consider using {@link #rowsSample(int, Random)}, as this method is using a shared {@link Random}
     * instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the CSV size (as the CSV size is not known upfront).
     * @return this loader instance
     */
    public CsvLoader rowsSample(int size) {
        return rowsSample(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures CSV loader to select a sample of the rows of a CSV. This method will prevent the full CSV from loading
     * in memory, and hence can be used on potentially very large CSVs.
     *
     * @param size   the size of the sample. Can be bigger than the CSV size (as the CSV size is not known upfront).
     * @param random a custom random number generator
     * @return this loader instance
     */
    public CsvLoader rowsSample(int size, Random random) {
        this.configBuilder.rowSampleSize(size).rowsSampleRandom(random);
        return this;
    }

    /**
     * Configures the CSV loader to only include rows that are matching the provided condition. Applying the condition
     * during load would allow extracting only relevant data from very large CSVs using constant memory. Condition is
     * applied to an already converted row, with column names and positions matching the resulting DataFrame, not the
     * CSV columns.
     */
    public CsvLoader rows(RowPredicate rowCondition) {
        this.configBuilder.rowCondition(rowCondition);
        return this;
    }

    /**
     * Provides an explicit header for the processed CSV. If not set, the first row of CSV will be treated as a header.
     * If set, the first row will be treated as data. Column names are assigned to CSV columns positionally from left
     * to right. Header provided here must have a size less or equal to the number of columns in the CSV.
     * <p>The columns of the result DataFrame may be the same or a subset of the header columns (if further filtered via
     * the various "cols(..)" methods</p>.
     *
     * @param columns user-defined DataFrame columns
     * @return this loader instance
     */
    public CsvLoader header(String... columns) {
        // columns are defined by the user, parser should use them
        this.configBuilder
                .autoColumns(false)
                .excludeHeaderValues(false);
        for(int i=0; i<columns.length; i++) {
            this.configBuilder.column(CsvColumnMapping.column(columns[i]).index(i));
        }
        return cols(columns);
    }

    /**
     * Instead of using the first CSV row as a DataFrame header, generate a header with labels like "c0", "c1", etc.
     */
    public CsvLoader generateHeader() {
        this.configBuilder
                .autoColumns(true)
                .excludeHeaderValues(false);
        return this;
    }

    /**
     * Configures the loader to only process the specified columns, and include them in the DataFrame in the specified
     * order. Column names argument refers to the names of the resulting DataFrame that may or may not match the CSV
     * columns header, depending on the loader settings.
     *
     * @return this loader instance
     */
    public CsvLoader cols(String... columns) {
        this.configBuilder.schemaFactory(CsvSchemaFactory.ofCols(columns));
        return this;
    }

    /**
     * Configures the loader to only process the specified columns.
     *
     * @return this loader instance
     */
    public CsvLoader cols(int... columns) {
        this.configBuilder.schemaFactory(CsvSchemaFactory.ofCols(columns));
        return this;
    }

    /**
     * Configures the loader to only process the columns that do not match the specified names. The remaining CSV columns
     * will be loaded in the order they are present in the CSV.
     *
     * @return this loader instance
     */
    public CsvLoader colsExcept(String... columns) {
        this.configBuilder.schemaFactory(CsvSchemaFactory.ofColsExcept(columns));
        return this;
    }

    /**
     * Configures the loader to only process the columns that do not match the specified positions. The remaining CSV
     * columns will be loaded in the order they are present in the CSV.
     *
     * @return this loader instance
     */
    public CsvLoader colsExcept(int... columns) {
        this.configBuilder.schemaFactory(CsvSchemaFactory.ofColsExcept(columns));
        return this;
    }

    /**
     * Provides a conversion function for a CSV column at a given position to produce a desired type.
     */
    public CsvLoader col(int column, ValueMapper<String, ?> mapper) {
        this.configBuilder.column(CsvColumnMapping.column(column).mapper(mapper).nullable(true));
        return this;
    }

    /**
     * Provides a conversion function of a CSV column at a given position to produce a desired type.
     */
    public CsvLoader col(String column, ValueMapper<String, ?> mapper) {
        this.configBuilder.column(CsvColumnMapping.column(column).mapper(mapper).nullable(true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded without conversion (as a String), but with value compaction. Should be used
     * to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(int column) {
        this.configBuilder.column(CsvColumnMapping.column(column).compact());
        return this;
    }

    /**
     * Configures a CSV column to be loaded without conversion (as a String), but with value compaction. Should be used
     * to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(String column) {
        this.configBuilder.column(CsvColumnMapping.column(column).compact());
        return this;
    }

    /**
     * Configures a CSV column to be loaded with the specified conversion and with converted value compaction. Should
     * be used instead of {@link #col(int, ValueMapper)} to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(int column, ValueMapper<String, ?> mapper) {
        this.configBuilder.column(CsvColumnMapping.column(column).mapper(mapper).compact());
        return this;
    }

    /**
     * Configures a CSV column to be loaded with the specified conversion and with converted value compaction. Should
     * be used instead of {@link #col(String, ValueMapper)} to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(String column, ValueMapper<String, ?> mapper) {
        this.configBuilder.column(CsvColumnMapping.column(column).mapper(mapper).compact());
        return this;
    }

    /**
     * Set format override for the column
     * @see CsvFormat#columnFormat()
     * @since 2.0.0
     */
    public CsvLoader colFormat(int column, CsvColumnFormat format) {
        this.configBuilder.column(CsvColumnMapping.column(column).format(format));
        return this;
    }

    /**
     * Set format override for the column
     * @see CsvFormat#columnFormat()
     * @since 2.0.0
     */
    public CsvLoader colFormat(String column, CsvColumnFormat format) {
        this.configBuilder.column(CsvColumnMapping.column(column).format(format));
        return this;
    }

    public CsvLoader intCol(int column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.INTEGER));
        return this;
    }

    public CsvLoader intCol(String column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.INTEGER));
        return this;
    }

    public CsvLoader intCol(int column, int forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.INTEGER)
                .nullableWithDefault(true, forNull));
        return this;
    }

    public CsvLoader intCol(String column, int forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.INTEGER)
                .nullableWithDefault(true, forNull));
        return this;
    }

    public CsvLoader longCol(int column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.LONG));
        return this;
    }

    public CsvLoader longCol(String column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.LONG));
        return this;
    }

    public CsvLoader longCol(int column, long forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.LONG)
                .nullableWithDefault(true, forNull));
        return this;
    }

    public CsvLoader longCol(String column, long forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.LONG)
                .nullableWithDefault(true, forNull));
        return this;
    }

    /**
     * @since 1.1.0
     */
    public CsvLoader floatCol(int column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.FLOAT));
        return this;
    }

    /**
     * @since 1.1.0
     */
    public CsvLoader floatCol(String column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.FLOAT));
        return this;
    }

    public CsvLoader floatCol(int column, float forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.FLOAT)
                .nullableWithDefault(true, forNull));
        return this;
    }

    public CsvLoader floatCol(String column, float forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.FLOAT)
                .nullableWithDefault(true, forNull));
        return this;
    }

    public CsvLoader doubleCol(int column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.DOUBLE));
        return this;
    }

    public CsvLoader doubleCol(String column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.DOUBLE));
        return this;
    }

    public CsvLoader doubleCol(int column, double forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.DOUBLE)
                .nullableWithDefault(true, forNull));
        return this;
    }

    public CsvLoader doubleCol(String column, double forNull) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.DOUBLE)
                .nullableWithDefault(true, forNull));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to BigDecimals.
     */
    public CsvLoader decimalCol(int column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.BIG_DECIMAL)
                .nullable(true));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to BigDecimals.
     */
    public CsvLoader decimalCol(String column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.BIG_DECIMAL)
                .nullable(true));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to primitive booleans.
     */
    public CsvLoader boolCol(int column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.BOOLEAN));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to primitive booleans.
     */
    public CsvLoader boolCol(String column) {
        this.configBuilder.column(CsvColumnMapping.column(column).type(CsvColumnType.BOOLEAN));
        return this;
    }

    /**
     * Instructs the loader to convert values in the specified column to numbers of the specified type. This method will
     * result in "object" columns (and hence can store nulls). If you want a column with primitive numbers, use methods
     * like {@link #intCol(int)}, etc. instead.
     */
    public CsvLoader numCol(int column, Class<? extends Number> type) {
        return col(column, numericMapper(type));
    }

    /**
     * Instructs the loader to convert values in the specified column to numbers of the specified type. This method will
     * result in "object" columns (and hence can store nulls). If you want a column with primitive numbers, use methods
     * like {@link #intCol(String)}, etc. instead.
     */
    public CsvLoader numCol(String column, Class<? extends Number> type) {
        return col(column, numericMapper(type));
    }

    public CsvLoader dateCol(int column) {
        return col(column, ValueMapper.stringToDate());
    }

    public CsvLoader dateCol(String column) {
        return col(column, ValueMapper.stringToDate());
    }

    public CsvLoader dateCol(int column, DateTimeFormatter formatter) {
        return col(column, ValueMapper.stringToDate(formatter));
    }

    public CsvLoader dateCol(String column, DateTimeFormatter formatter) {
        return col(column, ValueMapper.stringToDate(formatter));
    }

    public CsvLoader dateTimeCol(int column) {
        return col(column, ValueMapper.stringToDateTime());
    }

    public CsvLoader dateTimeCol(String column) {
        return col(column, ValueMapper.stringToDateTime());
    }

    public CsvLoader dateTimeCol(int column, DateTimeFormatter formatter) {
        return col(column, ValueMapper.stringToDateTime(formatter));
    }

    public CsvLoader dateTimeCol(String column, DateTimeFormatter formatter) {
        return col(column, ValueMapper.stringToDateTime(formatter));
    }

    /**
     * Optionally sets the style or format of the imported CSV. CSVFormat comes from "commons-csv" library and
     * contains a number of predefined formats, such as CSVFormat.MYSQL, etc. It also allows to customize the format
     * further, by defining custom delimiters, line separators, etc.
     *
     * @param format a format object defined in commons-csv library
     * @return this loader instance
     * @deprecated use {@link #format(CsvFormat)} instead
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public CsvLoader format(CSVFormat format) {
        if(format == null) {
            return this;
        }
        this.configBuilder.csvFormat(format);
        return this;
    }

    /**
     * Optionally sets the style or format of the imported CSV. Allows customizing the format by defining custom
     * delimiters, line separators, etc.
     *
     * @param format CSV format to use
     * @return this
     *
     * @see CsvFormat#defaultFormat()
     *
     * @since 2.0.0
     */
    public CsvLoader format(CsvFormat format) {
        if(format == null) {
            return this;
        }
        this.configBuilder.csvFormat(format);
        return this;
    }

    /**
     * Read empty cells as nulls instead of empty strings. Equivalent to calling <code>nullString("")</code>
     *
     * @see #nullString(String)
     */
    public CsvLoader emptyStringIsNull() {
        return nullString("");
    }

    /**
     * Configures the loader to read as nulls cells whose values are equal to the "nullString" argument.
     *
     * @see #emptyStringIsNull()
     * @since 1.2.0
     */
    public CsvLoader nullString(String nullString) {
        this.configBuilder.nullable(true);
        this.configBuilder.csvFormatBuilder()
                .nullString(Objects.requireNonNull(nullString));
        return this;
    }

    /**
     * If any CSV rows are shorter than the header, pad those with nulls.
     * If not set, and such condition is encountered, an exception is thrown.
     *
     * @since 2.0.0
     */
    public CsvLoader nullPadRows() {
        this.configBuilder.nullable(true);
        this.configBuilder.csvFormatBuilder().allowEmptyColumns();
        return this;
    }

    public DataFrame load(Path filePath) {
        return load(ByteSource.ofPath(filePath));
    }

    public DataFrame load(File file) {
        return load(ByteSource.ofFile(file));
    }

    public DataFrame load(String filePath) {
        return load(ByteSource.ofFile(filePath));
    }

    public DataFrame load(Reader reader) {
        return new CsvParser(configBuilder.build()).parse(reader);
    }

    /**
     * @since 1.1.0
     */
    public DataFrame load(ByteSource src) {
        return new CsvParser(configBuilder.build()).parse(src);
    }

    /**
     * @since 1.1.0
     */
    public Map<String, DataFrame> loadAll(ByteSources srcs) {
        return srcs.process((name, src) -> load(src));
    }

    private ValueMapper<String, ?> numericMapper(Class<? extends Number> type) {
        if (Integer.class.equals(type)) {
            return ValueMapper.stringToInt();
        }
        if (Long.class.equals(type)) {
            return ValueMapper.stringToLong();
        }
        if (Double.class.equals(type)) {
            return ValueMapper.stringToDouble();
        }
        if (Float.class.equals(type)) {
            return ValueMapper.stringToFloat();
        }
        if (BigDecimal.class.equals(type)) {
            return ValueMapper.stringToBigDecimal();
        }
        if (BigInteger.class.equals(type)) {
            return ValueMapper.stringToBigInteger();
        }
        throw new IllegalArgumentException("Can't map numeric type to a string converter: " + type);
    }
}
