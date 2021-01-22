package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SyncableFileOutputStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @since 0.11
 */
public class AvroSaver {

    private boolean createMissingDirs;
    private boolean excludeSchema;
    private final AvroSchemaBuilder schemaBuilder;

    public AvroSaver() {
        this.schemaBuilder = new AvroSchemaBuilder();
    }

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
        this.schemaBuilder.name(name);
        return this;
    }

    /**
     * Sets the schema namespace of the generated Avro file. Optional. The default will be "com.nhl.dflib".
     */
    public AvroSaver namespace(String namespace) {
        this.schemaBuilder.namespace(namespace);
        return this;
    }

    /**
     * Instructs saver to exclude schema from the saved file. It makes it more compact, but would require a
     * schema to be available externally on load.
     */
    public AvroSaver excludeSchema() {
        this.excludeSchema = true;
        return this;
    }

    public void save(DataFrame df, OutputStream out) {

        Schema schema = schemaBuilder.createSchema(df);

        try {
            if (excludeSchema) {
                saveNoSchema(df, schema, out);
            } else {
                saveWithSchema(df, schema, out);
            }
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
        try (SyncableFileOutputStream out = new SyncableFileOutputStream(file)) {
            save(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing Avro file '" + file + "': " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, String fileName) {
        save(df, new File(fileName));
    }

    protected void saveWithSchema(DataFrame df, Schema schema, OutputStream out) throws IOException {
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);

        // DataFileWriter includes Schema in the output
        try (DataFileWriter<GenericRecord> outWriter = new DataFileWriter<>(writer)) {
            outWriter.create(schema, out);
            for (RowProxy r : df) {
                outWriter.append(rowToRecord(schema, r));
            }
        }
    }

    protected void saveNoSchema(DataFrame df, Schema schema, OutputStream out) throws IOException {

        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

        // DatumWriter does not include Schema in the output
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        for (RowProxy r : df) {
            writer.write(rowToRecord(schema, r), encoder);
        }

        encoder.flush();
    }

    protected GenericRecord rowToRecord(Schema schema, RowProxy r) {

        GenericRecord ar = new GenericData.Record(schema);

        // TODO: Avoid copying data. GenericRecord is simple, so just create a GenericData wrapper around RowProxy.
        for (Schema.Field f : schema.getFields()) {
            ar.put(f.name(), r.get(f.name()));
        }

        return ar;
    }
}
