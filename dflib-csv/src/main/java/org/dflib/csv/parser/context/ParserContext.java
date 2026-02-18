package org.dflib.csv.parser.context;

import org.dflib.csv.parser.ParserState;

/**
 * General context holder of the parsing process.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public class ParserContext implements RuleContext {

    private final RowColumnPosition position;
    // guard slice that is used between columns
    private final DataSlice overflow = new DataSlice();

    DataCallback callback;

    // when true - current row shouldn't be processed
    private boolean skipRow;
    // when true - scanner should stop reading further input
    private boolean stopRequested;
    // the current state is valid only in AUTO mode
    private int currentState = ParserState.NO.ordinal();
    // override the next state to this one
    public int stateOverride;
    // index in the rule flow, see RuleFlow
    public int flowIndex = -1;
    // when true - last processed slice (i.e., column) was quoted
    private boolean lastSliceQuoted;
    private boolean allowEmptyColumns;

    // Row-first mode fields
    private DataSlice[] rowSlices;
    private char[] deferredData;
    private boolean rowFirstMode;

    // Active slice: points to the DataSlice currently being written to by rules.
    // In per-column mode (header), this is a clean slice used by the `onNewColumn()` callback.
    // In row-first mode, this targets rowSlices[rowColumnCount] during scanning.
    private DataSlice activeSlice;

    public ParserContext() {
        this.activeSlice = new DataSlice();
        this.position = new RowColumnPosition();
        this.allowEmptyColumns = true;
    }

    public void setCallback(DataCallback callback) {
        this.callback = callback;
    }

    public DataCallback callback() {
        return callback;
    }

    public boolean stopRequested() {
        return stopRequested;
    }

    public void initRowBuffer(int columnCount, boolean allowEmptyColumns) {
        this.rowSlices = new DataSlice[columnCount];
        for (int i = 0; i < columnCount; i++) {
            this.rowSlices[i] = new DataSlice();
            if (deferredData != null) {
                this.rowSlices[i].setData(deferredData);
            }
        }
        this.deferredData = null;
        this.position.column = 0;
        this.rowFirstMode = true;
        this.activeSlice = rowSlices[0];
        this.allowEmptyColumns = allowEmptyColumns;
    }

    public void rebindRowSlices(char[] data) {
        if (!rowFirstMode) {
            activeSlice.setData(data);
            deferredData = data;
            return;
        }
        for (DataSlice rowSlice : rowSlices) {
            rowSlice.setData(data);
        }
    }

    public int bufferAnchor() {
        if (rowFirstMode) {
            return rowSlices[0].from();
        }
        return activeSlice.from();
    }

    public void shiftAll(int shift) {
        if (rowSlices != null) {
            for (int i = 0; i < position.column; i++) {
                rowSlices[i].shift(shift);
            }
        }
        // Shift the currently open column (whether it's activeSlice pointing to a rowSlice or dataSlice)
        if (activeSlice.open()) {
            activeSlice.shift(shift);
        }
    }

    @Override
    public void markColumnStart(int position) {
        lastSliceQuoted = false;
        if (rowFirstMode) {
            activeSlice = rowSlices[this.position.column];
        }
        activeSlice.setFrom(position);
        activeSlice.setQuoted(false);
        activeSlice.setEscaped(false);
    }

    @Override
    public void markColumnStartQuoted(int position) {
        markColumnStart(position);
        activeSlice.setQuoted(true);
    }

    @Override
    public void markColumnEnd(int position, boolean optional) {
        if (!activeSlice.open()) {
            if (optional) {
                return;
            } else {
                throw new IllegalStateException("Unexpected end of a column");
            }
        }

        lastSliceQuoted = activeSlice.quoted();
        activeSlice.setTo(position);
        if (rowFirstMode) {
            activeSlice = overflow;
        } else {
            callback.onNewColumn(activeSlice);
            activeSlice.reset();
        }
        this.position.advanceColumn();

    }

    @Override
    public void markRowEnd(int position) {
        if (skipRow) {
            skipRow = false;
            resetForRow();
            return;
        }

        // optional since the previous column could be quoted
        markColumnEnd(position, true);

        // check for missing columns in this row
        if (rowFirstMode && this.position.column < rowSlices.length) {
            if (allowEmptyColumns) {
                // Ensure that missing columns in short rows do not retain values from the previous row.
                // Missing slices are represented as "not open" (from == -1), so downstream extractors can treat them as null.
                for (int i = this.position.column; i < rowSlices.length; i++) {
                    // mark this slice as invalid
                    rowSlices[i].setFrom(-1);
                }
            } else {
                throw new IllegalStateException("Unexpected end of the row " + this.position);
            }
        }

        callback.onNewRow(rowSlices);
        this.position.advanceRow();
        resetForRow();
    }

    @Override
    public void skipRow(int position) {
        skipRow = true;
        markColumnEnd(position, true);
    }

    @Override
    public void markColumnEscape() {
        activeSlice.setEscaped(true);
    }

    @Override
    public boolean isLastSliceQuoted() {
        return lastSliceQuoted;
    }

    public void markLastRow(int lastPosition) {
        if (activeSlice.open() || position.column > 0) {
            // EOF right after a delimiter: the trailing column was never started because
            // the scanner ran out of data. Open it here so markRowEnd closes it as an
            // empty value instead of treating the row as short.
            if (rowFirstMode && !activeSlice.open() && position.column < rowSlices.length) {
                markColumnStart(lastPosition);
            }
            markRowEnd(lastPosition);
        }
    }

    @Override
    public void overrideState(ParserState parserState) {
        stateOverride = parserState.ordinal();
    }

    public int nextStateOverride() {
        int parserState = stateOverride;
        stateOverride = 0;
        return parserState;
    }

    void resetForRow() {
        if (!rowFirstMode) {
            activeSlice.reset();
        }
        lastSliceQuoted = false;
        stateOverride = 0;
        flowIndex = -1;
        position.resetColumn();
    }

    public boolean checkLimit(int limit) {
        // Note: we do not check the offset here, as it was processed at the OFFSET stage
        if(position.row < limit) {
            return true;
        }
        // this logic will allow one extra row to be processed to keep limit check fast
        stopRequested = true;
        return false;
    }

    @Override
    public DataSlice activeSlice() {
        return activeSlice;
    }

    public RowColumnPosition position() {
        return position;
    }

    public int currentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }
}
