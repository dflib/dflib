package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.DataSlice;

import java.util.ArrayList;
import java.util.List;

final class ColumnDetectionCallback implements DataCallback {

    private final ColumnDetectionListener listener;
    private final List<DataSlice> firstRowSlices;
    private final boolean trailingDelimiter;

    ColumnDetectionCallback(ColumnDetectionListener listener, boolean trailingDelimiter) {
        this.listener = listener;
        this.firstRowSlices = new ArrayList<>();
        this.trailingDelimiter = trailingDelimiter;
    }

    @Override
    public void onNewColumn(DataSlice slice) {
        DataSlice copy = slice.copy();
        firstRowSlices.add(copy);
    }

    @Override
    public void onNewRow(DataSlice[] unused) {
        if (trailingDelimiter && !firstRowSlices.isEmpty()) {
            // trim the last column if it's produced by the trailing delimiter.
            DataSlice lastSlice = firstRowSlices.get(firstRowSlices.size() - 1);
            if (lastSlice.from() == lastSlice.to()) {
                firstRowSlices.remove(firstRowSlices.size() - 1);
            }
        }
        listener.columnsDetected(firstRowSlices.toArray(new DataSlice[0]));
    }
}
