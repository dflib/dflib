package org.dflib.avro;

import org.dflib.DataFrame;
import org.apache.avro.Schema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Saves DataFrame schemas in JSON format.
 *
 * @since 0.11
 */
public class AvroSchemaSaver extends BaseSaver<AvroSchemaSaver> {

    /**
     * Compiles a schema for the DataFrame and stores it in the provided file.
     */
    public void save(DataFrame df, Path filePath) {
        Schema schema = schemaBuilder.compileSchema(df);
        save(schema, filePath.toFile());
    }

    /**
     * Compiles a schema for the DataFrame and stores it in the provided file.
     */
    public void save(DataFrame df, String fileName) {
        Schema schema = schemaBuilder.compileSchema(df);
        save(schema, fileName);
    }

    /**
     * Compiles a schema for the DataFrame and stores it in the provided file.
     */
    public void save(DataFrame df, File file) {
        Schema schema = schemaBuilder.compileSchema(df);
        save(schema, file);
    }

    /**
     * Compiles a schema for the DataFrame and stores it in the provided stream.
     */
    public void save(DataFrame df, OutputStream out) {
        Schema schema = schemaBuilder.compileSchema(df);
        save(schema, out);
    }

    public void save(Schema schema, Path filePath) {
        save(schema, filePath.toFile());
    }

    public void save(Schema schema, String fileName) {
        save(schema, new File(fileName));
    }

    public void save(Schema schema, File file) {

        createMissingDirsIfNeeded(file);

        try (FileOutputStream out = new FileOutputStream(file)) {
            save(schema, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing Avro file '" + file + "': " + e.getMessage(), e);
        }
    }

    public void save(Schema schema, OutputStream out) {

        try {
            doSave(schema, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing records as Avro: " + e.getMessage(), e);
        }
    }

    protected void doSave(Schema schema, OutputStream out) throws IOException {
        byte[] bytes = schema.toString().getBytes(StandardCharsets.UTF_8);
        out.write(bytes);
    }
}
