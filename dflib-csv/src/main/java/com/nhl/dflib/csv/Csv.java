package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;

public class Csv {

    public static DataFrame fromFile(String filePath) {
        return loader().fromFile(filePath);
    }

    public static CsvLoader loader() {
        return new CsvLoader();
    }
}
