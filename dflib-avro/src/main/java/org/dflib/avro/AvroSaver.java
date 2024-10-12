package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.avro.schema.AvroSchemaUtils;
import org.dflib.row.RowProxy;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SyncableFileOutputStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Saves DataFrames to binary ".avro" files with an embedded schema and optional compression.
 */
public class AvroSaver extends BaseSaver<AvroSaver> {

    private CodecFactory codec;
    private Schema schema;

    public AvroSaver codec(CodecFactory codec) {
        this.codec = codec;
        return this;
    }

    /**
     * Save data with the explicit Schema. If not set, a Schema will be generated automatically based on the
     * DataFrame contents.
     */
    public AvroSaver schema(Schema schema) {
        this.schema = schema;
        return this;
    }

    public void save(DataFrame df, OutputStream out) {

        Schema schema = getOrCreateSchema(df);
        try {
            doSave(df, schema, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing records as Avro: " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, File file) {

        createMissingDirsIfNeeded(file);

        // using SyncableFileOutputStream just like Avro does (though it should work with a regula FOS)
        try (SyncableFileOutputStream out = new SyncableFileOutputStream(file)) {
            save(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing Avro file '" + file + "': " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, Path filePath) {
        save(df, filePath.toFile());
    }


    public void save(DataFrame df, String fileName) {
        save(df, new File(fileName));
    }

    protected Schema getOrCreateSchema(DataFrame df) {
        return this.schema != null ? this.schema : schemaBuilder.compileSchema(df);
    }

    protected void doSave(DataFrame df, Schema schema, OutputStream out) throws IOException {

        DataFrame avroReadyDf = makeAvroReady(df, schema);

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);

        // DataFileWriter includes Schema in the output
        try (DataFileWriter<GenericRecord> outWriter = new DataFileWriter<>(writer)) {

            if (codec != null) {
                outWriter.setCodec(codec);
            }

            outWriter.create(schema, out);

            // using flyweight wrapper around DFLib RowProxy
            RowToAvroRecordAdapter record = new RowToAvroRecordAdapter(schema);
            for (RowProxy r : avroReadyDf) {
                outWriter.append(record.resetRow(r));
            }
        }
    }

    protected DataFrame makeAvroReady(DataFrame df, Schema schema) {

        // 1. enum values must be converted to GenericEnumSymbol
        // 2. unmapped types must be converted to Strings

        for (Schema.Field f : schema.getFields()) {
            Schema fSchema = f.schema().isUnion() ? AvroSchemaUtils.unpackUnion(f.schema()) : f.schema();

            if (AvroSchemaUtils.isEnum(fSchema)) {
                df = df.cols(f.name()).merge(Exp.$col(f.name()).mapVal(v -> new GenericData.EnumSymbol(fSchema, v)));
            } else if (AvroSchemaUtils.isUnmapped(fSchema)) {
                df = df.cols(f.name()).merge(Exp.$col(f.name()).castAsStr());
            }
        }

        return df;
    }


}
