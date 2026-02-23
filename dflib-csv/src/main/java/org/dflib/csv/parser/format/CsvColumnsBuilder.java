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

    final List<CsvColumnMapping.Builder> columns;

    /**
     * Creates an empty builder.
     */
    public CsvColumnsBuilder() {
        this.columns = new ArrayList<>();
    }

    /**
     * Builds merged column formats using global defaults from {@link CsvFormat}.
     */
    public List<CsvColumnMapping> build(CsvParserConfig config) {
        AtomicInteger index = new AtomicInteger(0);
        return columns.stream()
                .peek(cb -> {
                    // patch empty names, the only variant for this is a manually built format
                    int idx = index.getAndIncrement();
                    if(cb.name == null) {
                        cb.name("c" + idx);
                    }
                    cb.format(CsvColumnFormat.mergeWithConfig(cb.format, config.csvFormat()));
                    if(!cb.nullableDefined) {
                        cb.nullable(config.nullable());
                    }
                })
                .map(CsvColumnMapping.Builder::build)
                .collect(Collectors.toList());
    }

    /**
     * Merges all provided column builders.
     */
    public void merge(List<CsvColumnMapping> columns) {
        for (CsvColumnMapping column : columns) {
            merge(CsvColumnMapping.column(column.idx).merge(column));
        }
    }

    /**
     * Merges a single column builder by index or name.
     */
    public void merge(CsvColumnMapping.Builder column) {
        Objects.requireNonNull(column, "Column can't be null");
        if (column.idx == -1) {
            mergeByName(column.name, column);
        } else {
            mergeByIndex(column.idx, column);
        }
    }

    private void mergeByIndex(int index, CsvColumnMapping.Builder column) {
        CsvColumnMapping.Builder columnTo = index >= columns.size() ? null : get(index);
        CsvColumnMapping.Builder merged = mergeTo(column, columnTo);
        setByIndex(index, merged);
    }

    private void mergeByName(String name, CsvColumnMapping.Builder column) {
        CsvColumnMapping.Builder columnTo = findByName(name);
        CsvColumnMapping.Builder merged = mergeTo(column, columnTo);
        if (merged.idx != -1) {
            setByIndex(merged.idx, column);
        } else {
            append(merged);
        }
    }

    private CsvColumnMapping.Builder mergeTo(CsvColumnMapping.Builder column, CsvColumnMapping.Builder mergeTo) {
        if (mergeTo == null) {
            return column;
        }
        return mergeTo.merge(column);
    }

    private void append(CsvColumnMapping.Builder merged) {
        columns.add(merged.index(columns.size()));
    }

    private void setByIndex(int index, CsvColumnMapping.Builder merged) {
        ensureSize(index);
        merged.index(index);
        columns.set(index, merged);
    }

    private void ensureSize(int idxTo) {
        for (int i = columns.size(); i <= idxTo; i++) {
            // skip this filler column
            // it could be set later
            columns.add(CsvColumnMapping.column(i).type(CsvColumnType.STRING).skip());
        }
    }

    private CsvColumnMapping.Builder findByName(String name) {
        if (name == null) {
            return null;
        }
        for (CsvColumnMapping.Builder column : columns) {
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
    public CsvColumnMapping.Builder get(int i) {
        return columns.get(i);
    }
}
