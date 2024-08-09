package org.dflib.parquet.read;

import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @since 1.0.0-M23
 */
@FunctionalInterface
public interface SchemaProjector {

    MessageType project(MessageType schema);

    static SchemaProjector ofCols(int... columns) {
        return ch -> SchemaProjector.positions(ch, columns);
    }

    static SchemaProjector ofCols(String... columns) {
        return ch -> SchemaProjector.labels(ch, columns);
    }

    static SchemaProjector ofColsExcept(int... columns) {
        return ch -> SchemaProjector.positionsExcept(ch, columns);
    }

    static SchemaProjector ofColsExcept(String... columns) {
        return ch -> SchemaProjector.labelsExcept(ch, columns);
    }

    private static MessageType positions(MessageType schema, int[] columns) {
        int w = columns.length;
        List<Type> projection = new ArrayList<>(w);

        for (int i = 0; i < w; i++) {
            // TODO: DFLib exceptions for invalid positions
            projection.add(schema.getType(schema.getFieldName(columns[i])));
        }

        return new MessageType(schema.getName(), projection);
    }

    private static MessageType labels(MessageType schema, String[] columns) {
        int w = columns.length;
        List<Type> projection = new ArrayList<>(w);

        for (int i = 0; i < w; i++) {
            // TODO: DFLib exceptions for invalid columns
            projection.add(schema.getType(columns[i]));
        }

        return new MessageType(schema.getName(), projection);
    }

    private static MessageType positionsExcept(MessageType schema, int[] columns) {
        int w = columns.length;
        if (w == 0) {
            return schema;
        }

        Set<Integer> excludes = new HashSet<>((int) Math.ceil(w / 0.75));
        for (int e : columns) {
            excludes.add(e);
        }

        int len = schema.getFieldCount();
        int[] positions = new int[len - excludes.size()];

        for (int ii = 0, i = 0; i < len; i++) {
            if (!excludes.contains(i)) {
                positions[ii++] = i;
            }
        }

        return positions(schema, positions);
    }

    private static MessageType labelsExcept(MessageType schema, String[] columns) {
        int w = columns.length;
        if (w == 0) {
            return schema;
        }

        Set<String> excludes = new HashSet<>((int) Math.ceil(w / 0.75));
        for (String e : columns) {
            excludes.add(e);
        }

        List<Type> projection = new ArrayList<>();
        for (Type t : schema.getFields()) {
            if (!excludes.contains(t.getName())) {
                projection.add(t);
            }
        }

        return new MessageType(schema.getName(), projection);
    }
}
