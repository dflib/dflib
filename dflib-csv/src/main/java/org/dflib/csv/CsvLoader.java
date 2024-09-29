package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.dflib.DataFrame;
import org.dflib.DoubleValueMapper;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.RowPredicate;
import org.dflib.ValueMapper;
import org.dflib.builder.DataFrameAppender;
import org.dflib.builder.DataFrameByRowBuilder;
import org.dflib.collection.Iterators;
import org.dflib.sample.Sampler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A configurable loader of CSV files.
 */
public class CsvLoader {

    private HeaderStrategy headerStrategy;
    private ColumnExtractStrategy columnExtractStrategy;
    private final List<ColumnConfig> columnConfigs;

    private CSVFormat format;

    private RowPredicate rowCondition;
    private int rowSampleSize;
    private Random rowsSampleRandom;
    private int offset;
    private int limit = -1;

    public CsvLoader() {
        this.format = CSVFormat.DEFAULT;
        this.columnConfigs = new ArrayList<>();
    }

    /**
     * Skips the specified number of rows.
     *
     * @since 1.0.0-M20
     */
    public CsvLoader offset(int len) {
        this.offset = len;
        return this;
    }

    /**
     * Limits the max number of rows to the provided value
     *
     * @since 1.0.0-M20
     */
    public CsvLoader limit(int len) {
        this.limit = len;
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
     * @since 1.0.0-M20
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
     * @since 1.0.0-M20
     */
    public CsvLoader rowsSample(int size, Random random) {
        this.rowSampleSize = size;
        this.rowsSampleRandom = Objects.requireNonNull(random);
        return this;
    }

    /**
     * Configures the CSV loader to only include rows that are matching the provided condition. Applying the condition
     * during load would allow extracting only relevant data from very large CSVs using constant memory. Condition is
     * applied to an already converted row, with column names and positions matching the resulting DataFrame, not the
     * CSV columns.
     *
     * @since 1.0.0-M20
     */
    public CsvLoader rows(RowPredicate rowCondition) {
        this.rowCondition = rowCondition;
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
     * @since 0.7
     */
    public CsvLoader header(String... columns) {
        this.headerStrategy = HeaderStrategy.explicit(Index.of(columns));
        return this;
    }

    /**
     * Instead of using the first CSV row as a DataFrame header, generate a header with labels like "c0", "c1", etc.
     *
     * @since 1.0.0-M20
     */
    public CsvLoader generateHeader() {
        this.headerStrategy = HeaderStrategy.generated();
        return this;
    }

    /**
     * Configures the loader to only process the specified columns, and include them in the DataFrame in the specified
     * order. Column names argument refers to the names of the resulting DataFrame that may or may not match the CSV
     * columns header, depending on the loader settings.
     *
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader cols(String... columns) {
        this.columnExtractStrategy = ColumnExtractStrategy.ofCols(columns);
        return this;
    }

    /**
     * Configures the loader to only process the specified columns.
     *
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader cols(int... columns) {
        this.columnExtractStrategy = ColumnExtractStrategy.ofCols(columns);
        return this;
    }

    /**
     * Configures the loader to only process the columns that do not match the specified names. The remaining CSV columns
     * will be loaded in the order they are present in the CSV.
     *
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader colsExcept(String... columns) {
        this.columnExtractStrategy = ColumnExtractStrategy.ofColsExcept(columns);
        return this;
    }

    /**
     * Configures the loader to only process the columns that do not match the specified positions. The remaining CSV
     * columns will be loaded in the order they are present in the CSV.
     *
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader colsExcept(int... columns) {
        this.columnExtractStrategy = ColumnExtractStrategy.ofColsExcept(columns);
        return this;
    }

    /**
     * Provides a conversion function for a CSV column at a given position to produce a desired type.
     *
     * @since 1.0.0-RC1
     */
    public CsvLoader col(int column, ValueMapper<String, ?> mapper) {
        columnConfigs.add(ColumnConfig.objectCol(column, mapper, false));
        return this;
    }

    /**
     * Provides a conversion function of a CSV column at a given position to produce a desired type.
     *
     * @since 1.0.0-RC1
     */
    public CsvLoader col(String column, ValueMapper<String, ?> mapper) {
        columnConfigs.add(ColumnConfig.objectCol(column, mapper, false));
        return this;
    }

    /**
     * Configures a CSV column to be loaded without conversion (as a String), but with value compaction. Should be used
     * to save memory for low-cardinality columns.
     *
     * @since 1.0.0-RC1
     */
    public CsvLoader compactCol(int column) {
        columnConfigs.add(ColumnConfig.objectCol(column, true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded without conversion (as a String), but with value compaction. Should be used
     * to save memory for low-cardinality columns.
     *
     * @since 1.0.0-RC1
     */
    public CsvLoader compactCol(String column) {
        columnConfigs.add(ColumnConfig.objectCol(column, true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded with the specified conversion and with converted value compaction. Should
     * be used instead of {@link #col(int, ValueMapper)} to save memory for low-cardinality columns.
     *
     * @since 1.0.0-RC1
     */
    public CsvLoader compactCol(int column, ValueMapper<String, ?> mapper) {
        columnConfigs.add(ColumnConfig.objectCol(column, mapper, true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded with the specified conversion and with converted value compaction. Should
     * be used instead of {@link #col(String, ValueMapper)} to save memory for low-cardinality columns.
     *
     * @since 1.0.0-RC1
     */
    public CsvLoader compactCol(String column, ValueMapper<String, ?> mapper) {
        columnConfigs.add(ColumnConfig.objectCol(column, mapper, true));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader intCol(int column) {
        columnConfigs.add(ColumnConfig.intCol(column, IntValueMapper.fromString()));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader intCol(String column) {
        columnConfigs.add(ColumnConfig.intCol(column, IntValueMapper.fromString()));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader intCol(int column, int forNull) {
        columnConfigs.add(ColumnConfig.intCol(column, IntValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader intCol(String column, int forNull) {
        columnConfigs.add(ColumnConfig.intCol(column, IntValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longCol(int column) {
        columnConfigs.add(ColumnConfig.longCol(column, LongValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longCol(String column) {
        columnConfigs.add(ColumnConfig.longCol(column, LongValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longCol(int column, long forNull) {
        columnConfigs.add(ColumnConfig.longCol(column, LongValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longCol(String column, long forNull) {
        columnConfigs.add(ColumnConfig.longCol(column, LongValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader doubleCol(int column) {
        columnConfigs.add(ColumnConfig.doubleCol(column, DoubleValueMapper.fromString()));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader doubleCol(String column) {
        columnConfigs.add(ColumnConfig.doubleCol(column, DoubleValueMapper.fromString()));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader doubleCol(int column, double forNull) {
        columnConfigs.add(ColumnConfig.doubleCol(column, DoubleValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader doubleCol(String column, double forNull) {
        columnConfigs.add(ColumnConfig.doubleCol(column, DoubleValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to BigDecimals.
     *
     * @since 1.0.0-M23
     */
    public CsvLoader decimalCol(int column) {
        columnConfigs.add(ColumnConfig.objectCol(column, ValueMapper.stringToBigDecimal(), false));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to BigDecimals.
     *
     * @since 1.0.0-M23
     */
    public CsvLoader decimalCol(String column) {
        columnConfigs.add(ColumnConfig.objectCol(column, ValueMapper.stringToBigDecimal(), false));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to primitive booleans.
     *
     * @since 1.0.0-M23
     */
    public CsvLoader boolCol(int column) {
        columnConfigs.add(ColumnConfig.boolCol(column));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to primitive booleans.
     *
     * @since 1.0.0-M23
     */
    public CsvLoader boolCol(String column) {
        columnConfigs.add(ColumnConfig.boolCol(column));
        return this;
    }

    /**
     * Instructs the loader to convert values in the specified column to numbers of the specified type. This method will
     * result in "object" columns (and hence can store nulls). If you want a column with primitive numbers, use methods
     * like {@link #intCol(int)}, etc. instead.
     *
     * @since 1.0.0-M23
     */
    public CsvLoader numCol(int column, Class<? extends Number> type) {
        return col(column, numericMapper(type));
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader numCol(String column, Class<? extends Number> type) {
        return col(column, numericMapper(type));
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

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader dateCol(int column) {
        return col(column, ValueMapper.stringToDate());
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader dateCol(String column) {
        return col(column, ValueMapper.stringToDate());
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader dateCol(int column, DateTimeFormatter formatter) {
        return col(column, ValueMapper.stringToDate(formatter));
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader dateCol(String column, DateTimeFormatter formatter) {
        return col(column, ValueMapper.stringToDate(formatter));
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader dateTimeCol(int column) {
        return col(column, ValueMapper.stringToDateTime());
    }

    /**
     * @since 1.0.0-M23
     */
    public CsvLoader dateTimeCol(String column) {
        return col(column, ValueMapper.stringToDateTime());
    }


    /**
     * @since 1.0.0-M23
     */
    public CsvLoader dateTimeCol(int column, DateTimeFormatter formatter) {
        return col(column, ValueMapper.stringToDateTime(formatter));
    }

    /**
     * @since 1.0.0-M23
     */
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
     */
    public CsvLoader format(CSVFormat format) {
        this.format = format;
        return this;
    }

    /**
     * If called, the loader will convert missing values to nulls instead of empty strings.
     *
     * @since 0.6
     */
    public CsvLoader emptyStringIsNull() {
        this.format = format.withNullString("");
        return this;
    }

    /**
     * @since 0.11
     */
    public DataFrame load(Path filePath) {
        return load(filePath.toFile());
    }

    public DataFrame load(File file) {
        try (Reader r = new FileReader(file)) {
            return load(r);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + file, e);
        }
    }

    public DataFrame load(String filePath) {
        try (Reader r = new FileReader(filePath)) {
            return load(r);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    public DataFrame load(Reader reader) {

        Iterator<CSVRecord> it0 = read(reader);

        // "offset" is applied even if we read the header from the iterator
        Iterator<CSVRecord> it1 = offset > 0 ? Iterators.skip(it0, offset) : it0;
        CsvHeader csvHeader = createCsvHeader(it1);
        CsvColumnMap columnMap = createColumnMap(csvHeader.getHeader());

        // Some header strategies may peek inside the iterator, but not use the first row for the header.
        // So we need to re-add this row back to the DataFrame
        CSVRecord maybeUnconsumedDataRow = csvHeader.getMaybeUnconsumedDataRow();

        // The header does not count towards the limit, so apply the limit AFTER reading the header
        int limit = this.limit;
        if (limit == 0 || (maybeUnconsumedDataRow == null && !it1.hasNext())) {
            return DataFrame.empty(columnMap.getDfHeader());
        }

        Extractor<CSVRecord, ?>[] extractors = columnMap.extractors(this.columnConfigs);
        DataFrameByRowBuilder<CSVRecord, ?> builder = DataFrame
                .byRow(extractors)
                .columnIndex(columnMap.getDfHeader());

        if (rowSampleSize > 0) {
            builder.sampleRows(rowSampleSize, rowsSampleRandom);
        }

        if (rowCondition != null) {
            builder.selectRows(rowCondition);
        }

        DataFrameAppender<CSVRecord> appender = builder.appender();

        if (maybeUnconsumedDataRow != null) {
            appender.append(maybeUnconsumedDataRow);
            limit--;
        }

        Iterator<CSVRecord> it2 = limit >= 0 ? Iterators.limit(it1, limit) : it1;
        while (it2.hasNext()) {
            appender.append(it2.next());
        }

        return appender.toDataFrame();
    }

    private Iterator<CSVRecord> read(Reader reader) {
        try {
            return format.parse(reader).iterator();
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }

    private CsvColumnMap createColumnMap(Index csvHeader) {
        return columnExtractStrategy != null
                ? columnExtractStrategy.columnMap(csvHeader)
                : ColumnExtractStrategy.all().columnMap(csvHeader);
    }

    private CsvHeader createCsvHeader(Iterator<CSVRecord> it) {
        return headerStrategy != null
                ? headerStrategy.createCsvHeader(it)
                : HeaderStrategy.firstRow().createCsvHeader(it);
    }
}
