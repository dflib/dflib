package org.dflib.avro.schema;

import org.apache.avro.Schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @since 2.0.0
 */
@FunctionalInterface
public interface SchemaProjector {

    Schema project(Schema schema);

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

    private static Schema positions(Schema schema, int[] columns) {

        Schema newSchema = Schema.createRecord(
                schema.getName(),
                schema.getDoc(),
                schema.getNamespace(),
                schema.isError()
        );

        List<Schema.Field> fields = schema.getFields();
        List<Schema.Field> newFields = new ArrayList<>(columns.length);

        for (int column : columns) {
            // TODO: DFLib exceptions for invalid positions
            Schema.Field f = fields.get(column);
            Schema.Field newField = new Schema.Field(
                    f.name(),
                    f.schema(),
                    f.doc(),
                    f.defaultVal(),
                    f.order()
            );

            // TODO: copy field aliases?

            newFields.add(newField);
        }

        newSchema.setFields(newFields);
        return newSchema;
    }

    private static Schema labels(Schema schema, String[] columns) {

        Schema newSchema = Schema.createRecord(
                schema.getName(),
                schema.getDoc(),
                schema.getNamespace(),
                schema.isError()
        );

        List<Schema.Field> newFields = new ArrayList<>(columns.length);

        for (String column : columns) {
            Schema.Field f = schema.getField(column);
            Schema.Field newField = new Schema.Field(
                    f.name(),
                    f.schema(),
                    f.doc(),
                    f.defaultVal(),
                    f.order()
            );

            // TODO: copy field aliases?

            newFields.add(newField);
        }

        newSchema.setFields(newFields);
        return newSchema;
    }

    private static Schema positionsExcept(Schema schema, int[] columns) {
        int w = columns.length;
        if (w == 0) {
            return schema;
        }

        Set<Integer> excludes = new HashSet<>((int) Math.ceil(w / 0.75));
        for (int e : columns) {
            excludes.add(e);
        }

        int len = schema.getFields().size();
        int[] positions = new int[len - excludes.size()];

        for (int ii = 0, i = 0; i < len; i++) {
            if (!excludes.contains(i)) {
                positions[ii++] = i;
            }
        }

        return positions(schema, positions);
    }

    private static Schema labelsExcept(Schema schema, String[] columns) {
        int w = columns.length;
        if (w == 0) {
            return schema;
        }

        Set<String> excludes = new HashSet<>((int) Math.ceil(w / 0.75));
        Collections.addAll(excludes, columns);

        Schema newSchema = Schema.createRecord(
                schema.getName(),
                schema.getDoc(),
                schema.getNamespace(),
                schema.isError()
        );

        List<Schema.Field> newFields = new ArrayList<>(columns.length);

        for (Schema.Field f : schema.getFields()) {
            if (!excludes.contains(f.name())) {
                Schema.Field newField = new Schema.Field(
                        f.name(),
                        f.schema(),
                        f.doc(),
                        f.defaultVal(),
                        f.order()
                );

                // TODO: copy field aliases?

                newFields.add(newField);
            }
        }

        newSchema.setFields(newFields);
        return newSchema;
    }
}
