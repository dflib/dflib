package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;

/**
 * @since 0.11
 */
public class Avro {

    public static void save(DataFrame df, Appendable out) {
        saver().save(df, out);
    }

    public static AvroSaver saver() {
        return new AvroSaver();
    }
}
