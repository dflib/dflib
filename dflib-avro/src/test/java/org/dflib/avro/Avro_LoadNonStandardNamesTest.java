package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Avro_LoadNonStandardNamesTest {

    @TempDir
    static Path outBase;

    static final Schema SCHEMA = new Schema.Parser().parse("""
            { "type":"record",
              "name":"Custom",
              "namespace":"org.example",
              "fields":[
                {"name":"s1","type":"string"},
                {"name":"s2","type":"int"}
              ]}""");

    public record R1(String s1, int s2) {
    }

    static File createAvroFile() throws IOException {

        File out = new File(outBase.toFile(), "names.avro");
        List<R1> list = List.of(
                new R1("aaa", -5),
                new R1("bbb", 6)
        );

        var datumWriter = new GenericDatumWriter<GenericRecord>(SCHEMA, new GenericData());

        try (var fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(SCHEMA, out);

            for (var u : list) {
                GenericRecord r = new GenericData.Record(SCHEMA);
                r.put("s1", u.s1());
                r.put("s2", u.s2());
                fileWriter.append(r);
            }
        }

        return out;
    }

    @Test
    public void load() throws IOException {
        File f = createAvroFile();
        DataFrame df = Avro.load(f);

        new DataFrameAsserts(df, "s1", "s2")
                .expectHeight(2)
                .expectRow(0, "aaa", -5)
                .expectRow(1, "bbb", 6);
    }
}
