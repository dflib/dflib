package org.dflib.csv.parser;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.builder.DataFrameAppender;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvColumnsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.List;

/**
 * CSV parser main entry point, orchestrates the whole parsing process.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public class CsvParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvParser.class);

    /**
     * Format of the CSV provided by the user
     */
    final CsvParserConfig config;
    ParserContext context;
    ParserRuleFlow ruleFlow;
    DataFrameBuilder dfBuilder;
    CsvColumnsBuilder columnsBuilder;

    /**
     * Creates a new parser with the default format.
     */
    public CsvParser() {
        this(CsvParserConfig.builder().build());
    }

    /**
     * Creates a new parser with the specified format.
     *
     * @param config CSV format to use
     */
    public CsvParser(CsvParserConfig config) {
        this.config = config;
    }

    public DataFrame parse(Path filePath) {
        return parse(ByteSource.ofPath(filePath));
    }

    public DataFrame parse(File file) {
        return parse(ByteSource.ofFile(file));
    }

    public DataFrame parse(String filePath) {
        return parse(ByteSource.ofFile(filePath));
    }

    public DataFrame parse(ByteSource src) {
        Codec codec = config.compressionCodec() != null
                ? config.compressionCodec()
                : Codec.ofUri(src.uri().orElse("")).orElse(null);

        ByteSource plainSrc = codec != null ? src.decompress(codec) : src;

        try (Reader in = createReader(plainSrc)) {
            return parse(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading source: " + plainSrc.uri().orElse("?"), e);
        }
    }

    private Reader createReader(ByteSource src) throws IOException {
        return config.checkByteOrderMark()
                ? BOM.reader(src, config.encoding())
                : createNonBomReader(src);
    }

    private Reader createNonBomReader(ByteSource src) {
        return new InputStreamReader(src.stream(), config.encoding());
    }

    /**
     * Main parse method, runs parsing and builds DataFrame.
     * @param reader provider by the caller.
     * @return DataFrame built on the CSV content.
     */
    public DataFrame parse(Reader reader) {
        // Effective columns are results of merging all data provided by the user plus detected from the CSV file
        this.columnsBuilder = new CsvColumnsBuilder();
        this.context = new ParserContext();
        this.ruleFlow = new ParserRuleFlow(config, context);
        this.dfBuilder = new DataFrameBuilder(config);
        this.context.setCallback(new ColumnDetectionCallback(buildListener(), config.csvFormat().trailingDelimiter()));
        new CsvScanner(context, ruleFlow).scan(reader);
        checkEmptyAppender();
        return dfBuilder.buildDataFrame();
    }

    /**
     * Builds column detection listener.
     * @return listener based on the format settings.
     */
    ColumnDetectionListener buildListener() {
        // generate or validate header values
        ColumnDetectionListener listener = config.autoColumns()
                ? new ColumnGeneratorListener(this.config, this.columnsBuilder)
                : new ColumnValidatorListener(this.config, this.columnsBuilder);

        // build columns merging user data with the actual content
        listener = listener.andThen(this::buildColumns);

        // append the first row, if needed
        if(!config.excludeHeaderValues()) {
            // use the actual callback to consume the row
            listener = listener.andThen(slices -> context.callback().onNewRow(slices));
        }
        return listener;
    }

    /**
     * Merges all provided and detected info about columns and provides it down the line.
     * @param detected columns detected by the AUTO stage.
     */
    private void buildColumns(DataSlice[] detected) {
        int csvWidth = detected.length;
        for(int i = csvWidth; i < columnsBuilder.size(); i++) {
            // ignore columns that are not in the actual file
            CsvColumnMapping.Builder builder = columnsBuilder.get(i);
            if(!builder.isSkipped()) {
                if(config.csvFormat().allowEmptyColumns()) {
                    LOGGER.warn("More columns defined in the provided format, " +
                            "that are detected in the file. Column {} will be empty in the result.", i);
                    builder.skip();
                } else {
                    throw new IllegalStateException("More columns defined in the provided format, " +
                            "that are detected in the file. Use CsvLoader.nullPadRows() if this is intended.");
                }
            }
        }
        List<CsvColumnMapping> fullWidthColumns = columnsBuilder.build(config);
        List<CsvColumnMapping> trimmedToWidthColumns = fullWidthColumns.subList(0, csvWidth);
        this.ruleFlow.initColumns(trimmedToWidthColumns);
        this.context.initRowBuffer(csvWidth, config.csvFormat().allowEmptyColumns());
        initAppender(fullWidthColumns);
    }

    /**
     * Build DataFrameAppender based on the columns.
     * @param columns merged columns data.
     */
    void initAppender(List<CsvColumnMapping> columns) {
        DataFrameAppender<DataSlice[]> appender = dfBuilder.buildAppender(columns);
        int limit = config.limit() + (config.excludeHeaderValues() ? 1 : 0);
        this.context.setCallback(config.limit() >= 0
                ? new LimitCallback(this.context, appender, limit)
                : new NoLimitCallback(appender)
        );
    }

    /**
     * In case there is no content in the file, appender would never be initialized, so create at least something.
     */
    void checkEmptyAppender() {
        // appender could be uninitialized if the reader was empty
        if (dfBuilder.appender == null) {
            // try and use columns that were provided by the format
            columnsBuilder.merge(config.columnMappings());
            List<CsvColumnMapping> columns = columnsBuilder.build(config);
            if(config.autoColumns()) {
                LOGGER.warn("CSV data is empty, and auto columns is on. Header would be incorrect.");
            }
            initAppender(columns);
        }
    }
}
