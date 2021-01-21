package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SyncableFileOutputStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @since 0.11
 */
public class AvroSaver {

    private boolean createMissingDirs;
    private String namespace;
    private String name;

    /**
     * Instructs the saver to create any missing directories in the file path.
     *
     * @return this saver instance
     */
    public AvroSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }


    /**
     * Sets the schema name of the generated Avro file. Optional. The default will be "DataFrame".
     */
    public AvroSaver name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the schema namespace of the generated Avro file. Optional. The default will be "com.nhl.dflib".
     */
    public AvroSaver namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public void save(DataFrame df, OutputStream out) {

        Schema schema = createSchema(df);

        try {
            save(df, schema, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing records as Avro: " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, File file) {

        if (createMissingDirs) {
            File dir = file.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }

        // using SyncableFileOutputStream just like Avro does (though it should work with a regula FOS)
        try (SyncableFileOutputStream out = new SyncableFileOutputStream(file);) {
            save(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing Avro file '" + file + "': " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, String fileName) {
        save(df, new File(fileName));
    }

    protected void save(DataFrame df, Schema schema, OutputStream out) throws IOException {
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (DataFileWriter<GenericRecord> writer = new DataFileWriter<>(datumWriter)) {
            writer.create(schema, out);

            for (RowProxy r : df) {
                writer.append(map(schema, r));
            }
        }
    }

    protected Schema createSchema(DataFrame df) {

        String name = this.name != null ? this.name : "DataFrame";
        String namespace = this.namespace != null ? this.namespace : "com.nhl.dflib";

        SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder
                .record(name)
                .namespace(namespace)
                .fields();

        for (String column : df.getColumnsIndex()) {
            Class<?> type = df.getColumn(column).getInferredType();
            createSchemaField(fields, column, type);
        }

        return fields.endRecord();
    }

    protected GenericRecord map(Schema schema, RowProxy r) {

        GenericRecord ar = new GenericData.Record(schema);

        // TODO: Avoid copying data. GenericRecord is simple, so just create a simple wrapper around RowProxy.
        for (Schema.Field f : schema.getFields()) {
            ar.put(f.name(), r.get(f.name()));
        }

        return ar;
    }

    protected void createSchemaField(SchemaBuilder.FieldAssembler<Schema> builder, String column, Class<?> type) {
        builder.name(column).type(avroType(type)).noDefault();
    }

    protected Schema avroType(Class<?> type) {
        switch (type.getName()) {

            case "int":
                return Schema.create(Schema.Type.INT);
            case "java.lang.Integer":
                return Schema.createUnion(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL));

            case "long":
                return Schema.create(Schema.Type.LONG);
            case "java.lang.Long":
                return Schema.createUnion(Schema.create(Schema.Type.LONG), Schema.create(Schema.Type.NULL));

            case "float":
                return Schema.create(Schema.Type.FLOAT);
            case "java.lang.Float":
                return Schema.createUnion(Schema.create(Schema.Type.FLOAT), Schema.create(Schema.Type.NULL));

            case "double":
                return Schema.create(Schema.Type.DOUBLE);
            case "java.lang.Double":
                return Schema.createUnion(Schema.create(Schema.Type.DOUBLE), Schema.create(Schema.Type.NULL));

            case "boolean":
                return Schema.create(Schema.Type.BOOLEAN);
            case "java.lang.Boolean":
                return Schema.createUnion(Schema.create(Schema.Type.BOOLEAN), Schema.create(Schema.Type.NULL));

            // TODO: byte[], java.time, BigDecimal, BigInteger, etc.

            default:
                return Schema.create(Schema.Type.STRING);
        }
    }


}
