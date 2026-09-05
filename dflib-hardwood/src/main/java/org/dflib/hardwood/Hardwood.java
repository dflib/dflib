package org.dflib.hardwood;

import dev.hardwood.schema.FileSchema;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.DataFrame;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

/**
 * Reads and writes .parquet files as DataFrames, using the
 * <a href="https://hardwood.dev/">Hardwood</a> Parquet engine. An alternative to the "dflib-parquet" module, which is
 * built on "parquet-java" and its Hadoop dependencies.
 *
 * @since 2.0.0
 */
public class Hardwood {

    // Below this many rows a file is read a row at a time, and at or above it a column at a time. Hardwood's batch
    // API decodes whole arrays, which is the shape DFLib stores its columns in, but setting up and tearing down its
    // batch readers has a fixed cost that only pays for itself on large files
    static final int MIN_BATCH_HEIGHT = 1_000_000;

    public static DataFrame load(File file) {
        return loader().load(file);
    }

    public static DataFrame load(Path filePath) {
        return loader().load(filePath);
    }

    public static DataFrame load(String filePath) {
        return loader().load(filePath);
    }

    public static DataFrame load(ByteSource src) {
        return loader().load(src);
    }

    public static Map<String, DataFrame> loadAll(ByteSources src) {
        return loader().loadAll(src);
    }

    public static FileSchema loadSchema(File file) {
        return schemaLoader().load(file);
    }

    public static FileSchema loadSchema(Path filePath) {
        return schemaLoader().load(filePath);
    }

    public static FileSchema loadSchema(String filePath) {
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

    public static HardwoodSaver saver() {
        return new HardwoodSaver();
    }

    public static HardwoodLoader loader() {
        return new HardwoodLoader(MIN_BATCH_HEIGHT);
    }

    public static HardwoodSchemaLoader schemaLoader() {
        return new HardwoodSchemaLoader();
    }
}
