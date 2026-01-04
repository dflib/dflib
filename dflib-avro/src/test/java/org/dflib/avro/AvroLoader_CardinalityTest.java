package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvroLoader_CardinalityTest {

    static File _TEST1_AVRO;
    static File _TEST2_AVRO;

    @BeforeAll
    static void setupSrcDirs() throws URISyntaxException {
        _TEST1_AVRO = new File(AvroLoader_CardinalityTest.class.getResource("test1.avro").toURI()).getAbsoluteFile();
        _TEST2_AVRO = new File(AvroLoader_CardinalityTest.class.getResource("test2.avro").toURI()).getAbsoluteFile();
    }

    @Test
    public void defaultCardinality_Nulls() {
        DataFrame df = new AvroLoader().load(_TEST1_AVRO);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(6, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void defaultCardinality_noNulls() {
        DataFrame df = new AvroLoader().load(_TEST2_AVRO);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));


        assertEquals(4, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void compactCols_All() {
        DataFrame df = new AvroLoader().compactCols().load(_TEST1_AVRO);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void compactCols_Name() {
        DataFrame df = new AvroLoader()
                .compactCols("a", "b")
                .load(_TEST1_AVRO);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void compactCols_Pos_Nulls() {
        DataFrame df = new AvroLoader()
                .compactCols(0, 1)
                .load(_TEST1_AVRO);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("a").mapVal(System::identityHashCode),
                $col("b").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }
}
