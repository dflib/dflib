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
    private int limit;

    public CsvLoader() {
        this.format = CSVFormat.DEFAULT;
        this.columnConfigs = new ArrayList<>();
        this.limit = -1;
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
     * Skips the specified number of rows. E.g. if the header is defined manually, you might call this method with "1"
     * as an argument.
     *
     * @param n number of rows to skip
     * @return this loader instance
     * @deprecated the new alternative is calling {@link #offset(int)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public CsvLoader skipRows(int n) {
        return offset(n);
    }

    /**
     * Configures CSV loader to select a sample of the rows of a CSV. Unlike {@link DataFrame#sampleRows(int, Random)},
     * this method will prevent the full CSV from loading in memory, and hence can be used on potentially very large
     * CSVs. If you are executing multiple sampling runs in parallel, consider using {@link #rowsSample(int, Random)},
     * as this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the CSV size (as the CSV size is not known upfront).
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader rowsSample(int size) {
        return rowsSample(size, Sampler.getDefaultRandom());
    }

    /**
     * @since 0.7
     * @deprecated in favor of {@link #rowsSample(int)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public CsvLoader sampleRows(int size) {
        return rowsSample(size);
    }

    /**
     * Configures CSV loader to select a sample of the rows of a CSV. Unlike {@link DataFrame#sampleRows(int, Random)},
     * this method will prevent the full CSV from loading in memory, and hence can be used on potentially very large
     * CSVs.
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
     * @since 0.7
     * @deprecated in favor of {@link #rowsSample(int, Random)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public CsvLoader sampleRows(int size, Random random) {
        return rowsSample(size, random);
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
     * @since 0.16
     * @deprecated in favor of {@link #rows(RowPredicate)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public CsvLoader selectRows(RowPredicate condition) {
        return rows(condition);
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
     * @return this loader instance
     * @since 0.7
     * @deprecated in favor of {@link #cols(String...)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public CsvLoader selectColumns(String... columns) {
        return cols(columns);
    }

    /**
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader cols(int... columns) {
        this.columnExtractStrategy = ColumnExtractStrategy.ofCols(columns);
        return this;
    }

    /**
     * @return this loader instance
     * @since 0.7
     * @deprecated in favor of {@link #cols(int...)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public CsvLoader selectColumns(int... columns) {
        return cols(columns);
    }

    /**
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader colsExcept(String... columns) {
        this.columnExtractStrategy = ColumnExtractStrategy.ofColsExcept(columns);
        return this;
    }

    /**
     * @return this loader instance
     * @since 1.0.0-M20
     */
    public CsvLoader colsExcept(int... columns) {
        this.columnExtractStrategy = ColumnExtractStrategy.ofColsExcept(columns);
        return this;
    }

    /**
     * @return this loader instance
     * @since 0.7
     * @deprecated in favor of {@link #colsExcept(String...)}
     */
    @Deprecated(since = "1.0.0-M20", forRemoval = true)
    public CsvLoader dropColumns(String... columns) {
        return colsExcept(columns);
    }

    public CsvLoader columnType(int column, ValueMapper<String, ?> mapper) {
        columnConfigs.add(ColumnConfig.objectColumn(column, mapper));
        return this;
    }

    public CsvLoader columnType(String column, ValueMapper<String, ?> mapper) {
        columnConfigs.add(ColumnConfig.objectColumn(column, mapper));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(int column) {
        columnConfigs.add(ColumnConfig.intColumn(column, IntValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(String column) {
        columnConfigs.add(ColumnConfig.intColumn(column, IntValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(int column, int forNull) {
        columnConfigs.add(ColumnConfig.intColumn(column, IntValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(String column, int forNull) {
        columnConfigs.add(ColumnConfig.intColumn(column, IntValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(int column) {
        columnConfigs.add(ColumnConfig.longColumn(column, LongValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(String column) {
        columnConfigs.add(ColumnConfig.longColumn(column, LongValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(int column, long forNull) {
        columnConfigs.add(ColumnConfig.longColumn(column, LongValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(String column, long forNull) {
        columnConfigs.add(ColumnConfig.longColumn(column, LongValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(int column) {
        columnConfigs.add(ColumnConfig.doubleColumn(column, DoubleValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(String column) {
        columnConfigs.add(ColumnConfig.doubleColumn(column, DoubleValueMapper.fromString()));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(int column, double forNull) {
        columnConfigs.add(ColumnConfig.doubleColumn(column, DoubleValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(String column, double forNull) {
        columnConfigs.add(ColumnConfig.doubleColumn(column, DoubleValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.16
     */
    public CsvLoader boolColumn(int column) {
        columnConfigs.add(ColumnConfig.boolColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader boolColumn(String column) {
        columnConfigs.add(ColumnConfig.boolColumn(column));
        return this;
    }

    /**
     * Instructs the loader to convert values in the specified column to numbers of the specified type. This method will
     * result in "object" columns (and hence can store nulls). If you want a column with primitive numbers, use methods
     * like {@link #intColumn(int)}, etc. instead.
     *
     * @since 0.6
     */
    public CsvLoader numColumn(int column, Class<? extends Number> type) {
        return columnType(column, numericMapper(type));
    }

    /**
     * @since 0.6
     */
    public CsvLoader numColumn(String column, Class<? extends Number> type) {
        return columnType(column, numericMapper(type));
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
     * @since 0.6
     */
    public CsvLoader dateColumn(int column) {
        return columnType(column, ValueMapper.stringToDate());
    }

    /**
     * @since 0.6
     */
    public CsvLoader dateColumn(String column) {
        return columnType(column, ValueMapper.stringToDate());
    }

    /**
     * @since 0.6
     */
    public CsvLoader dateTimeColumn(int column) {
        return columnType(column, ValueMapper.stringToDateTime());
    }

    /**
     * @since 0.6
     */
    public CsvLoader dateTimeColumn(String column) {
        return columnType(column, ValueMapper.stringToDateTime());
    }

    /**
     * @since 0.6
     */
    public CsvLoader dateColumn(int column, DateTimeFormatter formatter) {
        return columnType(column, ValueMapper.stringToDate(formatter));
    }

    /**
     * @since 0.6
     */
    public CsvLoader dateColumn(String column, DateTimeFormatter formatter) {
        return columnType(column, ValueMapper.stringToDate(formatter));
    }

    /**
     * @since 0.6
     */
    public CsvLoader dateTimeColumn(int column, DateTimeFormatter formatter) {
        return columnType(column, ValueMapper.stringToDateTime(formatter));
    }

    /**
     * @since 0.6
     */
    public CsvLoader dateTimeColumn(String column, DateTimeFormatter formatter) {
        return columnType(column, ValueMapper.stringToDateTime(formatter));
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
        DataFrameByRowBuilder<CSVRecord, ?> builder = DataFrame.byRow(extractors).columnIndex(columnMap.getDfHeader());

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
