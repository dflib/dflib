package org.dflib.json;

import org.dflib.DataFrame;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;

/**
 * The main entry point to the code that can load DataFrames from JSON.
 *
 * @since 0.8
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
     *
     * @since 1.0.0-RC1
     */
    public DataFrame load(String json) {
        return loader().load(json);
    }

    /**
     * Loads a DataFrame from a JSON file at the specified path.
     *
     * @since 1.0.0-RC1
     */
    public DataFrame load(Path filePath) {
        return loader().load(filePath);
    }

    /**
     * Loads a DataFrame from a JSON file.
     *
     * @since 1.0.0-RC1
     */
    public DataFrame load(File file) {
        return loader().load(file);
    }

    /**
     * Loads a DataFrame from the provided InputStream
     *
     * @since 1.0.0-RC1
     */
    public DataFrame load(InputStream in) {
        return loader().load(in);
    }

    /**
     * @since 1.0.0-RC1
     */
    public DataFrame load(Reader reader) {
        return loader().load(reader);
    }

    /**
     * @since 0.11
     */
    public static JsonSaver saver() {
        return new JsonSaver();
    }

    /**
     * @since 1.0.0-RC1
     */
    public void save(DataFrame df, Appendable out) {
        saver().save(df, out);
    }

    /**
     * @since 1.0.0-RC1
     */
    public void save(DataFrame df, File file) {
        saver().save(df, file);
    }

    /**
     * @since 1.0.0-RC1
     */
    public void save(DataFrame df, Path filePath) {
        saver().save(df, filePath);
    }

    /**
     * @since 1.0.0-RC1
     */
    public void save(DataFrame df, String fileName) {
        saver().save(df, fileName);
    }

}
