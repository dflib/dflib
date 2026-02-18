package org.dflib.csv.parser.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Utility class that merges information about CSV columns from different sources.
 * <p>
 * Data is indexed by the column index value, if set. If it's not set,
 * columns are indexed by the order in the merging list.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public class CsvColumnsBuilder {

    final List<CsvColumnFormat.Builder> columns;

    /**
     * Creates an empty builder.
     */
    public CsvColumnsBuilder() {
        this.columns = new ArrayList<>();
    }

    /**
     * Builds merged column formats using global defaults from {@link CsvFormat}.
     */
    public List<CsvColumnFormat> build(CsvFormat format) {
        AtomicInteger index = new AtomicInteger(0);
        return columns.stream()
                .peek(cb -> {
                    // patch empty names, the only variant for this is a manually built format
                    int idx = index.getAndIncrement();
                    if(cb.name == null) {
                        cb.name("c" + idx);
                    }
                    if (cb.quote == null) {
                        cb.quote(format.quote);
                    }
                    if (cb.trim == null) {
                        cb.trim(format.trim);
                    }
                    if (!cb.nullableDefined) {
                        cb.nullable = format.nullable;
                        cb.nullValue = format.nullValue == null ? null : format.nullValue.toCharArray();
                    }
                })
                .map(CsvColumnFormat.Builder::build)
                .collect(Collectors.toList());
    }

    /**
     * Merges all provided column builders.
     */
    public void merge(List<CsvColumnFormat.Builder> columns) {
        for (CsvColumnFormat.Builder column : columns) {
            merge(column);
        }
    }

    /**
     * Merges a single column builder by index or name.
     */
    public void merge(CsvColumnFormat.Builder column) {
        Objects.requireNonNull(column, "Column can't be null");
        if (column.idx == -1) {
            mergeByName(column.name, column);
        } else {
            mergeByIndex(column.idx, column);
        }
    }

    private void mergeByIndex(int index, CsvColumnFormat.Builder column) {
        CsvColumnFormat.Builder columnTo = index >= columns.size() ? null : get(index);
        CsvColumnFormat.Builder merged = mergeTo(column, columnTo);
        setByIndex(index, merged);
    }

    private void mergeByName(String name, CsvColumnFormat.Builder column) {
        CsvColumnFormat.Builder columnTo = findByName(name);
        CsvColumnFormat.Builder merged = mergeTo(column, columnTo);
        if (merged.idx != -1) {
            setByIndex(merged.idx, column);
        } else {
            append(merged);
        }
    }

    private CsvColumnFormat.Builder mergeTo(CsvColumnFormat.Builder column, CsvColumnFormat.Builder mergeTo) {
        if (mergeTo == null) {
            return column;
        }
        return mergeTo.merge(column);
    }

    private void append(CsvColumnFormat.Builder merged) {
        columns.add(merged.index(columns.size()));
    }

    private void setByIndex(int index, CsvColumnFormat.Builder merged) {
        ensureSize(index);
        merged.index(index);
        columns.set(index, merged);
    }

    private void ensureSize(int idxTo) {
        for (int i = columns.size(); i <= idxTo; i++) {
            // skip this filler column
            // it could be set later
            columns.add(CsvFormat.column(i).type(CsvColumnType.STRING).skip());
        }
    }

    private CsvColumnFormat.Builder findByName(String name) {
        if (name == null) {
            return null;
        }
        for (CsvColumnFormat.Builder column : columns) {
            if (name.equals(column.name)) {
                return column;
            }
        }
        return null;
    }

    /**
     * Returns the number of tracked column builders.
     */
    public int size() {
        return columns.size();
    }

    /**
     * Returns whether no columns were added.
     */
    public boolean isEmpty() {
        return columns.isEmpty();
    }

    /**
     * Returns a column builder by index.
     */
    public CsvColumnFormat.Builder get(int i) {
        return columns.get(i);
    }
}
