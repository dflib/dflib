package com.nhl.dflib.json;

/**
 * The main entry point to the code that can load DataFrames from JSON.
 *
 * @since 0.8
 */
public class Json {

    public static JsonLoader loader() {
        return new JsonLoader();
    }

    /**
     * @since 0.11
     */
    public static JsonSaver saver() {
        return new JsonSaver();
    }
}
