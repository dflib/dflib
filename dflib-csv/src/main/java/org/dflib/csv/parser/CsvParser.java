package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.builder.DataFrameAppender;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvColumnFormat;
import org.dflib.csv.parser.format.CsvColumnsBuilder;
import org.dflib.csv.parser.format.CsvFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
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
    final CsvFormat format;
    ParserContext context;
    ParserRuleFlow ruleFlow;
    DataFrameBuilder dfBuilder;
    CsvColumnsBuilder columnsBuilder;

    /**
     * Creates a new parser with the default format.
     *
     * @see CsvFormat#defaultFormat()
     */
    public CsvParser() {
        this(CsvFormat.defaultFormat());
    }

    /**
     * Creates a new parser with the specified format.
     *
     * @param format CSV format to use
     */
    public CsvParser(CsvFormat format) {
        this.format = format;
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
        this.ruleFlow = new ParserRuleFlow(format, context);
        this.dfBuilder = new DataFrameBuilder(format);
        this.context.setCallback(new ColumnDetectionCallback(buildListener(), format.trailingDelimiter()));
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
        ColumnDetectionListener listener = format.autoColumns()
                ? new ColumnGeneratorListener(this.format, this.columnsBuilder)
                : new ColumnValidatorListener(this.format, this.columnsBuilder);

        // build columns merging user data with the actual content
        listener = listener.andThen(this::buildColumns);

        // append the first row, if needed
        if(!format.excludeHeaderValues()) {
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
            CsvColumnFormat.Builder builder = columnsBuilder.get(i);
            if(!builder.isSkipped()) {
                if(format.allowEmptyColumns()) {
                    LOGGER.warn("More columns defined in the provided format, " +
                            "that are detected in the file. Column {} will be empty in the result.", i);
                    builder.skip();
                } else {
                    throw new IllegalStateException("More columns defined in the provided format, " +
                            "that are detected in the file. Use CsvLoader.nullPadRows() if this is intended.");
                }
            }
        }
        List<CsvColumnFormat> fullWidthColumns = columnsBuilder.build(format);
        List<CsvColumnFormat> trimmedToWidthColumns = fullWidthColumns.subList(0, csvWidth);
        this.ruleFlow.initColumns(trimmedToWidthColumns);
        this.context.initRowBuffer(csvWidth, format.allowEmptyColumns());
        initAppender(fullWidthColumns);
    }

    /**
     * Build DataFrameAppender based on the columns.
     * @param columns merged columns data.
     */
    void initAppender(List<CsvColumnFormat> columns) {
        DataFrameAppender<DataSlice[]> appender = dfBuilder.buildAppender(columns);
        this.context.setCallback(format.limit() >= 0
                ? new LimitCallback(this.context, appender, format.limit() + (format.excludeHeaderValues() ? 1 : 0))
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
            for(CsvColumnFormat.Builder builder : format.columnBuilders()) {
                columnsBuilder.merge(builder);
            }
            List<CsvColumnFormat> columns = columnsBuilder.build(format);
            if(format.autoColumns()) {
                LOGGER.warn("CSV data is empty, and auto columns is on. Header would be incorrect.");
            }
            initAppender(columns);
        }
    }
}
