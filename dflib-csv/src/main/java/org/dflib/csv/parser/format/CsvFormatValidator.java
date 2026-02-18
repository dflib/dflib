package org.dflib.csv.parser.format;

import java.util.List;

/**
 * Validates CsvFormat configuration for internal consistency.
 */
class CsvFormatValidator {
    private final CsvFormat.Builder format;
    private final boolean explicitAutoColumns;
    private final List<CsvColumnFormat.Builder> columnBuilders;
    
    CsvFormatValidator(CsvFormat.Builder builder) {
        this.format = builder;
        this.explicitAutoColumns = builder.explicitAutoColumns;
        this.columnBuilders = builder.columnBuilders;
    }

    /**
     * Performs all validation checks.
     * @throws IllegalArgumentException if any validation fails
     */
    void validate() {
        validateDelimiter();
        validateLineBreak();
        validateComment();
        validateQuoteAndEscape();
        validateLimitAndOffset();
        validateColumns();
    }
    
    private void validateDelimiter() {
        String delimiter = format.delimiter.delimiter;
        if (delimiter == null || delimiter.isEmpty()) {
            throw new IllegalArgumentException("Delimiter must not be empty");
        }
        if (delimiter.indexOf('\r') != -1 || delimiter.indexOf('\n') != -1) {
            throw new IllegalArgumentException("Delimiter must not contain line break characters");
        }
    }
    
    private void validateLineBreak() {
        if (format.lineBreak == null) {
            throw new IllegalArgumentException("Line break must be specified");
        }
        String delimiter = format.delimiter.delimiter;
        switch (format.lineBreak) {
            case AUTO -> {
                // no-op: the generic check above already rejects delimiters containing \r or \n
            }
            case CR -> {
                if ("\r".equals(delimiter)) {
                    throw new IllegalArgumentException("Delimiter and line break must differ");
                }
            }
            case LF -> {
                if ("\n".equals(delimiter)) {
                    throw new IllegalArgumentException("Delimiter and line break must differ");
                }
            }
            case CRLF -> {
                if ("\r\n".equals(delimiter)) {
                    throw new IllegalArgumentException("Delimiter and line break must differ");
                }
            }
        }
    }
    
    private void validateComment() {
        String delimiter = format.delimiter.delimiter;
        if (format.comment != null) {
            if (format.comment.isEmpty()) {
                throw new IllegalArgumentException("Comment prefix must not be empty");
            }
            if (format.comment.indexOf('\r') != -1 || format.comment.indexOf('\n') != -1) {
                throw new IllegalArgumentException("Comment prefix must not contain line break characters");
            }
            if (format.comment.equals(delimiter)) {
                throw new IllegalArgumentException("Comment prefix and delimiter must differ");
            }
        }
    }
    
    private void validateQuoteAndEscape() {
        if (format.quote == null) {
            throw new IllegalArgumentException("Quote must be specified");
        }
        char quoteChar = format.quote.quoteChar();
        if (quoteChar == '\r' || quoteChar == '\n') {
            throw new IllegalArgumentException("Quote character must not be a line break");
        }
        String delimiter = format.delimiter.delimiter;
        if (delimiter.length() == 1 && delimiter.charAt(0) == quoteChar) {
            throw new IllegalArgumentException("Quote character and delimiter must differ");
        }
        if (format.escape == null) {
            throw new IllegalArgumentException("Escape must be specified");
        }
        if (format.escape == Escape.CUSTOM) {
            if (format.escapeChar == 0) {
                throw new IllegalArgumentException("Custom escape must be specified");
            }
            char escapeChar = format.escapeChar;
            if (escapeChar == '\r' || escapeChar == '\n') {
                throw new IllegalArgumentException("Escape character must not be a line break");
            }
        }
    }
    
    private void validateLimitAndOffset() {
        if (format.limit < -1) {
            throw new IllegalArgumentException("Limit must be non-negative or -1 (no limit)");
        }
        if (format.offset < 0) {
            throw new IllegalArgumentException("Offset must be non-negative");
        }
        if (format.sizeHint < 0) {
            throw new IllegalArgumentException("Size hint must be non-negative");
        }
    }
    
    private void validateColumns() {
        if (explicitAutoColumns && !format.autoColumns && columnBuilders.isEmpty()) {
            throw new IllegalArgumentException("No columns specified. Please specify at least one column or use autoColumns(true)");
        }
    }
}
