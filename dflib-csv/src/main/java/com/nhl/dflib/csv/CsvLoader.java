package com.nhl.dflib.csv;

import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.ValuePredicate;
import com.nhl.dflib.sample.Sampler;
import com.nhl.dflib.series.builder.BooleanMappedAccumulator;
import com.nhl.dflib.series.builder.DoubleMappedAccumulator;
import com.nhl.dflib.series.builder.IntMappedAccumulator;
import com.nhl.dflib.series.builder.LongMappedAccumulator;
import com.nhl.dflib.series.builder.ObjectAccumulator;
import com.nhl.dflib.series.builder.ObjectMappedAccumulator;
import com.nhl.dflib.series.builder.SeriesBuilder;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
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

    // storing converters as list to ensure predictable resolution order when the user supplies overlapping converters
    private List<AccumPair> builders;
    private List<RowFilterPair> rowFilters;

    public CsvLoader() {
        this.format = CSVFormat.DEFAULT;
        this.builders = new ArrayList<>();
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
     * during load would allow to extract relevant data from very large CSVs.
     *
     * @param columnName the name of the column the condition applies to
     * @param condition  column value condition that needs to be fulfilled for the row to be included in the resulting DataFrame.
     * @return this loader instance
     * @since 0.8
     */
    public <V> CsvLoader filterRows(String columnName, ValuePredicate<V> condition) {
        rowFilters.add(new RowFilterPair<>(i -> i.position(columnName), condition));
        return this;
    }

    /**
     * Configures CSV loader to only include rows that are matching the provided criterion. Applying the condition
     * during load would allow to extract relevant data from very large CSVs.
     *
     * @param columnPos position of the column the condition applies to
     * @param condition column value condition that needs to be fulfilled for the row to be included in the resulting DataFrame.
     * @return this loader instance
     * @since 0.8
     */
    public <V> CsvLoader filterRows(int columnPos, ValuePredicate<V> condition) {
        rowFilters.add(new RowFilterPair<>(i -> columnPos, condition));
        return this;
    }

    /**
     * @deprecated since 0.7 in favor of {@link #header(String...)}
     */
    @Deprecated
    public CsvLoader columns(String... columns) {
        return header(columns);
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

    public CsvLoader columnType(int column, ValueMapper<String, ?> typeConverter) {
        return columnType(column, new ObjectMappedAccumulator<>(typeConverter));
    }

    public CsvLoader columnType(String column, ValueMapper<String, ?> typeConverter) {
        return columnType(column, new ObjectMappedAccumulator<>(typeConverter));
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(int column) {
        return columnType(column, new IntMappedAccumulator<>(IntValueMapper.fromString()));
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(String column) {
        return columnType(column, new IntMappedAccumulator<>(IntValueMapper.fromString()));
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(int column, int forNull) {
        return columnType(column, new IntMappedAccumulator<>(IntValueMapper.fromString(forNull)));
    }

    /**
     * @since 0.6
     */
    public CsvLoader intColumn(String column, int forNull) {
        return columnType(column, new IntMappedAccumulator<>(IntValueMapper.fromString(forNull)));
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(int column) {
        return columnType(column, new LongMappedAccumulator<>(LongValueMapper.fromString()));
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(String column) {
        return columnType(column, new LongMappedAccumulator(LongValueMapper.fromString()));
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(int column, long forNull) {
        return columnType(column, new LongMappedAccumulator(LongValueMapper.fromString(forNull)));
    }

    /**
     * @since 0.6
     */
    public CsvLoader longColumn(String column, long forNull) {
        return columnType(column, new LongMappedAccumulator(LongValueMapper.fromString(forNull)));
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(int column) {
        return columnType(column, new DoubleMappedAccumulator(DoubleValueMapper.fromString()));
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(String column) {
        return columnType(column, new DoubleMappedAccumulator(DoubleValueMapper.fromString()));
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(int column, double forNull) {
        return columnType(column, new DoubleMappedAccumulator(DoubleValueMapper.fromString(forNull)));
    }

    /**
     * @since 0.6
     */
    public CsvLoader doubleColumn(String column, double forNull) {
        return columnType(column, new DoubleMappedAccumulator(DoubleValueMapper.fromString(forNull)));
    }

    /**
     * @since 0.6
     */
    public CsvLoader booleanColumn(int column) {
        return columnType(column, new BooleanMappedAccumulator<>(BooleanValueMapper.fromString()));
    }

    /**
     * @since 0.6
     */
    public CsvLoader booleanColumn(String column) {
        return columnType(column, new BooleanMappedAccumulator<>(BooleanValueMapper.fromString()));
    }

    /**
     * Instructs the loader to convert values in the specified column to numbers of the specified type. This method will
     * result in "object" columns (and hence can store nulls). If you want a column with primitive numbers, use methods
     * like {@link #intColumn(int)}, etc. instead.
     *
     * @since 0.6
     */
    public CsvLoader numColumn(int column, Class<? extends Number> type) {
        return columnType(column, numBuilder(type));
    }

    /**
     * @since 0.6
     */
    public CsvLoader numColumn(String column, Class<? extends Number> type) {
        return columnType(column, numBuilder(type));
    }

    private CsvLoader columnType(int column, SeriesBuilder<String, ?> columnBuilder) {
        builders.add(new AccumPair(i -> column, columnBuilder));
        return this;
    }

    private CsvLoader columnType(String column, SeriesBuilder<String, ?> columnBuilder) {
        builders.add(new AccumPair(i -> i.position(column), columnBuilder));
        return this;
    }

    private SeriesBuilder<String, ?> numBuilder(Class<? extends Number> type) {

        if (Integer.class.equals(type)) {
            return new ObjectMappedAccumulator<>(ValueMapper.stringToInt());
        }

        if (Long.class.equals(type)) {
            return new ObjectMappedAccumulator<>(ValueMapper.stringToLong());
        }

        if (Double.class.equals(type)) {
            return new ObjectMappedAccumulator<>(ValueMapper.stringToDouble());
        }

        if (Float.class.equals(type)) {
            return new ObjectMappedAccumulator<>(ValueMapper.stringToFloat());
        }

        if (BigDecimal.class.equals(type)) {
            return new ObjectMappedAccumulator<>(ValueMapper.stringToBigDecimal());
        }

        if (BigInteger.class.equals(type)) {
            return new ObjectMappedAccumulator<>(ValueMapper.stringToBigInteger());
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
            Iterator<CSVRecord> it = format.parse(reader).iterator();

            rewind(it);
            Index unfilteredHeader = unfilteredHeader(it);
            ColumnFilterPair pair = filterHeader(unfilteredHeader);

            if (!it.hasNext()) {
                return DataFrame.newFrame(pair.header).empty();
            }

            CsvLoaderWorker worker = rowSampleSize > 0
                    ? samplingWorker(pair, unfilteredHeader)
                    : noSamplingWorker(pair, unfilteredHeader);

            return worker.load(it);

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }

    private CsvLoaderWorker noSamplingWorker(ColumnFilterPair pair, Index unfilteredHeader) {
        return rowFilters.isEmpty()
                ? new BaseCsvLoaderWorker(pair.header, pair.csvPositions, createAccumulators(pair.header))
                : new FilteringCsvLoaderWorker(pair.header, pair.csvPositions, createAccumulators(pair.header), createRowFilter(unfilteredHeader));
    }

    private CsvLoaderWorker samplingWorker(ColumnFilterPair pair, Index unfilteredHeader) {
        return rowFilters.isEmpty()
                ?  new SamplingCsvLoaderWorker(pair.header, pair.csvPositions, createAccumulators(pair.header), rowSampleSize, rowsSampleRandom)
                :  new FilteringSamplingCsvLoaderWorker(pair.header, pair.csvPositions, createAccumulators(pair.header), createAccumulators(pair.header), createRowFilter(unfilteredHeader), rowSampleSize, rowsSampleRandom);
    }

    private void rewind(Iterator<CSVRecord> it) {
        for (int i = 0; i < skipRows && it.hasNext(); i++) {
            it.next();
        }
    }

    private Index unfilteredHeader(Iterator<CSVRecord> it) {
        if (it.hasNext()) {
            return header != null ? header : loadHeader(it.next());
        } else {
            return header != null ? header : Index.forLabels();
        }
    }

    private ColumnFilterPair filterHeader(Index unfilteredHeader) {

        int uw = unfilteredHeader.size();

        if (includeColumns == null && includeColumnPositions == null && dropColumns == null) {
            int[] positions = new int[uw];
            for (int i = 0; i < positions.length; i++) {
                positions[i] = i;
            }

            return new ColumnFilterPair(unfilteredHeader, positions);
        }

        List<String> columns = new ArrayList<>(uw);
        List<Integer> positions = new ArrayList<>(uw);

        if (includeColumns != null) {
            for (int i = 0; i < includeColumns.length; i++) {
                columns.add(includeColumns[i]);
                // this will throw if the label is invalid, which is exactly what we want
                positions.add(unfilteredHeader.position(includeColumns[i]));
            }

        } else if (includeColumnPositions != null) {

            for (int i = 0; i < includeColumnPositions.length; i++) {
                columns.add(unfilteredHeader.getLabel(includeColumnPositions[i]));
                positions.add(includeColumnPositions[i]);
            }

        } else {
            for (int i = 0; i < uw; i++) {
                columns.add(unfilteredHeader.getLabel(i));
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

        Index header = Index.forLabels(columns.toArray(new String[0]));

        int[] positionsArray = new int[positions.size()];
        for (int i = 0; i < positionsArray.length; i++) {
            positionsArray[i] = positions.get(i);
        }

        return new ColumnFilterPair(header, positionsArray);
    }

    private Index loadHeader(CSVRecord header) {

        int width = header.size();
        String[] columnNames = new String[width];
        for (int i = 0; i < width; i++) {
            columnNames[i] = header.get(i);
        }

        return Index.forLabels(columnNames);
    }

    private SeriesBuilder<String, ?>[] createAccumulators(Index columns) {

        int w = columns.size();
        SeriesBuilder<String, ?>[] builders = new SeriesBuilder[w];

        // there may be overlapping pairs... the last one wins
        for (AccumPair p : this.builders) {
            builders[p.positionResolver.apply(columns)] = p.builder;
        }

        // fill missing builders with no-transform builders
        for (int i = 0; i < w; i++) {
            if (builders[i] == null) {
                builders[i] = new ObjectAccumulator<>();
            }
        }

        return builders;
    }

    private Predicate<SeriesBuilder<String, ?>[]> createRowFilter(Index columns) {

        if (rowFilters.isEmpty()) {
            return null;
        }

        Predicate<SeriesBuilder<String, ?>[]> p = rowFilters.get(0).toPredicate(columns);

        for (int i = 1; i < rowFilters.size(); i++) {
            p = p.and(rowFilters.get(i).toPredicate(columns));
        }

        return p;
    }

    private class AccumPair {
        Function<Index, Integer> positionResolver;
        SeriesBuilder<String, ?> builder;

        AccumPair(Function<Index, Integer> positionResolver, SeriesBuilder<String, ?> builder) {
            this.positionResolver = positionResolver;
            this.builder = builder;
        }
    }

    private class ColumnFilterPair {
        Index header;
        int[] csvPositions;

        ColumnFilterPair(Index header, int[] csvPositions) {
            this.header = header;
            this.csvPositions = csvPositions;
        }
    }

    private class RowFilterPair<V> {
        Function<Index, Integer> positionResolver;
        ValuePredicate<V> condition;

        RowFilterPair(Function<Index, Integer> positionResolver, ValuePredicate<V> condition) {
            this.positionResolver = positionResolver;
            this.condition = condition;
        }

        Predicate<SeriesBuilder<String, ?>[]> toPredicate(Index columns) {

            int pos = positionResolver.apply(columns);
            return builders -> condition.test((V) builders[pos].peek());
        }
    }
}
