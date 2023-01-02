package com.nhl.dflib.csv;

import com.nhl.dflib.*;
import com.nhl.dflib.csv.loader.CsvSeriesAppender;
import com.nhl.dflib.csv.loader.ColumnConfig;
import com.nhl.dflib.csv.loader.CsvCell;
import com.nhl.dflib.csv.loader.RowFilterConfig;
import com.nhl.dflib.sample.Sampler;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

public class CsvLoader {

    private int skipRows;
    private Index header;

    private String[] includeColumns;
    private int[] includeColumnPositions;
    private String[] dropColumns;

    private CSVFormat format;

    private int rowSampleSize;
    private Random rowsSampleRandom;

    private final List<ColumnConfig> columns;
    private final List<RowFilterConfig<?>> rowFilters;

    public CsvLoader() {
        this.format = CSVFormat.DEFAULT;
        this.columns = new ArrayList<>();
        this.rowFilters = new ArrayList<>();
    }

    /**
     * Skips the specified number of rows. E.g. if the header is defined manually, you might call this method with "1"
     * as an argument.
     *
     * @param n number of rows to skip
     * @return this loader instance
     */
    public CsvLoader skipRows(int n) {
        this.skipRows = n;
        return this;
    }

    /**
     * Configures CSV loader to select a sample of the rows of a CSV. Unlike {@link DataFrame#sampleRows(int, Random)},
     * this method will prevent the full CSV from loading in memory, and hence can be used on potentially very large
     * CSVs. If you are executing multiple sampling runs in parallel, consider using {@link #sampleRows(int, Random)},
     * as this method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the CSV size (as the CSV size is not known upfront).
     * @return this loader instance
     * @since 0.7
     */
    public CsvLoader sampleRows(int size) {
        return sampleRows(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures CSV loader to select a sample of the rows of a CSV. Unlike {@link DataFrame#sampleRows(int, Random)},
     * this method will prevent the full CSV from loading in memory, and hence can be used on potentially very large
     * CSVs.
     *
     * @param size   the size of the sample. Can be bigger than the CSV size (as the CSV size is not known upfront).
     * @param random a custom random number generator
     * @return this loader instance
     * @since 0.7
     */
    public CsvLoader sampleRows(int size, Random random) {
        this.rowSampleSize = size;
        this.rowsSampleRandom = Objects.requireNonNull(random);
        return this;
    }

    /**
     * Configures CSV loader to only include rows that are matching the provided criterion. Applying the condition
     * during load would allow extracting relevant data from very large CSVs.
     *
     * @param columnName the name of the column the condition applies to
     * @param condition  column value condition that needs to be fulfilled for the row to be included in the resulting DataFrame.
     * @return this loader instance
     * @since 0.11
     */
    public <V> CsvLoader selectRows(String columnName, ValuePredicate<V> condition) {
        rowFilters.add(RowFilterConfig.create(columnName, condition));
        return this;
    }

    /**
     * Configures CSV loader to only include rows that are matching the provided criterion. Applying the condition
     * during load would allow extracting relevant data from very large CSVs.
     *
     * @param columnPos position of the column the condition applies to
     * @param condition column value condition that needs to be fulfilled for the row to be included in the resulting DataFrame.
     * @return this loader instance
     * @since 0.11
     */
    public <V> CsvLoader selectRows(int columnPos, ValuePredicate<V> condition) {
        rowFilters.add(RowFilterConfig.create(columnPos, condition));
        return this;
    }

    /**
     * Provides an alternative header to the returned DataFrame. If set, the first row of CSV is treated as data, not
     * as header. Column names are assigned to CSV columns positionally from left to right. Header provided here must
     * have a size less or equal to the number of columns in the CSV.
     *
     * @param columns user-defined DataFrame columns
     * @return this loader instance
     * @since 0.7
     */
    public CsvLoader header(String... columns) {
        this.header = Index.forLabels(columns);
        return this;
    }

    /**
     * @return this loader instance
     * @since 0.7
     */
    public CsvLoader selectColumns(String... columns) {
        this.includeColumnPositions = null;
        this.includeColumns = columns;
        return this;
    }

    /**
     * @return this loader instance
     * @since 0.7
     */
    public CsvLoader selectColumns(int... columns) {
        this.includeColumnPositions = columns;
        this.includeColumns = null;
        return this;
    }

    /**
     * @return this loader instance
     * @since 0.7
     */
    public CsvLoader dropColumns(String... columns) {
        this.dropColumns = columns;
        return this;
    }

    public CsvLoader columnType(int column, ValueMapper<String, ?> mapper) {
        columns.add(ColumnConfig.objectColumn(column, mapper));
        return this;
    }

    public CsvLoader columnType(String column, ValueMapper<String, ?> mapper) {
        columns.add(ColumnConfig.objectColumn(column, mapper));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(int column) {
        columns.add(ColumnConfig.intColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(String column) {
        columns.add(ColumnConfig.intColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(int column, int forNull) {
        columns.add(ColumnConfig.intColumn(column, IntValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(String column, int forNull) {
        columns.add(ColumnConfig.intColumn(column, IntValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(int column) {
        columns.add(ColumnConfig.longColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(String column) {
        columns.add(ColumnConfig.longColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(int column, long forNull) {
        columns.add(ColumnConfig.longColumn(column, LongValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(String column, long forNull) {
        columns.add(ColumnConfig.longColumn(column, LongValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(int column) {
        columns.add(ColumnConfig.doubleColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(String column) {
        columns.add(ColumnConfig.doubleColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(int column, double forNull) {
        columns.add(ColumnConfig.doubleColumn(column, DoubleValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(String column, double forNull) {
        columns.add(ColumnConfig.doubleColumn(column, DoubleValueMapper.fromString(forNull)));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader booleanColumn(int column) {
        columns.add(ColumnConfig.booleanColumn(column));
        return this;
    }

    /**
     * @since 0.6
     */
    public CsvLoader booleanColumn(String column) {
        columns.add(ColumnConfig.booleanColumn(column));
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
        try {

            Iterator<CSVRecord> it = createRecordIterator(reader);
            ColumnMap columnMap = createColumnMap(it);

            if (!it.hasNext()) {
                return DataFrame.empty(columnMap.dfHeader);
            }

            ColumnConfig[] unfilteredColumns = ColumnConfig.columnBuilders(columnMap.csvHeader, this.columns);

            CsvLoaderWorker worker = rowSampleSize > 0
                    ? samplingWorker(columnMap, unfilteredColumns)
                    : noSamplingWorker(columnMap, unfilteredColumns);

            return worker.load(it);

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }

    private CsvLoaderWorker noSamplingWorker(ColumnMap columnMap, ColumnConfig[] csvColumns) {
        return rowFilters.isEmpty()
                ? new BaseCsvLoaderWorker(columnMap.dfHeader, columnMap.createColumnBuilders(csvColumns))
                : new FilteringCsvLoaderWorker(columnMap.dfHeader, columnMap.createColumnBuilders(csvColumns), columnMap.createValueHolders(csvColumns), createRowFilter(columnMap.csvHeader));
    }

    private CsvLoaderWorker samplingWorker(ColumnMap columnMap, ColumnConfig[] csvColumns) {
        return rowFilters.isEmpty()
                ? new SamplingCsvLoaderWorker(columnMap.dfHeader, columnMap.createColumnBuilders(csvColumns), rowSampleSize, rowsSampleRandom)
                : new FilteringSamplingCsvLoaderWorker(columnMap.dfHeader, columnMap.createColumnBuilders(csvColumns), columnMap.createValueHolders(csvColumns), createRowFilter(columnMap.csvHeader), rowSampleSize, rowsSampleRandom);
    }

    private Iterator<CSVRecord> createRecordIterator(Reader reader) throws IOException {

        Iterator<CSVRecord> it = format.parse(reader).iterator();
        for (int i = 0; i < skipRows && it.hasNext(); i++) {
            it.next();
        }

        return it;
    }

    private ColumnMap createColumnMap(Iterator<CSVRecord> it) {
        return createColumnMap(createCsvHeader(it));
    }

    private ColumnMap createColumnMap(Index csvHeader) {

        int uw = csvHeader.size();

        if (includeColumns == null && includeColumnPositions == null && dropColumns == null) {
            int[] positions = new int[uw];
            for (int i = 0; i < positions.length; i++) {
                positions[i] = i;
            }

            return new ColumnMap(csvHeader, csvHeader, positions);
        }

        List<String> columns = new ArrayList<>(uw);
        List<Integer> positions = new ArrayList<>(uw);

        if (includeColumns != null) {
            for (String includeColumn : includeColumns) {
                columns.add(includeColumn);
                // this will throw if the label is invalid, which is exactly what we want
                positions.add(csvHeader.position(includeColumn));
            }

        } else if (includeColumnPositions != null) {

            for (int includeColumnPosition : includeColumnPositions) {
                columns.add(csvHeader.getLabel(includeColumnPosition));
                positions.add(includeColumnPosition);
            }

        } else {
            for (int i = 0; i < uw; i++) {
                columns.add(csvHeader.getLabel(i));
                positions.add(i);
            }
        }

        if (dropColumns != null) {
            for (String toDrop : dropColumns) {
                int i = columns.indexOf(toDrop);
                if (i >= 0) {
                    columns.remove(i);
                    positions.remove(i);
                }
            }
        }

        Index dfHeader = Index.forLabels(columns.toArray(new String[0]));

        int[] csvPositions = new int[positions.size()];
        for (int i = 0; i < csvPositions.length; i++) {
            csvPositions[i] = positions.get(i);
        }

        return new ColumnMap(csvHeader, dfHeader, csvPositions);
    }

    private Index createCsvHeader(Iterator<CSVRecord> it) {
        if (it.hasNext()) {
            return header != null ? header : loadCsvHeader(it.next());
        } else {
            return header != null ? header : Index.forLabels();
        }
    }

    private Index loadCsvHeader(CSVRecord header) {

        int width = header.size();
        String[] columnNames = new String[width];
        for (int i = 0; i < width; i++) {
            columnNames[i] = header.get(i);
        }

        return Index.forLabels(columnNames);
    }

    private Predicate<CsvCell<?>[]> createRowFilter(Index columns) {

        if (rowFilters.isEmpty()) {
            return c -> true;
        }

        Predicate<CsvCell<?>[]> p = rowFilters.get(0).toPredicate(columns);

        for (int i = 1; i < rowFilters.size(); i++) {
            p = p.and(rowFilters.get(i).toPredicate(columns));
        }

        return p;
    }

    private static class ColumnMap {

        Index csvHeader;
        Index dfHeader;
        int[] csvPositions;

        ColumnMap(Index csvHeader, Index dfHeader, int[] csvPositions) {
            this.csvHeader = csvHeader;
            this.dfHeader = dfHeader;
            this.csvPositions = csvPositions;
        }

        CsvSeriesAppender<?>[] createColumnBuilders(ColumnConfig[] csvColumns) {

            int w = dfHeader.size();
            CsvSeriesAppender<?>[] builders = new CsvSeriesAppender[w];

            for (int i = 0; i < w; i++) {
                builders[i] = csvColumns[csvPositions[i]].createColumnBuilder(csvPositions[i]);
            }

            return builders;
        }

        CsvCell<?>[] createValueHolders(ColumnConfig[] csvColumns) {

            int w = csvHeader.size();
            CsvCell<?>[] holders = new CsvCell[w];

            for (int i = 0; i < w; i++) {
                holders[i] = csvColumns[i].createValueHolderColumn(i);
            }

            return holders;
        }
    }

}
