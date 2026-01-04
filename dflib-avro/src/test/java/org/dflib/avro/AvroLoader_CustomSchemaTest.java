package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class AvroLoader_CustomSchemaTest {

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

    static final String SUB_SCHEMA = """
            {
              "type":"record",
              "name":"DataFrame",
              "fields":[
                {"name":"a","type":"int"},
                {"name":"c","type":"int"}
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
    public void cols() {
        DataFrame df = Avro.loader()
                .schema(Avro.schemaLoader().loadFromJson(SUB_SCHEMA.getBytes()))
                .load(avroFile);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 100)
                .expectRow(1, 2, 200)
                .expectRow(2, 3, 300);
    }
}
