package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_BuilderTest {

    @Test
    public void testObjectSource() {

        List<From> data = List.of(new From("L1", -1), new From("L2", -2));

        DataFrame df = DataFrame
                .builder(
                        Extractor.$col(From::getS),
                        Extractor.$int(From::getI),
                        Extractor.$long(From::getL),
                        Extractor.$double(From::getD),
                        Extractor.$bool(From::isB)
                )
                .appendData()
                .append(new From("a", 1))
                .append(new From("b", 2))
                .append(new From("c", 3))
                .append(data)
                .build();

        new DataFrameAsserts(df, "0", "1", "2", "3", "4").expectHeight(5)

                .expectIntColumns("1")
                .expectLongColumns("2")
                .expectDoubleColumns("3")
                .expectBooleanColumns("4")

                .expectRow(0, "a", 1, 10_000_000_001L, 1.01, false)
                .expectRow(1, "b", 2, 10_000_000_002L, 2.01, true)
                .expectRow(2, "c", 3, 10_000_000_003L, 3.01, false)
                .expectRow(3, "L1", -1, 9_999_999_999L, -0.99, false)
                .expectRow(4, "L2", -2, 9_999_999_998L, -1.99, true);
    }

    @Test
    public void testObjectSource_SkipExtractor() {

        List<From> data = List.of(new From("L1", -1), new From("L2", -2));

        DataFrame df = DataFrame
                .builder(
                        Extractor.$col(From::getS),
                        Extractor.$int(From::getI)
                )
                .columnNames("o", "i")
                // skip extractor creation, create the DataFrame straight from the builder
                .build(data);

        new DataFrameAsserts(df, "o", "i").expectHeight(2)
                .expectIntColumns("i")
                .expectRow(0, "L1", -1)
                .expectRow(1, "L2", -2);
    }

    @Test
    public void testNoExtractors() {
        assertThrows(IllegalArgumentException.class, () -> DataFrame
                .builder()
                .columnNames("a", "b")
                .appendData());
    }

    static class From {
        final String s;
        final int i;

        From(String s, int i) {
            this.s = s;
            this.i = i;
        }

        public String getS() {
            return s;
        }

        public int getI() {
            return i;
        }

        public double getD() {
            return i + 0.01;
        }

        public long getL() {
            return i + 10_000_000_000L;
        }

        public boolean isB() {
            return i % 2 == 0;
        }
    }
}
