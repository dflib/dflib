package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;

import java.io.File;
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

    public static void save(DataFrame df, String filePath) {
        saver().save(df, filePath);
    }

    public static void save(DataFrame df, File file) {
        saver().save(df, file);
    }

    public static void save(DataFrame df, OutputStream out) {
        saver().save(df, out);
    }

    public static AvroSaver saver() {
        return new AvroSaver();
    }
}
