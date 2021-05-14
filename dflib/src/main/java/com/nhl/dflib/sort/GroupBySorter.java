package com.nhl.dflib.sort;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.RowToValueMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Sorting processor for GroupBy objects.
 *
 * @see Comparators
 * @since 0.11
 */
public class GroupBySorter {

    private final GroupBy groupBy;

    public GroupBySorter(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public GroupBy sort(String column, boolean ascending) {
        return sort(Comparators.of(groupBy.getUngrouped().getColumn(column), ascending));
    }

    public GroupBy sort(int column, boolean ascending) {
        return sort(Comparators.of(groupBy.getUngrouped().getColumn(column), ascending));
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        return sort(Comparators.of(groupBy.getUngrouped(), columns, ascending));
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        return sort(Comparators.of(groupBy.getUngrouped(), columns, ascending));
    }

    public GroupBy sort(IntComparator comparator) {

        Objects.requireNonNull(comparator, "Null 'sorter'");

        DataFrame ungrouped = groupBy.getUngrouped();
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupBy.size() / 0.75));

        for (Object groupKey : groupBy.getGroups()) {
            IntSeries groupIndex = groupBy.getGroupIndex(groupKey);
            IntSeries sortedGroup = new DataFrameSorter(ungrouped, groupIndex).sortedPositions(comparator);
            sorted.put(groupKey, sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, comparator);
    }

    public <V extends Comparable<? super V>> GroupBy sort(RowToValueMapper<V> sortKeyExtractor) {
        return sort(Comparators.of(groupBy.getUngrouped(), sortKeyExtractor));
    }
}
