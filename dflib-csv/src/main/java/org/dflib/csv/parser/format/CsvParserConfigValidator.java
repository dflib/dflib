package org.dflib.csv.parser.format;

import java.util.List;

/**
 * Validates CsvFormat configuration for internal consistency.
 */
class CsvParserConfigValidator {
    private final CsvParserConfig.Builder config;
    private final List<CsvColumnMapping.Builder> columnBuilders;

    CsvParserConfigValidator(CsvParserConfig.Builder builder) {
        this.config = builder;
        this.columnBuilders = builder.columnBuilders;
    }

    /**
     * Performs all validation checks.
     *
     * @throws IllegalArgumentException if any validation fails
     */
    void validate() {
        validateLimitAndOffset();
        validateColumns();
    }

    private void validateLimitAndOffset() {
        if (config.limit < -1) {
            throw new IllegalArgumentException("Limit must be non-negative or -1 (no limit)");
        }
        if (config.offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
    }

    private void validateColumns() {
        if (config.explicitAutoColumns
                && !config.autoColumns
                && columnBuilders.isEmpty()) {
            throw new IllegalArgumentException("No columns specified. Please specify at least one column or use autoColumns(true)");
        }
    }
}
