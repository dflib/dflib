package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;

import java.io.File;
import java.io.OutputStream;

/**
 * @since 0.11
 */
public class Avro {

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
