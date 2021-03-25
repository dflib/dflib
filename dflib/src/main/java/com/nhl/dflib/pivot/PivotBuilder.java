package com.nhl.dflib.pivot;

import com.nhl.dflib.Aggregator;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.SeriesAggregator;
import com.nhl.dflib.aggregate.ColumnAggregator;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 0.11
 */
public class PivotBuilder {

    private final DataFrame dataFrame;
    private int columnForColumns = -1;
    private int columnForRows = -1;

    public PivotBuilder(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
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
     * {@link #values(String, SeriesAggregator)}.
     */
    public DataFrame values(String columnName) {
        int pos = validateColumn(columnName);
        return values(pos);
    }

    /**
     * Executes pivot transform, using values from the provided column name to populate the resulting DataFame.
     * There must be no more than one value for each pivot row and column combination, or an exception will be thrown.
     * For datasets where multiple values are present, use a flavor with value aggregator -
     * {@link #values(int, SeriesAggregator)}.
     */
    public DataFrame values(int columnPos) {
        return values(columnPos, null);
    }

    /**
     * Executes pivot transform, using values from the provided column name to populate the resulting DataFame. Values
     * with matching pivot row and column are aggregated with the provided aggregator.
     */
    public DataFrame values(String columnName, SeriesAggregator<?, ?> valuesAggregator) {
        int pos = validateColumn(columnName);
        return values(pos, valuesAggregator);
    }

    /**
     * Executes pivot transform, using values from the provided column name to populate the resulting DataFame. Values
     * with matching pivot row and column are aggregated with the provided aggregator.
     */
    public DataFrame values(int columnPos, SeriesAggregator<?, ?> valuesAggregator) {
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
        int rightRowPos = 2;
        return left.fullJoin().on(0).with(right)
                .map(df -> df.fillNullsFromSeries(0, df.getColumn(rightRowPos)))
                .map(df -> df.dropColumns(df.getColumnsIndex().getLabel(rightRowPos)));
    }

    private DataFrame empty(String rowColumnName) {
        return DataFrame.newFrame(rowColumnName).empty();
    }

    private DataFrame aggregateChunk(DataFrame chunk, SeriesAggregator<?, ?> valuesAggregator) {

        if (valuesAggregator == null) {
            // TODO: need to handle no-aggregator validation.. dupes must result in an exception
            return chunk;
        }

        String rowColumnName = chunk.getColumnsIndex().getLabel(0);
        String valueColumnName = chunk.getColumnsIndex().getLabel(1);

        return chunk.group(rowColumnName).agg(
                Aggregator.first(rowColumnName),
                new ColumnAggregator(valuesAggregator, i -> 1, i -> valueColumnName)
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
