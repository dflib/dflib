package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class AvroLoader_ColumnFilterTest {

    static Path avroFile;

    static final String SCHEMA = """
            {
              "type":"record",
              "name":"DataFrame",
              "fields":[
                {"name":"a","type":"int"},
                {"name":"b","type":"int"},
                {"name":"c","type":"int"},
                {"name":"d","type":"int"}
              ]
            }""";

    @BeforeAll
    static void createAvroFile(@TempDir Path outBase) {

        record R(int a, int b, int c, int d) {}

        avroFile = TestWriter.of(R.class, outBase)
                .schema(SCHEMA)
                .writer((r, o) -> {
                    r.put("a", o.a());
                    r.put("b", o.b());
                    r.put("c", o.c());
                    r.put("d", o.d());
                })
                .write(new R(1, 10, 100, 1000),new R(2, 20, 200, 2000),new R(3, 30, 300, 3000));
    }

    @Test
    public void all() {
        DataFrame df = Avro.loader().load(avroFile);
        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, 10, 100, 1000)
                .expectRow(1, 2, 20, 200, 2000)
                .expectRow(2, 3, 30, 300, 3000);
    }

    @Test
    public void cols() {
        DataFrame df = Avro.loader()
                .cols("a", "c")
                .load(avroFile);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 100)
                .expectRow(1, 2, 200)
                .expectRow(2, 3, 300);
    }

    @Test
    public void cols_reorder() {
        DataFrame df = Avro.loader()
                .cols("c", "a")
                .load(avroFile);
        new DataFrameAsserts(df, "c", "a")
                .expectHeight(3)
                .expectRow(0, 100, 1)
                .expectRow(1, 200, 2)
                .expectRow(2, 300, 3);
    }

    @Test
    public void cols_pos() {
        DataFrame df = Avro.loader()
                .cols(0, 2)
                .load(avroFile);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 100)
                .expectRow(1, 2, 200)
                .expectRow(2, 3, 300);
    }

    @Test
    public void cols_pos_reorder() {
        DataFrame df = Avro.loader()
                .cols(2, 0)
                .load(avroFile);
        new DataFrameAsserts(df, "c", "a")
                .expectHeight(3)
                .expectRow(0, 100, 1)
                .expectRow(1, 200, 2)
                .expectRow(2, 300, 3);
    }

    @Test
    public void cols_except() {
        DataFrame df = Avro.loader()
                .colsExcept("b", "d")
                .load(avroFile);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 100)
                .expectRow(1, 2, 200)
                .expectRow(2, 3, 300);
    }

    @Test
    public void cols_pos_except() {
        DataFrame df = Avro.loader()
                .colsExcept(1, 3)
                .load(avroFile);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 100)
                .expectRow(1, 2, 200)
                .expectRow(2, 3, 300);
    }
}
