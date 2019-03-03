package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;

import java.io.File;

public class Csv {

    public static DataFrame fromFile(String filePath) {
        return loader().fromFile(filePath);
    }

    public static CsvLoader loader() {
        return new CsvLoader();
    }

    public static void toFile(DataFrame df, String filePath) {
        saver().toFile(df, filePath);
    }

    public static void toFile(DataFrame df, File file) {
        saver().toFile(df, file);
    }

    public static CsvSaver saver() {
        return new CsvSaver();
    }
}
