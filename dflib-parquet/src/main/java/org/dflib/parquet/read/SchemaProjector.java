package org.dflib.parquet.read;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolves a DFLib column filter against the top-level field names of a Parquet file, producing the names of the
 * columns to read, in the order they must appear in the DataFrame.
 */
@FunctionalInterface
public interface SchemaProjector {

    List<String> project(List<String> schemaColumns);

    static SchemaProjector ofCols(int... columns) {
        return sc -> SchemaProjector.positions(sc, columns);
    }

    static SchemaProjector ofCols(String... columns) {
        return sc -> SchemaProjector.labels(sc, columns);
    }

    static SchemaProjector ofColsExcept(int... columns) {
        return sc -> SchemaProjector.positionsExcept(sc, columns);
    }

    static SchemaProjector ofColsExcept(String... columns) {
        return sc -> SchemaProjector.labelsExcept(sc, columns);
    }

    private static List<String> positions(List<String> schemaColumns, int[] columns) {
        List<String> projection = new ArrayList<>(columns.length);

        for (int column : columns) {
            if (column < 0 || column >= schemaColumns.size()) {
                throw new IllegalArgumentException("Column position is out of bounds: " + column);
            }
            projection.add(schemaColumns.get(column));
        }

        return projection;
    }

    private static List<String> labels(List<String> schemaColumns, String[] columns) {
        List<String> projection = new ArrayList<>(columns.length);

        for (String column : columns) {
            if (!schemaColumns.contains(column)) {
                throw new IllegalArgumentException("Column is not present in the Parquet schema: " + column);
            }
            projection.add(column);
        }

        return projection;
    }

    private static List<String> positionsExcept(List<String> schemaColumns, int[] columns) {
        int w = columns.length;
        if (w == 0) {
            return schemaColumns;
        }

        Set<Integer> excludes = new HashSet<>((int) Math.ceil(w / 0.75));
        for (int e : columns) {
            excludes.add(e);
        }

        int len = schemaColumns.size();
        List<String> projection = new ArrayList<>(len - excludes.size());
        for (int i = 0; i < len; i++) {
            if (!excludes.contains(i)) {
                projection.add(schemaColumns.get(i));
            }
        }

        return projection;
    }

    private static List<String> labelsExcept(List<String> schemaColumns, String[] columns) {
        int w = columns.length;
        if (w == 0) {
            return schemaColumns;
        }

        Set<String> excludes = new HashSet<>((int) Math.ceil(w / 0.75));
        Collections.addAll(excludes, columns);

        List<String> projection = new ArrayList<>(schemaColumns.size() - excludes.size());
        for (String c : schemaColumns) {
            if (!excludes.contains(c)) {
                projection.add(c);
            }
        }

        return projection;
    }
}
