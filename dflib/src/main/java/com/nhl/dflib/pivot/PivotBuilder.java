package com.nhl.dflib.pivot;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Series;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @since 0.11
 */
public class PivotBuilder {

    private static final Exp<?> oneValueAgg = Exp.$col(0).agg(PivotBuilder::oneValueAggregator);

    private final DataFrame dataFrame;
    private int columnForColumns = -1;
    private int columnForRows = -1;

    public PivotBuilder(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    private static <T> T oneValueAggregator(Series<? extends T> series) {

        switch (series.size()) {
            case 0:
                throw new IllegalArgumentException("Unexpected empty Series");
            case 1:
                return series.get(0);
            default:
                throw new IllegalArgumentException(
                        "Duplicate rows in the pivot table. " +
                                "Consider passing an explicit aggregator to the pivot operation.");
        }
    }

    /**
     * Use values from "columnName" to create pivoted table columns. For meaningful results "columnName" should contain
     * "categorical" data.
     */
    public PivotBuilder columns(String columnName) {
        this.columnForColumns = validateColumn(columnName);
        return this;
    }

    /**
     * Use values from "columnPos" to create pivoted table columns. For meaningful results "columnPos" should contain
     * "categorical" data.
     */
    public PivotBuilder columns(int columnPos) {
        this.columnForColumns = validateColumn(columnPos);
        return this;
    }

    /**
     * Use values from "columnName" to create pivoted table rows.
     */
    public PivotBuilder rows(String columnName) {
        this.columnForRows = validateColumn(columnName);
        return this;
    }

    /**
     * Use values from "columnPos" to create pivoted table rows.
     */
    public PivotBuilder rows(int columnPos) {
        this.columnForRows = validateColumn(columnPos);
        return this;
    }

    /**
     * Executes pivot transform, using values from the provided column name to populate the resulting DataFame.
     * There must be no more than one value for each pivot row and column combination, or an exception will be thrown.
     * For datasets where multiple values are present, use a flavor with value aggregator -
     * {@link #values(int, Exp)}.
     */
    public DataFrame values(String columnName) {
        int pos = validateColumn(columnName);
        return doPivot(pos, oneValueAgg);
    }

    /**
     * Executes pivot transform, using values from the provided column name to populate the resulting DataFame.
     * There must be no more than one value for each pivot row and column combination, or an exception will be thrown.
     * For datasets where multiple values are present, use a flavor with value aggregator -
     * {@link #values(int, Exp)}.
     */
    public DataFrame values(int columnPos) {
        return doPivot(columnPos, oneValueAgg);
    }

    /**
     * Executes pivot transform, using values from the provided column name to populate the resulting DataFame. Values
     * with matching pivot row and column are aggregated with the provided aggregator. Aggregator may look like this:
     * <code>$decimal(0).sum()</code>. Notice that the column name or index can be anything, as the evaluation happens
     * against individual columns, not DataFrame.
     */
    public <S, T> DataFrame values(String columnName, Exp<T> valuesAggregator) {
        int pos = validateColumn(columnName);
        return doPivot(pos, valuesAggregator);
    }

    /**
     * Executes pivot transform, using values from the provided column name to populate the resulting DataFame. Values
     * with matching pivot row and column are aggregated with the provided aggregator. Aggregator may look like this:
     * <code>$decimal(0).sum()</code>. Notice that the column name or index can be anything, as the evaluation happens
     * against individual columns, not DataFrame.
     */
    public <T> DataFrame values(int columnPos, Exp<T> valuesAggregator) {
        return doPivot(columnPos, valuesAggregator);
    }

    protected <T> DataFrame doPivot(int columnPos, Exp<T> valuesAggregator) {
        Objects.requireNonNull(valuesAggregator, "Null 'valuesAggregator'");
        int columnForValues = validateColumn(columnPos);

        if (columnForColumns < 0) {
            throw new IllegalStateException("Column for pivot columns is not specified. Call 'columns(..)'.");
        }

        if (columnForRows < 0) {
            throw new IllegalStateException("Column for pivot rows is not specified. Call 'rows(..)'.");
        }

        String rowColumnName = dataFrame.getColumnsIndex().getLabel(columnForRows);
        GroupBy byColumn = dataFrame.group(columnForColumns);

        List<DataFrame> chunks = new ArrayList<>(byColumn.size());

        for (Object col : byColumn.getGroups()) {

            DataFrame byColumnDf = byColumn.getGroup(col);
            DataFrame pivotChunk = DataFrame
                    .newFrame(rowColumnName, col.toString())
                    .columns(
                            byColumnDf.getColumn(columnForRows),
                            byColumnDf.getColumn(columnForValues)
                    ).map(df -> aggregateChunk(df, valuesAggregator));

            chunks.add(pivotChunk);
        }

        switch (chunks.size()) {
            case 0:
                return DataFrame.newFrame(rowColumnName).empty();
            case 1:
                return chunks.get(0);
            default:
                return chunks.stream()
                        .reduce(this::joinChunks)
                        .orElseGet(() -> empty(rowColumnName));
        }
    }

    private DataFrame joinChunks(DataFrame left, DataFrame right) {
        int rightRowPos = left.width();
        return left.fullJoin().on(0).with(right)
                .map(df -> df.fillNullsFromSeries(0, df.getColumn(rightRowPos)))
                .map(df -> df.dropColumns(df.getColumnsIndex().getLabel(rightRowPos)));
    }

    private DataFrame empty(String rowColumnName) {
        return DataFrame.newFrame(rowColumnName).empty();
    }

    private <S, T> DataFrame aggregateChunk(DataFrame chunk, Exp<T> aggregator) {

        String rowColumnName = chunk.getColumnsIndex().getLabel(0);
        String valueColumnName = chunk.getColumnsIndex().getLabel(1);

        return chunk.group(rowColumnName).agg(
                Exp.$col(rowColumnName).first(),

                // capture a column with dynamically defined name
                Exp.$col(valueColumnName)
                        // and then pass the resulting Series (not DataFrame)
                        // to the column name-agnostic aggregator exp
                        .unary(s -> aggregator.eval(s))
                        // restore the column name
                        .as(valueColumnName)
        );
    }

    private int validateColumn(String name) {
        // side effect of "position" is to validate "name" presence
        return dataFrame.getColumnsIndex().position(name);
    }

    private int validateColumn(int pos) {
        if (pos < 0) {
            throw new IllegalArgumentException("Negative column position: " + pos);
        }

        if (dataFrame.width() <= pos) {
            throw new IllegalArgumentException("Column position " + pos + " is out of bounds. DataFrame width: " + dataFrame.width());
        }

        return pos;
    }
}
