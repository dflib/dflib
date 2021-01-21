package com.nhl.dflib.avro;

import com.nhl.dflib.*;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AvroTest {

    @TempDir
    static File destination;

    static final DataFrame df = DataFrame.newFrame(
            "int", "Integer", "long", "Long", "double", "Double", "bool", "Bool", "String")
            .columns(
                    IntSeries.forInts(1, 2, 3),
                    Series.forData(Integer.valueOf(11), Integer.valueOf(12), null),
                    LongSeries.forLongs(Long.MAX_VALUE - 1L, Long.MIN_VALUE + 1L, 5L),
                    Series.forData(Long.valueOf(21L), Long.valueOf(22L), null),
                    DoubleSeries.forDoubles(20.12, 20.123, 20.1235),
                    Series.forData(Double.valueOf(30.1), Double.valueOf(31.45), null),
                    BooleanSeries.forBooleans(true, false, true),
                    Series.forData(Boolean.TRUE, Boolean.FALSE, null),
                    Series.forData("s1", "s2", null)
            );

    static final DataFrame empty = DataFrame.newFrame(df.getColumnsIndex()).empty();

    @Test
    public void test_File_withSchema_Empty() {
        File file = new File(destination, "test_File_withSchema_Empty.avro");
        Avro.save(empty, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        DataFrame loaded = Avro.load(file);
        assertNotNull(loaded);
        new DataFrameAsserts(loaded, empty.getColumnsIndex()).expectHeight(0);
    }

    @Test
    public void test_File_withSchema() {
        File file = new File(destination, "test_File_withSchema.avro");
        Avro.save(df, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        DataFrame loaded = Avro.load(file);
        assertNotNull(loaded);
        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(df.height())

                .expectIntColumns(0)
                .expectLongColumns(2)
                .expectDoubleColumns(4)
                .expectBooleanColumns(6)

                .expectRow(0, 1, 11, Long.MAX_VALUE - 1L, 21L, 20.12, 30.1, true, true, "s1")
                .expectRow(1, 2, 12, Long.MIN_VALUE + 1L, 22L, 20.123, 31.45, false, false, "s2")
                .expectRow(2, 3, null, 5L, null, 20.1235, null, true, null, null);
    }

    @Test
    public void testSave_toFile_excludeSchema() {
        File file = new File(destination, "testSave_toFile_excludeSchema.avro");
        new AvroSaver().excludeSchema().save(df, file);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);

        // TODO: API to load using externally saved schema
    }

    @Test
    public void testSave_toFile_excludeSchema_Empty() {
        File file = new File(destination, "testSave_toFile_excludeSchema_Empty.avro");
        new AvroSaver().excludeSchema().save(empty, file);
        assertTrue(file.exists());
        assertEquals(0, file.length());
    }
}
