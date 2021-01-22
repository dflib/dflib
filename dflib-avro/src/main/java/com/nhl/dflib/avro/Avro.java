package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import org.apache.avro.Schema;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @since 0.11
 */
public class Avro {

    public static DataFrame load(File file) {
        return loader().load(file);
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


    public static void saveData(DataFrame df, String filePath) {
        dataSaver().save(df, filePath);
    }

    public static void saveData(DataFrame df, File file) {
        dataSaver().save(df, file);
    }

    public static void saveData(DataFrame df, OutputStream out) {
        dataSaver().save(df, out);
    }

    public static AvroDataSaver dataSaver() {
        return new AvroDataSaver();
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
