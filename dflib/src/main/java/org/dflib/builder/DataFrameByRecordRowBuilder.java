package org.dflib.builder;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @since 2.0.0
 */
public class DataFrameByRecordRowBuilder<T extends Record> {

    private final Class<? extends Record> type;
    private Index columnsIndex;
    private int capacity;

    public DataFrameByRecordRowBuilder(Class<? extends Record> type) {
        this.type = type;
    }

    /**
     * Sets an explicit set of column names in the resulting DataFrame. If not called, columns are taken from the
     * record class, sorted alphabetically.
     */
    public DataFrameByRecordRowBuilder<T> columnNames(String... columnNames) {
        return columnIndex(Index.of(columnNames));
    }

    /**
     * Sets an explicit set of columns in the resulting DataFrame. If not called, columns are taken from the
     * record class, sorted alphabetically.
     */
    public DataFrameByRecordRowBuilder<T> columnIndex(Index columnsIndex) {
        this.columnsIndex = columnsIndex;
        return this;
    }

    /**
     * Sets an explicit "vertical" capacity to avoid internal array resizing. If not set, DFLib will make a guess if
     * possible or use a default.
     */
    public DataFrameByRecordRowBuilder<T> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public DataFrame of(Iterable<T> source) {
        int capacity = guessCapacity(source);

        Accessors accessors = columnsIndex != null
                ? accessorsForColumns(columnsIndex.toArray())
                : accessorsForAllRecordMembers();

        int w = accessors.names.length;
        if (w == 0) {
            return DataFrame.empty();
        }

        ObjectAccum<Object>[] accums = new ObjectAccum[w];
        for (int i = 0; i < w; i++) {
            accums[i] = new ObjectAccum<>(capacity);
        }

        for (T record : source) {
            for (int i = 0; i < w; i++) {
                try {
                    accums[i].push(accessors.methods[i].invoke(record));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to invoke accessor '" + accessors.names[i] + "'", e);
                }
            }
        }

        Series<?>[] series = new Series[w];
        for (int i = 0; i < w; i++) {
            series[i] = accums[i].toSeries();
        }

        Index index = columnsIndex != null ? columnsIndex : Index.of(accessors.names);
        return new ColumnDataFrame(null, index, series);
    }


    private Accessors accessorsForColumns(String[] names) {

        int w = names.length;
        Method[] methods = new Method[w];

        RecordComponent[] components = type.getRecordComponents();
        for (int i = 0; i < w; i++) {
            for (RecordComponent c : components) {
                if (c.getName().equals(names[i])) {
                    methods[i] = c.getAccessor();
                    break;
                }
            }

            if (methods[i] == null) {
                throw new IllegalArgumentException("'" + names[i] + "' is not a member of the record type " + type.getName());
            }
        }

        return new Accessors(names, methods);
    }

    private Accessors accessorsForAllRecordMembers() {

        RecordComponent[] components = type.getRecordComponents();

        // TODO: is "getRecordComponents()" returned by copy, and are we allowed to sort it?
        Arrays.sort(components, Comparator.comparing(RecordComponent::getName));

        int w = components.length;
        String[] names = new String[w];
        Method[] methods = new Method[w];

        for (int i = 0; i < w; i++) {
            names[i] = components[i].getName();
            methods[i] = components[i].getAccessor();
        }

        return new Accessors(names, methods);
    }

    private int guessCapacity(Iterable<?> source) {
        if (this.capacity > 0) {
            return this.capacity;
        } else {
            return BuilderCapacity.capacity(source);
        }
    }

    record Accessors(String[] names, Method[] methods) {
    }
}
