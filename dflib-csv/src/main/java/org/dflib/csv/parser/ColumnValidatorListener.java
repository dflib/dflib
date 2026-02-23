package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvColumnsBuilder;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener that validates columns provided by the user against actual file content.
 *
 * @param config CSV file format defined by the loader.
 * @param columnsBuilder columns info gathered so far.
 */
record ColumnValidatorListener(CsvParserConfig config, CsvColumnsBuilder columnsBuilder) implements ColumnDetectionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnValidatorListener.class);

    @Override
    public void columnsDetected(DataSlice[] data) {
        columnsBuilder.merge(config.columnMappings());

        // align user-provided data with the actual CSV content
        int userWidth = columnsBuilder.size();
        int csvWidth = data.length;

        if (csvWidth == userWidth) {
            // just use the user data
            return;
        }

        // user set fewer columns that there are in the actual file
        if (csvWidth > userWidth) {
            for (int i = userWidth; i < csvWidth; i++) {
                // fill the missing columns with the fillers
                columnsBuilder.merge(CsvColumnMapping.column(i).skip());
            }
            LOGGER.warn("CSV row width {} exceeds configured columns {}; extra columns will be skipped",
                    csvWidth, userWidth);
        }
    }
}
