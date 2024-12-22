package org.dflib.parquet;

import org.apache.parquet.schema.MessageType;
import org.dflib.DataFrame;
import org.dflib.connector.ByteSource;
import org.dflib.connector.ByteSources;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;


public class Parquet {

    public static DataFrame load(File file) {
        return loader().load(file);
    }

    public static DataFrame load(Path filePath) {
        return loader().load(filePath);
    }

    public static DataFrame load(String filePath) {
        return loader().load(filePath);
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

    public static MessageType loadSchema(File file) {
        return schemaLoader().load(file);
    }

    public static MessageType loadSchema(Path filePath) {
        return schemaLoader().load(filePath);
    }

    public static MessageType loadSchema(String filePath) {
        return schemaLoader().load(filePath);
    }

    public static void save(DataFrame df, Path filePath) {
        saver().save(df, filePath);
    }

    public static void save(DataFrame df, String filePath) {
        saver().save(df, filePath);
    }

    public static void save(DataFrame df, File file) {
        saver().save(df, file);
    }

    public static ParquetSaver saver() {
        return new ParquetSaver();
    }

    public static ParquetLoader loader() {
        return new ParquetLoader();
    }

    public static ParquetSchemaLoader schemaLoader() {
        return new ParquetSchemaLoader();
    }
}
