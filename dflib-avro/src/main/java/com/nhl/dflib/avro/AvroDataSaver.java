package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;
import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SyncableFileOutputStream;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Saves DataFrames to binary ".avro" files with an embedded schema and optional compression.
 *
 * @since 0.11
 */
public class AvroDataSaver extends BaseSaver<AvroDataSaver> {

    private CodecFactory codec;
    private Schema schema;

    public AvroDataSaver codec(CodecFactory codec) {
        this.codec = codec;
        return this;
    }

    /**
     * Save data with the explicit Schema. If not set, a Schema will be generated automatically based on the
     * DataFrame contents.
     */
    public AvroDataSaver schema(Schema schema) {
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

    public void save(DataFrame df, String fileName) {
        save(df, new File(fileName));
    }

    protected Schema getOrCreateSchema(DataFrame df) {
        return this.schema != null ? this.schema : schemaBuilder.compileSchema(df);
    }

    protected void doSave(DataFrame df, Schema schema, OutputStream out) throws IOException {
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);

        // DataFileWriter includes Schema in the output
        try (DataFileWriter<GenericRecord> outWriter = new DataFileWriter<>(writer)) {

            if (codec != null) {
                outWriter.setCodec(codec);
            }

            outWriter.create(schema, out);

            // using flyweight wrapper around DFLib RowProxy
            RowToAvroRecordAdapter record = new RowToAvroRecordAdapter(schema);
            DataFrame avroReadyDf = DataFrameSaveNormalizer.normalize(df);
            for (RowProxy r : avroReadyDf) {
                outWriter.append(record.resetRow(r));
            }
        }
    }


}
