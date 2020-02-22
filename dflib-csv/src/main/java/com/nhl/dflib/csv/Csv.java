package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;

import java.io.File;
import java.io.Reader;

public class Csv {

    public static DataFrame load(File file) {
        return loader().load(file);
    }

    public static DataFrame load(String filePath) {
        return loader().load(filePath);
    }

    /**
     * @since 0.8
     */
    public static DataFrame load(Reader reader) {
        return loader().load(reader);
    }

    public static CsvLoader loader() {
        return new CsvLoader();
    }

    public static void save(DataFrame df, String filePath) {
        saver().save(df, filePath);
    }

    public static void save(DataFrame df, File file) {
        saver().save(df, file);
    }

    /**
     * @since 0.8
     */
    public static void save(DataFrame df, Appendable out) {
        saver().save(df, out);
    }

    public static CsvSaver saver() {
        return new CsvSaver();
    }
}
