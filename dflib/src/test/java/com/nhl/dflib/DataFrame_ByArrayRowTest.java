package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class DataFrame_ByArrayRowTest {

    @Test
    public void test() {

        List<Object[]> data = List.of(new Object[]{"L1", -1}, new Object[]{"L2", -2});

        DataFrame df = DataFrame
                .byArrayRow(
                        Extractor.$col(a -> a[0]),
                        Extractor.$int(a -> (Integer) a[1])
                )
                .columnNames("o", "i")
                .appender()
                .append("a", 1)
                .append("b", 2)
                .append(data)
                .toDataFrame();

        new DataFrameAsserts(df, "o", "i").expectHeight(4)

                .expectIntColumns("i")

                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "L1", -1)
                .expectRow(3, "L2", -2);
    }

    @Test
    public void testImplicitExtractors() {

        List<Object[]> data = List.of(new Object[]{"L1", -1}, new Object[]{"L2", -2});

        DataFrame df = DataFrame
                .byArrayRow("o", "i")
                .appender()
                .append("a", 1)
                .append("b", 2)
                .append(data)
                .toDataFrame();

        new DataFrameAsserts(df, "o", "i").expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "L1", -1)
                .expectRow(3, "L2", -2);
    }

    @Test
    public void testSample() {

        // fixed seed for predictable results
        Random rnd = new Random(1L);

        List<Object[]> data = List.of(new Object[]{"L1", -1}, new Object[]{"L2", -2});

        DataFrame df = DataFrame
                .byArrayRow(
                        Extractor.$col(a -> a[0]),
                        Extractor.$int(a -> (Integer) a[1])
                )
                .columnNames("o", "i")
                .sampleRows(2, rnd)
                .appender()
                .append("a", 1)
                .append("b", 2)
                .append(data)
                .toDataFrame();

        new DataFrameAsserts(df, "o", "i").expectHeight(2)
                .expectIntColumns("i")
                .expectRow(0, "b", 2)
                .expectRow(1, "L2", -2);
    }
}
