package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Path;

public class LoadCustomRecordNameTest {

    @TempDir
    static Path outBase;

    // custom "name" and "namespace"
    static final String SCHEMA1 = """
            { "type":"record",
              "name":"Custom",
              "namespace":"org.example",
              "fields":[
                {"name":"s1","type":"string"},
                {"name":"s2","type":"int"}
              ]}""";

    // custom "name", no "namespace"
    static final String SCHEMA2 = """
            { "type":"record",
              "name":"Custom",
              "fields":[
                {"name":"s1","type":"string"},
                {"name":"s2","type":"int"}
              ]}""";

    public record R1(String s1, int s2) {
    }

    static Path createAvroFile(String schema) {

        return TestWriter.of(R1.class, outBase)
                .schema(schema)
                .writer((r, o) -> {
                    r.put("s1", o.s1());
                    r.put("s2", o.s2());
                })
                .write(
                        new R1("aaa", -5),
                        new R1("bbb", 6));
    }

    @ParameterizedTest
    @ValueSource(strings = {SCHEMA1, SCHEMA2})
    public void load(String schema) {
        DataFrame df = Avro.load(createAvroFile(schema));

        new DataFrameAsserts(df, "s1", "s2")
                .expectHeight(2)
                .expectRow(0, "aaa", -5)
                .expectRow(1, "bbb", 6);
    }
}
