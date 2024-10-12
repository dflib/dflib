package org.dflib.json;

import org.dflib.DataFrame;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;

/**
 * The main entry point to the code that can load and save DataFrames from/to JSON.
 */
public class Json {

    /**
     * Creates a loader object that allows customization of JSON loading process.
     */
    public static JsonLoader loader() {
        return new JsonLoader();
    }

    /**
     * Loads a DataFrame from the provided JSON String.
     */
    public DataFrame load(String json) {
        return loader().load(json);
    }

    /**
     * Loads a DataFrame from a JSON file at the specified path.
     */
    public DataFrame load(Path filePath) {
        return loader().load(filePath);
    }

    /**
     * Loads a DataFrame from a JSON file.
     */
    public DataFrame load(File file) {
        return loader().load(file);
    }

    /**
     * Loads a DataFrame from the provided InputStream
     */
    public DataFrame load(InputStream in) {
        return loader().load(in);
    }

    public DataFrame load(Reader reader) {
        return loader().load(reader);
    }

    public static JsonSaver saver() {
        return new JsonSaver();
    }

    public void save(DataFrame df, Appendable out) {
        saver().save(df, out);
    }
    
    public void save(DataFrame df, File file) {
        saver().save(df, file);
    }
    
    public void save(DataFrame df, Path filePath) {
        saver().save(df, filePath);
    }
    
    public void save(DataFrame df, String fileName) {
        saver().save(df, fileName);
    }

}
