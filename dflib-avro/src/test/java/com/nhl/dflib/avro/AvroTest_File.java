package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvroTest_File {

    @TempDir
    static File destination;

    @Test
    public void testSaveLoad_File_Empty() {
        File file = new File(destination, "testSaveLoad_File_Empty.avro");
        DataFrame empty = DataFrame.newFrame("a", "b").empty();
        Avro.save(empty, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        DataFrame loaded = Avro.load(file);
        assertNotNull(loaded);
        new DataFrameAsserts(loaded, empty.getColumnsIndex()).expectHeight(0);
    }

    @Test
    public void testSaveLoad_File() {
        File file = new File(destination, "testSaveLoad_File.avro");

        DataFrame df = DataFrame.newFrame("a", "b")
                .byRow()
                .addRow(1, 2)
                .addRow(3, 4)
                .create();

        Avro.save(df, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        DataFrame loaded = Avro.load(file);
        assertNotNull(loaded);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }
}
