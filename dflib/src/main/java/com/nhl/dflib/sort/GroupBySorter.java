package com.nhl.dflib.sort;

import com.nhl.dflib.*;

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

    /**
     * @deprecated since 0.12 as sorting by RowToValueMapper is redundant, and can be expressed as a Sorter.
     */
    @Deprecated
    public <V extends Comparable<? super V>> GroupBy sort(RowToValueMapper<V> sortKeyExtractor) {
        return sort(Comparators.of(groupBy.getUngrouped(), sortKeyExtractor));
    }

    public GroupBy sort(Sorter... sorters) {
        return sorters.length == 0 ? groupBy : sort(Comparators.of(groupBy.getUngrouped(), sorters));
    }

    public GroupBy sort(IntComparator sorter) {

        Objects.requireNonNull(sorter, "Null 'sorter'");

        DataFrame ungrouped = groupBy.getUngrouped();
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupBy.size() / 0.75));

        for (Object groupKey : groupBy.getGroups()) {
            IntSeries groupIndex = groupBy.getGroupIndex(groupKey);
            IntSeries sortedGroup = new DataFrameSorter(ungrouped, groupIndex).sortIndex(sorter);
            sorted.put(groupKey, sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, sorter);
    }
}
