package org.dflib;

import org.dflib.agg.SeriesAggregator;
import org.dflib.concat.SeriesConcat;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since 0.6
 */
// TODO: common inheritance hierarchy between GroupBy and SeriesGroupBy (will likely need a DataFrameGroupBy)
public class SeriesGroupBy<T> {

    private Series<T> ungrouped;
    private Map<Object, IntSeries> groupsIndex;
    private Map<Object, Series<T>> resolvedGroups;

    public SeriesGroupBy(Series<T> ungrouped, Map<Object, IntSeries> groupsIndex) {
        this.ungrouped = ungrouped;
        this.groupsIndex = groupsIndex;
    }

    public int size() {
        return groupsIndex.size();
    }

    public Collection<Object> getGroups() {
        return groupsIndex.keySet();
    }

    public boolean hasGroup(Object key) {
        return groupsIndex.containsKey(key);
    }

    public IntSeries getGroupIndex(Object key) {
        return groupsIndex.get(key);
    }

    public Series<T> getGroup(Object key) {
        if (resolvedGroups == null) {
            resolvedGroups = new ConcurrentHashMap<>();
        }

        // TODO: nulls will blow up on read... check for nulls and do something right here..
        return resolvedGroups.computeIfAbsent(key, this::resolveGroup);
    }

    /**
     * Recombines groups back to a Series, preserving the effects of the initial grouping, and per-group sorting,
     * truncation and other operations.
     *
     * @return a new Series made from recombined groups.
     */
    public Series<T> toSeries() {
        IntSeries index = SeriesConcat.intConcat(groupsIndex.values());

        // this should hopefully preserve the nature of any primitive-based Series
        return ungrouped.select(index);
    }

    /**
     * Returns a Series, with each value corresponding to the result of a single group aggregation.
     */
    public <R> Series<R> agg(Exp<R> aggregator) {
        return SeriesAggregator.aggGroupBy(this, aggregator);
    }

    /**
     * Returns a DataFrame, with each row corresponding to the result of a single group aggregation with multiple
     * provided aggregators.
     */
    public DataFrame aggMultiple(Exp<?>... aggregators) {
        return SeriesAggregator.aggGroupMultiple(this, aggregators);
    }

    protected Series<T> resolveGroup(Object key) {

        IntSeries index = groupsIndex.get(key);
        if (index == null) {
            return null;
        }

        return ungrouped.select(index);
    }

}
