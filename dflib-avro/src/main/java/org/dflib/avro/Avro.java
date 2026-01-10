package org.dflib.avro;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
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
     * @deprecated in favor of {@link AvroTypeExtensions#registerLogicalType(LogicalType)} and {@link AvroTypeExtensions#registerConversion(Conversion)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void registerCustomType(SingleSchemaConversion<?> conversion) {
        AvroTypeExtensions.registerLogicalType(conversion.getLogicalType());
        AvroTypeExtensions.registerConversion(conversion);
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


    /**
     * @deprecated use {@link Avro#schemaLoader()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Schema loadSchema(Path path) {
        return schemaLoader().loadFromJson(path);
    }

    /**
     * @deprecated use {@link Avro#schemaLoader()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Schema loadSchema(File file) {
        return schemaLoader().loadFromJson(file);
    }

    /**
     * @deprecated use {@link Avro#schemaLoader()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static Schema loadSchema(String filePath) {
        return schemaLoader().loadFromJson(filePath);
    }

    /**
     * @deprecated use {@link Avro#schemaLoader()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
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

    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(DataFrame df, Path filePath) {
        schemaSaver().save(df, filePath);
    }

    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(DataFrame df, String filePath) {
        schemaSaver().save(df, filePath);
    }

    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(DataFrame df, File file) {
        schemaSaver().save(df, file);
    }

    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(DataFrame df, OutputStream out) {
        schemaSaver().save(df, out);
    }

    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(Schema schema, String filePath) {
        schemaSaver().save(schema, filePath);
    }


    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(Schema schema, Path filePath) {
        schemaSaver().save(schema, filePath);
    }

    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(Schema schema, File file) {
        schemaSaver().save(schema, file);
    }

    /**
     * @deprecated use {@link Avro#schemaSaver()}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static void saveSchema(Schema schema, OutputStream out) {
        schemaSaver().save(schema, out);
    }

    public static AvroSchemaSaver schemaSaver() {
        return new AvroSchemaSaver();
    }
}
