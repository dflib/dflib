package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.DataFrame;
import org.dflib.DoubleValueMapper;
import org.dflib.Extractor;
import org.dflib.FloatValueMapper;
import org.dflib.Index;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.RowPredicate;
import org.dflib.ValueMapper;
import org.dflib.builder.DataFrameAppender;
import org.dflib.builder.DataFrameByRowBuilder;
import org.dflib.codec.Codec;
import org.dflib.collection.Iterators;
import org.dflib.sample.Sampler;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * A configurable loader of CSV files.
 */
public class CsvLoader {

    private CsvHeaderFactory headerFactory;
    private CsvSchemaFactory schemaFactory;
    private final List<ColConfigurator> colConfigurators;

    private CSVFormat format;
    private Charset encoding;

    private RowPredicate rowCondition;
    private int rowSampleSize;
    private Random rowsSampleRandom;
    private int offset;
    private int limit = -1;

    private Codec compressionCodec;
    private boolean checkByteOrderMark;

    public CsvLoader() {
        this.format = CSVFormat.DEFAULT;
        this.colConfigurators = new ArrayList<>();
    }

    /**
     * Sets a compression codec for this saver. If not set, the saver will try to determine compression preferences
     * using the target file extension. So this method is especially useful if the target is not a file.
     *
     * @since 2.0.0
     */
    public CsvLoader compression(Codec codec) {
        this.compressionCodec = codec;
        return this;
    }

    /**
     * Checks the source leading bytes for "byte order mark" (BOM) placed there by certain CSV generators, and, if
     * present, uses it to determine content encoding. If the encoding is set explicitly by the user, the BOM is
     * stripped from the stream and is otherwise ignored. Without this setting, the BOM bytes will be treated as content
     * and usually prepended to the name of the first column in the resulting DataFrame. This is highly confusing, since
     * those symbols are invisible.
     *
     * <p>This setting is "safe" in a sense that it works with files with or without a BOM. It is not a default in
     * {@link CsvLoader} though, as it creates some minor overhead on load.
     */
    public CsvLoader checkByteOrderMark() {
        this.checkByteOrderMark = true;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public CsvLoader encoding(String encoding) {
        return encoding(encoding != null ? Charset.forName(encoding) : null);
    }

    /**
     * Allow to change the encoding from the platoform default. Ignored if the DataFrame is loaded from a Reader
     * (that performs character decoding on its own).
     *
     * @since 1.1.0
     */
    public CsvLoader encoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * Skips the specified number of rows.
     */
    public CsvLoader offset(int len) {
        this.offset = len;
        return this;
    }

    /**
     * Limits the max number of rows to the provided value
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
        this.rowSampleSize = size;
        this.rowsSampleRandom = Objects.requireNonNull(random);
        return this;
    }

    /**
     * Configures the CSV loader to only include rows that are matching the provided condition. Applying the condition
     * during load would allow extracting only relevant data from very large CSVs using constant memory. Condition is
     * applied to an already converted row, with column names and positions matching the resulting DataFrame, not the
     * CSV columns.
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
     */
    public CsvLoader header(String... columns) {
        this.headerFactory = CsvHeaderFactory.explicit(Index.of(columns));
        return this;
    }

    /**
     * Instead of using the first CSV row as a DataFrame header, generate a header with labels like "c0", "c1", etc.
     */
    public CsvLoader generateHeader() {
        this.headerFactory = CsvHeaderFactory.generated();
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
        this.schemaFactory = CsvSchemaFactory.ofCols(columns);
        return this;
    }

    /**
     * Configures the loader to only process the specified columns.
     *
     * @return this loader instance
     */
    public CsvLoader cols(int... columns) {
        this.schemaFactory = CsvSchemaFactory.ofCols(columns);
        return this;
    }

    /**
     * Configures the loader to only process the columns that do not match the specified names. The remaining CSV columns
     * will be loaded in the order they are present in the CSV.
     *
     * @return this loader instance
     */
    public CsvLoader colsExcept(String... columns) {
        this.schemaFactory = CsvSchemaFactory.ofColsExcept(columns);
        return this;
    }

    /**
     * Configures the loader to only process the columns that do not match the specified positions. The remaining CSV
     * columns will be loaded in the order they are present in the CSV.
     *
     * @return this loader instance
     */
    public CsvLoader colsExcept(int... columns) {
        this.schemaFactory = CsvSchemaFactory.ofColsExcept(columns);
        return this;
    }

    /**
     * Provides a conversion function for a CSV column at a given position to produce a desired type.
     */
    public CsvLoader col(int column, ValueMapper<String, ?> mapper) {
        colConfigurators.add(ColConfigurator.objectCol(column, mapper, false));
        return this;
    }

    /**
     * Provides a conversion function of a CSV column at a given position to produce a desired type.
     */
    public CsvLoader col(String column, ValueMapper<String, ?> mapper) {
        colConfigurators.add(ColConfigurator.objectCol(column, mapper, false));
        return this;
    }

    /**
     * Configures a CSV column to be loaded without conversion (as a String), but with value compaction. Should be used
     * to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(int column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded without conversion (as a String), but with value compaction. Should be used
     * to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(String column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded with the specified conversion and with converted value compaction. Should
     * be used instead of {@link #col(int, ValueMapper)} to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(int column, ValueMapper<String, ?> mapper) {
        colConfigurators.add(ColConfigurator.objectCol(column, mapper, true));
        return this;
    }

    /**
     * Configures a CSV column to be loaded with the specified conversion and with converted value compaction. Should
     * be used instead of {@link #col(String, ValueMapper)} to save memory for low-cardinality columns.
     */
    public CsvLoader compactCol(String column, ValueMapper<String, ?> mapper) {
        colConfigurators.add(ColConfigurator.objectCol(column, mapper, true));
        return this;
    }

    public CsvLoader intCol(int column) {
        colConfigurators.add(ColConfigurator.intCol(column, IntValueMapper.ofStr()));
        return this;
    }

    public CsvLoader intCol(String column) {
        colConfigurators.add(ColConfigurator.intCol(column, IntValueMapper.ofStr()));
        return this;
    }

    public CsvLoader intCol(int column, int forNull) {
        colConfigurators.add(ColConfigurator.intCol(column, IntValueMapper.ofStr(forNull)));
        return this;
    }

    public CsvLoader intCol(String column, int forNull) {
        colConfigurators.add(ColConfigurator.intCol(column, IntValueMapper.ofStr(forNull)));
        return this;
    }

    public CsvLoader longCol(int column) {
        colConfigurators.add(ColConfigurator.longCol(column, LongValueMapper.ofStr()));
        return this;
    }

    public CsvLoader longCol(String column) {
        colConfigurators.add(ColConfigurator.longCol(column, LongValueMapper.ofStr()));
        return this;
    }

    public CsvLoader longCol(int column, long forNull) {
        colConfigurators.add(ColConfigurator.longCol(column, LongValueMapper.ofStr(forNull)));
        return this;
    }

    public CsvLoader longCol(String column, long forNull) {
        colConfigurators.add(ColConfigurator.longCol(column, LongValueMapper.ofStr(forNull)));
        return this;
    }

    /**
     * @since 1.1.0
     */
    public CsvLoader floatCol(int column) {
        colConfigurators.add(ColConfigurator.floatCol(column, FloatValueMapper.ofStr()));
        return this;
    }

    /**
     * @since 1.1.0
     */
    public CsvLoader floatCol(String column) {
        colConfigurators.add(ColConfigurator.floatCol(column, FloatValueMapper.ofStr()));
        return this;
    }

    public CsvLoader doubleCol(int column) {
        colConfigurators.add(ColConfigurator.doubleCol(column, DoubleValueMapper.ofStr()));
        return this;
    }

    public CsvLoader doubleCol(String column) {
        colConfigurators.add(ColConfigurator.doubleCol(column, DoubleValueMapper.ofStr()));
        return this;
    }

    public CsvLoader doubleCol(int column, double forNull) {
        colConfigurators.add(ColConfigurator.doubleCol(column, DoubleValueMapper.ofStr(forNull)));
        return this;
    }

    public CsvLoader doubleCol(String column, double forNull) {
        colConfigurators.add(ColConfigurator.doubleCol(column, DoubleValueMapper.ofStr(forNull)));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to BigDecimals.
     */
    public CsvLoader decimalCol(int column) {
        colConfigurators.add(ColConfigurator.objectCol(column, ValueMapper.stringToBigDecimal(), false));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to BigDecimals.
     */
    public CsvLoader decimalCol(String column) {
        colConfigurators.add(ColConfigurator.objectCol(column, ValueMapper.stringToBigDecimal(), false));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to primitive booleans.
     */
    public CsvLoader boolCol(int column) {
        colConfigurators.add(ColConfigurator.boolCol(column));
        return this;
    }

    /**
     * Will convert values of a CSV column at a given position to primitive booleans.
     */
    public CsvLoader boolCol(String column) {
        colConfigurators.add(ColConfigurator.boolCol(column));
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
     */
    public CsvLoader format(CSVFormat format) {
        this.format = format != null ? format : CSVFormat.DEFAULT;
        return this;
    }

    /**
     * Convert missing values to nulls instead of empty strings. Equivalent to calling <code>nullString("")</code>
     *
     * @see #nullString(String)
     */
    public CsvLoader emptyStringIsNull() {
        return nullString("");
    }

    /**
     * Configures the loader to treat the <code>nullString</code> value as a null in tbe resulting DataFrame
     *
     * @see #emptyStringIsNull()
     * @since 1.2.0
     */
    public CsvLoader nullString(String nullString) {
        this.format = format.builder().setNullString(Objects.requireNonNull(nullString)).build();
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

    /**
     * @since 1.1.0
     */
    public DataFrame load(ByteSource src) {

        Codec codec = this.compressionCodec != null
                ? this.compressionCodec
                : Codec.ofUri(src.uri().orElse("")).orElse(null);

        ByteSource plainSrc = codec != null ? src.decompress(codec) : src;

        try (Reader in = createReader(plainSrc)) {
            return load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading source: " + plainSrc.uri().orElse("?"), e);
        }
    }

    /**
     * @since 1.1.0
     */
    public Map<String, DataFrame> loadAll(ByteSources srcs) {
        return srcs.process((name, src) -> load(src));
    }

    public DataFrame load(Reader reader) {

        Iterator<CSVRecord> it0 = read(reader);

        // "offset" is applied even if we read the header from the iterator
        Iterator<CSVRecord> it1 = offset > 0 ? Iterators.skip(it0, offset) : it0;
        CsvHeader csvHeader = createCsvHeader(it1);
        CsvSchema schema = createSchema(csvHeader.getHeader());

        // Some header strategies may peek inside the iterator, but not use the first row for the header.
        // So we need to re-add this row back to the DataFrame
        CSVRecord maybeUnconsumedDataRow = csvHeader.getMaybeUnconsumedDataRow();

        // The header does not count towards the limit, so apply the limit AFTER reading the header
        int limit = this.limit;
        if (limit == 0 || (maybeUnconsumedDataRow == null && !it1.hasNext())) {
            return DataFrame.empty(schema.getDfHeader());
        }

        Extractor<CSVRecord, ?>[] extractors = extractors(schema);
        DataFrameByRowBuilder<CSVRecord, ?> builder = DataFrame
                .byRow(extractors)
                .columnIndex(schema.getDfHeader());

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

    private Reader createReader(ByteSource src) throws IOException {
        return checkByteOrderMark ? BOM.reader(src, encoding) : createNonBomReader(src);
    }

    private Reader createNonBomReader(ByteSource src) {
        Charset encoding = this.encoding != null ? this.encoding : Charset.defaultCharset();
        return new InputStreamReader(src.stream(), encoding);
    }

    private Iterator<CSVRecord> read(Reader reader) {
        try {
            return format.parse(reader).iterator();
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }

    private CsvSchema createSchema(Index csvHeader) {
        return schemaFactory != null
                ? schemaFactory.schema(csvHeader)
                : CsvSchemaFactory.all().schema(csvHeader);
    }

    private CsvHeader createCsvHeader(Iterator<CSVRecord> it) {
        return headerFactory != null
                ? headerFactory.header(it)
                : CsvHeaderFactory.firstRow().header(it);
    }

    private Extractor<CSVRecord, ?>[] extractors(CsvSchema schema) {

        Index csvHeader = schema.getCsvHeader();
        int[] csvPositions = schema.getCsvPositions();

        int w = schema.getDfHeader().size();
        Extractor<CSVRecord, ?>[] extractors = new Extractor[w];

        Map<Integer, ColConfigurator> configurators = new HashMap<>();
        for (ColConfigurator c : colConfigurators) {
            int csvPos = c.srcColPos >= 0 ? c.srcColPos : csvHeader.position(c.srcColName);

            // later configs override earlier configs at the same position
            configurators.put(csvPos, c);
        }

        for (int i = 0; i < w; i++) {
            int csvPos = csvPositions[i];
            ColConfigurator cc = configurators.computeIfAbsent(csvPos, ii -> ColConfigurator.objectCol(ii, false));
            extractors[i] = cc.extractor(csvHeader);
        }

        return extractors;
    }


}
