package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Avro_FileTest {

    @TempDir
    static File destination;

    @Test
    public void saveLoad_File_Empty() {
        File file = new File(destination, "testSaveLoad_File_Empty.avro");
        DataFrame empty = DataFrame.empty("a", "b");
        Avro.save(empty, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        DataFrame loaded = Avro.load(file);
        assertNotNull(loaded);
        new DataFrameAsserts(loaded, empty.getColumnsIndex()).expectHeight(0);
    }

    @Test
    public void saveLoad_File() {
        File file = new File(destination, "testSaveLoad_File.avro");

        DataFrame df = DataFrame.byArrayRow("a", "b")
                .appender()
                .append(1, 2)
                .append(3, 4)
                .toDataFrame();

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
