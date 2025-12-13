package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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

    static File createAvroFile(String schema) throws IOException {
        Schema parsed = new Schema.Parser().parse(schema);

        File out = new File(outBase.toFile(), "names.avro");
        List<R1> list = List.of(
                new R1("aaa", -5),
                new R1("bbb", 6)
        );

        var datumWriter = new GenericDatumWriter<GenericRecord>(parsed, new GenericData());

        try (var fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(parsed, out);

            for (var u : list) {
                GenericRecord r = new GenericData.Record(parsed);
                r.put("s1", u.s1());
                r.put("s2", u.s2());
                fileWriter.append(r);
            }
        }

        return out;
    }

    @ParameterizedTest
    @ValueSource(strings = {SCHEMA1, SCHEMA2})
    public void load(String schema) throws IOException {
        File f = createAvroFile(schema);
        DataFrame df = Avro.load(f);

        new DataFrameAsserts(df, "s1", "s2")
                .expectHeight(2)
                .expectRow(0, "aaa", -5)
                .expectRow(1, "bbb", 6);
    }
}
