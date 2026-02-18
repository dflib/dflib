package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataSlice;

/**
 * Listener interface for the column detection stage.
 */
@FunctionalInterface
interface ColumnDetectionListener {

    void columnsDetected(DataSlice[] data);

    default ColumnDetectionListener andThen(ColumnDetectionListener listener) {
        return listener == null ? this : (data) -> {
            this.columnsDetected(data);
            listener.columnsDetected(data);
        };
    }

}
