package org.dflib.json;

import org.dflib.DataFrame;
import org.dflib.connector.ByteSource;
import org.dflib.connector.ByteSources;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

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
