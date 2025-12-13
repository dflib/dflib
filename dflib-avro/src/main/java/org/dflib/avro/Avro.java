package org.dflib.avro;

import org.apache.avro.Schema;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.DataFrame;
import org.dflib.avro.schema.AvroSchemaCompiler;
import org.dflib.avro.types.AvroTypeExtensions;
import org.dflib.avro.types.SingleSchemaConversion;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;


public class Avro {

    /**
     * An extension point to register a conversion for a custom type mapped to an underlying Avro simple type.
     * DFLib already provides a collection of type extensions to map various common value types to Avro. This
     * method allows to cover the types that are not (yet) included in DFLib.
     *
     * @param conversion a custom subclass of {@link SingleSchemaConversion} implementing custom type read/write logic.
     */
    // TODO: deprecate in favor of AvroTypeExtensions?
    public static void registerCustomType(SingleSchemaConversion<?> conversion) {
        AvroTypeExtensions.registerCustomType(conversion);
    }

    public static Schema compileSchema(DataFrame data, String namespace, String name) {
        return schemaCompiler().namespace(namespace).name(name).compileSchema(data);
    }

    public static AvroSchemaCompiler schemaCompiler() {
        return new AvroSchemaCompiler();
    }

    public static DataFrame load(File file) {
        return loader().load(file);
    }

    public static DataFrame load(Path filePath) {
        return loader().load(filePath);
    }

    public static DataFrame load(String filePath) {
        return loader().load(filePath);
    }

    public static DataFrame load(byte[] bytes) {
        return loader().load(bytes);
    }

    /**
     * @since 1.1.0
     */
    public static DataFrame load(ByteSource src) {
        return loader().load(src);
    }

    /**
     * @since 1.1.0
     */
    public static Map<String, DataFrame> loadAll(ByteSources src) {
        return loader().loadAll(src);
    }

    public static AvroLoader loader() {
        return new AvroLoader();
    }


    public static Schema loadSchema(Path path) {
        return schemaLoader().load(path);
    }

    public static Schema loadSchema(File file) {
        return schemaLoader().load(file);
    }

    public static Schema loadSchema(String filePath) {
        return schemaLoader().load(filePath);
    }

    public static Schema loadSchema(InputStream in) {
        return schemaLoader().load(in);
    }

    public static AvroSchemaLoader schemaLoader() {
        return new AvroSchemaLoader();
    }


    /**
     * Compiles a schema for the DataFrame and stores schema and data in the provided Avro file.
     */
    public static void save(DataFrame df, Path filePath) {
        saver().save(df, filePath);
    }

    /**
     * Compiles a schema for the DataFrame and stores schema and data in the provided Avro file.
     */
    public static void save(DataFrame df, String filePath) {
        saver().save(df, filePath);
    }

    /**
     * Compiles a schema for the DataFrame and stores schema and data in the provided Avro file.
     */
    public static void save(DataFrame df, File file) {
        saver().save(df, file);
    }

    /**
     * Compiles a schema for the DataFrame and stores schema and data in the provided stream in Avro format.
     */
    public static void save(DataFrame df, OutputStream out) {
        saver().save(df, out);
    }

    public static AvroSaver saver() {
        return new AvroSaver();
    }

    public static void saveSchema(DataFrame df, Path filePath) {
        schemaSaver().save(df, filePath);
    }

    public static void saveSchema(DataFrame df, String filePath) {
        schemaSaver().save(df, filePath);
    }

    public static void saveSchema(DataFrame df, File file) {
        schemaSaver().save(df, file);
    }

    public static void saveSchema(DataFrame df, OutputStream out) {
        schemaSaver().save(df, out);
    }

    public static void saveSchema(Schema schema, String filePath) {
        schemaSaver().save(schema, filePath);
    }


    public static void saveSchema(Schema schema, Path filePath) {
        schemaSaver().save(schema, filePath);
    }

    public static void saveSchema(Schema schema, File file) {
        schemaSaver().save(schema, file);
    }

    public static void saveSchema(Schema schema, OutputStream out) {
        schemaSaver().save(schema, out);
    }

    public static AvroSchemaSaver schemaSaver() {
        return new AvroSchemaSaver();
    }
}
