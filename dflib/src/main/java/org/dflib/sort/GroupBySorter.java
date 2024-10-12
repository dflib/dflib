package org.dflib.sort;


import org.dflib.DataFrame;
import org.dflib.GroupBy;
import org.dflib.IntSeries;
import org.dflib.Sorter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Sorting processor for GroupBy objects.
 *
 * @see Comparators
 */
public class GroupBySorter {

    private final GroupBy groupBy;

    public GroupBySorter(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public GroupBy sort(String column, boolean ascending) {
        return sort(Comparators.of(groupBy.getSource().getColumn(column), ascending));
    }

    public GroupBy sort(int column, boolean ascending) {
        return sort(Comparators.of(groupBy.getSource().getColumn(column), ascending));
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        return sort(Comparators.of(groupBy.getSource(), columns, ascending));
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        return sort(Comparators.of(groupBy.getSource(), columns, ascending));
    }

    public GroupBy sort(Sorter... sorters) {
        return sorters.length == 0 ? groupBy : sort(Comparators.of(groupBy.getSource(), sorters));
    }

    public GroupBy sort(IntComparator sorter) {

        Objects.requireNonNull(sorter, "Null 'sorter'");

        DataFrame ungrouped = groupBy.getSource();
        Map<Object, IntSeries> sorted = new LinkedHashMap<>((int) (groupBy.size() / 0.75));

        for (Object groupKey : groupBy.getGroupKeys()) {
            IntSeries groupIndex = groupBy.getGroupIndex(groupKey);
            IntSeries sortedGroup = DataFrameSorter.sort(sorter, groupIndex);
            sorted.put(groupKey, sortedGroup);
        }

        return new GroupBy(ungrouped, sorted, sorter);
    }
}
