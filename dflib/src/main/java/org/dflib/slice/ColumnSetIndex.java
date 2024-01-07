package org.dflib.slice;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @since 1.0.0-M19
 */
public abstract class ColumnSetIndex {

    public static ColumnSetIndex ofAdd(Index columnIndex, String... columns) {
        Index noColumnsFromSource = columnIndex.deduplicateLabels(columns);
        return new ByLabelColumnSetIndex(columnIndex, noColumnsFromSource, noColumnsFromSource.getLabels());
    }

    public static ColumnSetIndex of(Index columnIndex, String... columns) {
        return new ByLabelColumnSetIndex(columnIndex, Index.ofDeduplicated(columns), columns);
    }

    public static ColumnSetIndex of(Index columnIndex, Index index) {
        return new ByLabelColumnSetIndex(columnIndex, index, index.getLabels());
    }

    public static ColumnSetIndex of(Index columnIndex, int[] positions) {
        return new ByPosColumnSetIndex(columnIndex, positions);
    }

    final Index columnIndex;

    protected ColumnSetIndex(Index columnIndex) {
        this.columnIndex = columnIndex;
    }

    public abstract int size();

    public DataFrame select(Series<?>[] columns, Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(null, targetIndex().rename(oldToNewNames), columns);
    }

    public DataFrame select(Series<?>[] columns) {
        return new ColumnDataFrame(null, targetIndex(), columns);
    }

    public DataFrame replace(Series<?>[] columns, Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(null, columnIndex.rename(oldToNewNames), columns);
    }

    public DataFrame merge(DataFrame source, Series<?>[] columns, Map<String, String> oldToNewNames) {
        ColumnSetMerger merger = merger();
        return new ColumnDataFrame(null,
                merger.mergedIndex().rename(oldToNewNames),
                merger.mergedColumns(source, columns));
    }

    public DataFrame merge(DataFrame source, Series<?>[] columns) {
        ColumnSetMerger merger = merger();
        return new ColumnDataFrame(null,
                merger.mergedIndex(),
                merger.mergedColumns(source, columns));
    }

    public Series<?> getOrCreateColumn(DataFrame source, int pos) {
        String name = originalLabels()[pos];
        return columnIndex.hasLabel(name)
                ? source.getColumn(name)
                : new SingleValueSeries<>(null, source.height());
    }

    public Series<?> getOrCreateColumn(
            DataFrame source,
            int pos,
            UnaryOperator<Series<?>> andApplyToExisting,
            Supplier<Series<?>> createNew) {

        String name = originalLabels()[pos];
        return columnIndex.hasLabel(name)
                ? andApplyToExisting.apply(source.getColumn(name))
                : createNew.get();
    }

    // target Index is produced from "originalLabels", with difference being that it contain deduplicated labels that
    // can be used in the result, but can not be used to locate the original columns
    public abstract Index targetIndex();

    // allowed to contain duplicates like ["A", "A", "B", "B"]
    abstract String[] originalLabels();

    abstract ColumnSetMerger merger();

    static final class ByLabelColumnSetIndex extends ColumnSetIndex {

        final String[] originalLabels;
        final Index targetIndex;

        ByLabelColumnSetIndex(Index columnIndex, Index targetIndex, String[] labels) {
            super(columnIndex);
            this.targetIndex = targetIndex;
            this.originalLabels = labels;
        }

        @Override
        public int size() {
            return targetIndex.size();
        }

        @Override
        public Index targetIndex() {
            return targetIndex;
        }

        @Override
        String[] originalLabels() {
            return originalLabels;
        }

        @Override
        ColumnSetMerger merger() {
            return ColumnSetMerger.of(columnIndex, originalLabels);
        }
    }

    static final class ByPosColumnSetIndex extends ColumnSetIndex {

        final int[] positions;
        volatile String[] originalLabels;
        volatile Index targetIndex;

        ByPosColumnSetIndex(Index columnIndex, int[] positions) {
            super(columnIndex);
            this.positions = positions;
        }

        @Override
        public int size() {
            return positions.length;
        }

        @Override
        public Index targetIndex() {
            if (targetIndex == null) {
                targetIndex = createTargetIndex();
            }

            return targetIndex;
        }

        @Override
        String[] originalLabels() {
            if (originalLabels == null) {
                originalLabels = createOriginalLabels();
            }

            return originalLabels;
        }

        private Index createTargetIndex() {
            return Index.ofDeduplicated(originalLabels());
        }

        private String[] createOriginalLabels() {
            int sourceLen = columnIndex.size();

            int len = positions.length;
            String[] labels = new String[len];
            for (int i = 0; i < len; i++) {
                labels[i] = positions[i] < sourceLen
                        ? columnIndex.getLabel(positions[i])
                        : String.valueOf(positions[i]);
            }

            return labels;
        }

        @Override
        ColumnSetMerger merger() {
            return ColumnSetMerger.of(columnIndex, positions);
        }
    }
}
