package org.dflib.csv.parser.context;

/**
 * Callback for consuming data from the CSV parser
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public interface DataCallback {

    default void onNewColumn(DataSlice slice) {
    }

    /**
     * @param rowData row content or {@code null} if the parser in the column detection mode.
     */
    default void onNewRow(DataSlice[] rowData) {
    }

}
