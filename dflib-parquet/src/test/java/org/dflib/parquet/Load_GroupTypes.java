package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class Load_GroupTypes {

    // per https://parquet.apache.org/docs/file-format/types/logicaltypes/
    // TODO: VARIANT

    record RLS(List<String> ls) {
    }

    record RLD(List<Double> ls) {
    }

    record RMSD(Map<String, Double> map) {
    }

    @TempDir
    static Path outBase;

    static TestWriter<RLS> listOfStringsWriter() {
        return TestWriter.of(RLS.class, outBase)
                .schema("""
                        message test_schema {
                            required group ls (LIST) {
                                repeated group list {
                                    optional binary element (STRING);
                                }
                            }
                        }""")
                .writer((c, r) -> {
                    c.startMessage();
                    c.startField("ls", 0);
                    c.startGroup();

                    if (r.ls() != null) {

                        c.startField("list", 0);

                        for (String s : r.ls()) {
                            c.startGroup();

                            if (s != null) {
                                c.startField("element", 0);
                                c.addBinary(Binary.fromString(s));
                                c.endField("element", 0);
                            }
                            c.endGroup();
                        }

                        c.endField("list", 0);
                    }

                    c.endGroup();
                    c.endField("ls", 0);
                    c.endMessage();
                });
    }

    static TestWriter<RLD> listOfDoublesWriter() {
        return TestWriter.of(RLD.class, outBase)
                .schema("""
                        message test_schema {
                            required group ls (LIST) {
                                repeated group list {
                                    optional double element;
                                }
                            }
                        }""")
                .writer((c, r) -> {
                    c.startMessage();
                    c.startField("ls", 0);
                    c.startGroup();

                    if (r.ls() != null) {

                        c.startField("list", 0);

                        for (Double d : r.ls()) {
                            c.startGroup();

                            if (d != null) {
                                c.startField("element", 0);
                                c.addDouble(d);
                                c.endField("element", 0);
                            }
                            c.endGroup();
                        }

                        c.endField("list", 0);
                    }

                    c.endGroup();
                    c.endField("ls", 0);
                    c.endMessage();
                });
    }

    static TestWriter<RMSD> mapOfDoublesWriter() {
        return TestWriter.of(RMSD.class, outBase)
                .schema("""
                        message test_schema {
                            required group mp (MAP) {
                                repeated group key_value {
                                    required binary key (STRING);
                                    optional double value;
                                }
                            }
                        }""")
                .writer((c, r) -> {
                    c.startMessage();
                    c.startField("mp", 0);
                    c.startGroup();

                    if (r.map() != null) {
                        c.startField("key_value", 0);

                        for (Map.Entry<String, Double> e : r.map().entrySet()) {
                            c.startGroup();
                            c.startField("key", 0);
                            c.addBinary(Binary.fromString(e.getKey()));
                            c.endField("key", 0);

                            if (e.getValue() != null) {
                                c.startField("value", 1);
                                c.addDouble(e.getValue());
                                c.endField("value", 1);
                            }
                            c.endGroup();
                        }
                        c.endField("key_value", 0);
                    }

                    c.endGroup();
                    c.endField("mp", 0);
                    c.endMessage();
                });
    }

    @Test
    public void listOfStrings() {

        Path p = listOfStringsWriter().write(new RLS(List.of("s1", "s2")), new RLS(List.of("s3")));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "ls")
                .expectHeight(2)
                .expectRow(0, List.of("s1", "s2"))
                .expectRow(1, List.of("s3"));
    }

    @Test
    public void listOfStrings_Nulls() {
        Path p = listOfStringsWriter().write(new RLS(asList(null, "s2", null)), new RLS(Collections.singletonList(null)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "ls")
                .expectHeight(2)
                .expectRow(0, asList(null, "s2", null))
                .expectRow(1, Collections.singletonList(null));
    }

    @Test
    public void nullListOfStrings() {
        Path p = listOfStringsWriter().write(new RLS(asList("s1", "s2", "s3")), new RLS(null));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "ls")
                .expectHeight(2)
                .expectRow(0, List.of("s1", "s2", "s3"))
                .expectRow(1, List.of());
    }

    @Test
    public void listOfDoubles() {

        Path p = listOfDoublesWriter().write(new RLD(List.of(8.0, 5.1, -0.9)), new RLD(List.of(6.5)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "ls")
                .expectHeight(2)
                .expectRow(0, List.of(8.0, 5.1, -0.9))
                .expectRow(1, List.of(6.5));
    }

    @Test
    public void mapOfDoubles() {

        Path p = mapOfDoublesWriter().write(new RMSD(Map.of("s1", 8.0, "s2", -9.88)), new RMSD(Map.of("s3", 3.2)));

        DataFrame df = Parquet.load(p);

        new DataFrameAsserts(df, "mp")
                .expectHeight(2)
                .expectRow(0, Map.of("s1", 8.0, "s2", -9.88))
                .expectRow(1, Map.of("s3", 3.2));
    }
}
