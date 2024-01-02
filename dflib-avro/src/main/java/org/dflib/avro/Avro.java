package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.avro.schema.AvroSchemaCompiler;
import org.dflib.avro.types.SingleSchemaConversion;
import org.dflib.avro.types.SingletonLogicalTypeFactory;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * @since 0.11
 */
public class Avro {

    /**
     * An extension point to register a conversion for a custom type mapped to an underlying Avro simple type.
     * DFLib already provides a collection of type extensions to map various common value types to Avro. This
     * method allows to cover the types that are not (yet) included in DFLib.
     *
     * @param conversion a custom subclass of {@link SingleSchemaConversion} implementing custom type read/write logic.
     */
    public static void registerCustomType(SingleSchemaConversion<?> conversion) {
        registerCustomLogicalType(conversion.getLogicalType());
        GenericData.get().addLogicalTypeConversion(conversion);
    }

    protected static void registerCustomLogicalType(LogicalType logicalType) {
        LogicalTypes.LogicalTypeFactory typeFactory = new SingletonLogicalTypeFactory(logicalType);
        LogicalTypes.register(logicalType.getName(), typeFactory);
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
