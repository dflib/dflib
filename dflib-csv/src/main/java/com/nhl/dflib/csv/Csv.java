package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;

import java.io.File;

public class Csv {

    public static DataFrame load(File file) {
        return loader().load(file);
    }


    public static DataFrame load(String filePath) {
        return loader().load(filePath);
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

    public static CsvSaver saver() {
        return new CsvSaver();
    }
}
