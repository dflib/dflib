package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.io.File;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

public class Csv {

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

    public static DataFrame load(Reader reader) {
        return loader().load(reader);
    }

    public static CsvLoader loader() {
        return new CsvLoader();
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

    public static void save(DataFrame df, Appendable out) {
        saver().save(df, out);
    }

    /**
     * @since 2.0.0
     */
    public static void save(DataFrame df, OutputStream out) {
        saver().save(df, out);
    }

    public static CsvSaver saver() {
        return new CsvSaver();
    }
}
