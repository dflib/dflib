package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Avro_Test {

    @Test
    public void saveLoad_Empty() {
        DataFrame empty = DataFrame.empty("a", "b");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Avro.save(empty, out);
        byte[] bytes = out.toByteArray();

        assertTrue(bytes.length > 0);
        DataFrame loaded = Avro.load(bytes);
        new DataFrameAsserts(loaded, empty.getColumnsIndex()).expectHeight(0);
    }
}
