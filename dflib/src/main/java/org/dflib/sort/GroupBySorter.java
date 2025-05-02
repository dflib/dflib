package org.dflib.sort;


import org.dflib.GroupBy;
import org.dflib.Sorter;

/**
 * Sorting processor for GroupBy objects.
 *
 * @deprecated unused. GroupBy does its own sorting
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public class GroupBySorter {

    private final GroupBy groupBy;

    public GroupBySorter(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

    public GroupBy sort(String column, boolean ascending) {
        return sort(IntComparator.of(groupBy.getSource().getColumn(column), ascending));
    }

    public GroupBy sort(int column, boolean ascending) {
        return sort(IntComparator.of(groupBy.getSource().getColumn(column), ascending));
    }

    public GroupBy sort(String[] columns, boolean[] ascending) {
        return sort(IntComparator.of(groupBy.getSource(), columns, ascending));
    }

    public GroupBy sort(int[] columns, boolean[] ascending) {
        return sort(IntComparator.of(groupBy.getSource(), columns, ascending));
    }

    public GroupBy sort(Sorter... sorters) {
        return sorters.length == 0 ? groupBy : sort(IntComparator.of(groupBy.getSource(), sorters));
    }

    public GroupBy sort(IntComparator sorter) {
        return groupBy.sort(sorter);
    }
}
