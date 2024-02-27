package org.dflib.jdbc.connector;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.SeriesGroupBy;
import org.dflib.jdbc.SaveOp;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A thin wrapper around a <code>Series&gt;SaveOp></code> that provides a view of the save operation per-row statistics.
 * Useful when {@link TableSaver#save(DataFrame)} merges the data, generating a combination of inserts, updates, with some
 * rows possibly skipped.
 *
 * @since 0.6
 */
public class SaveStats {

    private Supplier<Series<SaveOp>> statsSupplier;

    private volatile Series<SaveOp> rowSaveStatuses;
    private volatile SeriesGroupBy<SaveOp> rowSaveStatusesByOp;

    public SaveStats(Supplier<Series<SaveOp>> statsSupplier) {
        this.statsSupplier = statsSupplier;
    }

    public DataFrame changedRows(DataFrame savedDf) {
        return savedDf.rows(getRowSaveStatuses().index(op -> op != SaveOp.skip)).select();
    }

    public Series<SaveOp> getRowSaveStatuses() {

        // No synchronization. Presuming the supplier is reentrant, and stats consumers are either no or low-concurrency.
        if (rowSaveStatuses == null) {
            rowSaveStatuses = statsSupplier.get();
        }

        return rowSaveStatuses;
    }

    public int getInsertCount() {
        return getOpCount(SaveOp.insert);
    }

    public int getUpdateCount() {
        return getOpCount(SaveOp.update);
    }

    public int getSkipCount() {
        return getOpCount(SaveOp.skip);
    }

    protected int getOpCount(SaveOp op) {

        // No synchronization. Presuming the supplier is reentrant, and stats consumers are either no or low-concurrency.
        if (rowSaveStatusesByOp == null) {
            rowSaveStatusesByOp = getRowSaveStatuses().group();
        }

        return rowSaveStatusesByOp.hasGroup(op) ? rowSaveStatusesByOp.getGroup(op).size() : 0;
    }

    @Override
    public String toString() {
        Map<String, Integer> map = new LinkedHashMap<>(5);
        map.put("inserted", getInsertCount());
        map.put("updated", getUpdateCount());
        map.put("unchanged", getSkipCount());
        return map.toString();
    }
}

