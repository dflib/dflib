package com.nhl.dflib.colset;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.Map;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * @since 1.0.0-M19
 */
public abstract class ColumnSetIndex {

    public static ColumnSetIndex ofAdd(DataFrame source, String... columns) {
        Index noColumnsFromSource = source.getColumnsIndex().deduplicateLabels(columns);
        return new ByLabelColumnSetIndex(source, noColumnsFromSource, noColumnsFromSource.getLabels());
    }

    public static ColumnSetIndex of(DataFrame source, String... columns) {
        return new ByLabelColumnSetIndex(source, Index.ofDeduplicated(columns), columns);
    }

    public static ColumnSetIndex of(DataFrame source, Index index) {
        return new ByLabelColumnSetIndex(source, index, index.getLabels());
    }

    public static ColumnSetIndex of(DataFrame source, int[] positions) {
        return new ByPosColumnSetIndex(source, positions);
    }

    final DataFrame source;

    protected ColumnSetIndex(DataFrame source) {
        this.source = source;
    }

    public abstract int size();

    public DataFrame select(Series<?>[] columns, Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(null, targetIndex().rename(oldToNewNames), columns);
    }

    public DataFrame select(Series<?>[] columns) {
        return new ColumnDataFrame(null, targetIndex(), columns);
    }

    public DataFrame replace(Series<?>[] columns, Map<String, String> oldToNewNames) {
        return new ColumnDataFrame(null, source.getColumnsIndex().rename(oldToNewNames), columns);
    }

    public DataFrame merge(Series<?>[] columns, Map<String, String> oldToNewNames) {
        ColumnSetMerger merger = merger();
        return new ColumnDataFrame(null,
                merger.mergedIndex().rename(oldToNewNames),
                merger.mergedColumns(source, columns));
    }

    public DataFrame merge(Series<?>[] columns) {
        ColumnSetMerger merger = merger();
        return new ColumnDataFrame(null,
                merger.mergedIndex(),
                merger.mergedColumns(source, columns));
    }

    public Series<?> getOrCreateColumn(int pos) {
        String name = originalLabels()[pos];
        return source.getColumnsIndex().hasLabel(name)
                ? source.getColumn(name)
                : new SingleValueSeries<>(null, source.height());
    }

    public Series<?> getOrCreateColumn(
            int pos,
            UnaryOperator<Series<?>> andApplyToExisting,
            Supplier<Series<?>> createNew) {

        String name = originalLabels()[pos];
        return source.getColumnsIndex().hasLabel(name)
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

        ByLabelColumnSetIndex(DataFrame source, Index targetIndex, String[] labels) {
            super(source);
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
            return ColumnSetMerger.of(source.getColumnsIndex(), originalLabels);
        }
    }

    static final class ByPosColumnSetIndex extends ColumnSetIndex {

        final int[] positions;
        volatile String[] originalLabels;
        volatile Index targetIndex;

        ByPosColumnSetIndex(DataFrame source, int[] positions) {
            super(source);
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
            Index sourceIndex = source.getColumnsIndex();
            int sourceLen = sourceIndex.size();

            int len = positions.length;
            String[] labels = new String[len];
            for (int i = 0; i < len; i++) {
                labels[i] = positions[i] < sourceLen
                        ? sourceIndex.getLabel(positions[i])
                        : String.valueOf(positions[i]);
            }

            return labels;
        }

        @Override
        ColumnSetMerger merger() {
            return ColumnSetMerger.of(source.getColumnsIndex(), positions);
        }
    }
}
