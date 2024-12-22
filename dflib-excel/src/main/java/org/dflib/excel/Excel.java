package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.ByteSource;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Map;

/**
 * An entry point to load and save Excel files.
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

    /**
     * @since 1.1.0
     */
    public static Map<String, DataFrame> load(ByteSource source) {
        return loader().load(source);
    }

    /**
     * @since 1.1.0
     */
    public static DataFrame loadSheet(ByteSource src, String sheetName) {
        return loader().loadSheet(src, sheetName);
    }

    public static DataFrame loadSheet(InputStream in, String sheetName) {
        return loader().loadSheet(in, sheetName);
    }

    public static DataFrame loadSheet(File file, String sheetName) {
        return loader().loadSheet(file, sheetName);
    }

    public static DataFrame loadSheet(String filePath, String sheetName) {
        return loader().loadSheet(filePath, sheetName);
    }

    public static DataFrame loadSheet(Path path, String sheetName) {
        return loader().loadSheet(path, sheetName);
    }

    /**
     * @since 1.1.0
     */
    public static DataFrame loadSheet(ByteSource src, int sheetNum) {
        return loader().loadSheet(src, sheetNum);
    }

    public static DataFrame loadSheet(InputStream in, int sheetNum) {
        return loader().loadSheet(in, sheetNum);
    }

    public static DataFrame loadSheet(File file, int sheetNum) {
        return loader().loadSheet(file, sheetNum);
    }

    public static DataFrame loadSheet(String filePath, int sheetNum) {
        return loader().loadSheet(filePath, sheetNum);
    }

    public static DataFrame loadSheet(Path path, int sheetNum) {
        return loader().loadSheet(path, sheetNum);
    }


    public static ExcelLoader loader() {
        return new ExcelLoader();
    }


    public static void save(Map<String, DataFrame> dfBySheet, File file) {
        saver().save(dfBySheet, file);
    }


    public static void save(Map<String, DataFrame> dfBySheet, Path filePath) {
        saver().save(dfBySheet, filePath);
    }


    public static void save(Map<String, DataFrame> dfBySheet, String fileName) {
        saver().save(dfBySheet, fileName);
    }



    public static void save(Map<String, DataFrame> dfBySheet, OutputStream out) {
        saver().save(dfBySheet, out);
    }


    public static void saveSheet(DataFrame df, File file, String sheetName) {
        saver().saveSheet(df, file, sheetName);
    }


    public static void saveSheet(DataFrame df, Path filePath, String sheetName) {
        saver().saveSheet(df, filePath, sheetName);
    }


    public static void saveSheet(DataFrame df, String fileName, String sheetName) {
        saver().saveSheet(df, fileName, sheetName);
    }



    public static void saveSheet(DataFrame df, OutputStream out, String sheetName) {
        saver().saveSheet(df, out, sheetName);
    }


    public static ExcelSaver saver() {
        return new ExcelSaver();
    }
}
