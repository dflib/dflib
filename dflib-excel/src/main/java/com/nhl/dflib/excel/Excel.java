package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * An entry point to work with Excel files.
 *
 * @since 0.13
 */
public class Excel {

    public static Map<String, DataFrame> load(InputStream in) {
        return loader().load(in);
    }

    public static Map<String, DataFrame> load(File file) {
        return loader().load(file);
    }

    public static Map<String, DataFrame> load(String filePath) {
        return loader().load(filePath);
    }

    public static Map<String, DataFrame> load(Path path) {
        return loader().load(path);
    }

    public static ExcelLoader loader() {
        return new ExcelLoader();
    }
}
