package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import org.apache.commons.csv.CSVFormat;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class Csv {

    public static DataFrame fromFile(String filePath) {
        return new CsvReader(CSVFormat.DEFAULT, () -> readerFromFilePath(filePath)).load();
    }

    private static Reader readerFromFilePath(String path) {
        try {
            return new FileReader(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + path, e);
        }
    }
}
