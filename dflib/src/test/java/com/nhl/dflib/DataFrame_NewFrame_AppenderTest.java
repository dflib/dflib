package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_NewFrame_AppenderTest {

    @Test
    public void testObjectSource() {

        List<From> data = List.of(new From("L1", -1), new From("L2", -2));

        DataFrame df = DataFrame
                .newFrame("o", "i", "l", "d", "b")
                .extractWith(
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

        new DataFrameAsserts(df, "o", "i", "l", "d", "b").expectHeight(5)

                .expectIntColumns("i")
                .expectLongColumns("l")
                .expectDoubleColumns("d")
                .expectBooleanColumns("b")

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
                .newFrame("o", "i")
                .extractWith(
                        Extractor.$col(From::getS),
                        Extractor.$int(From::getI)
                )
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
                .newFrame("a", "b")
                .extractWith()
                .appendData());
    }

    @Test
    public void testArraySource() {

        List<Object[]> data = List.of(new Object[]{"L1", -1}, new Object[]{"L2", -2});

        DataFrame df = DataFrame
                .newFrame("o", "i")
                .extractArrayWith(
                        Extractor.$col(a -> a[0]),
                        Extractor.$int(a -> (Integer) a[1])
                )
                .appendData()
                .append("a", 1)
                .append("b", 2)
                .append(data)
                .build();

        new DataFrameAsserts(df, "o", "i").expectHeight(4)

                .expectIntColumns("i")

                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "L1", -1)
                .expectRow(3, "L2", -2);
    }

    @Test
    public void testArraySource_ImplicitExtractors() {

        List<Object[]> data = List.of(new Object[]{"L1", -1}, new Object[]{"L2", -2});

        DataFrame df = DataFrame
                .newFrame("o", "i")
                .extractArray()
                .appendData()
                .append("a", 1)
                .append("b", 2)
                .append(data)
                .build();

        new DataFrameAsserts(df, "o", "i").expectHeight(4)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "L1", -1)
                .expectRow(3, "L2", -2);
    }

    @Test
    public void testArraySource_Sample() {

        // fixed seed for predictable results
        Random rnd = new Random(1L);

        List<Object[]> data = List.of(new Object[]{"L1", -1}, new Object[]{"L2", -2});

        DataFrame df = DataFrame
                .newFrame("o", "i")
                .extractArrayWith(
                        Extractor.$col(a -> a[0]),
                        Extractor.$int(a -> (Integer) a[1])
                )
                .sampleRows(2, rnd)
                .appendData()
                .append("a", 1)
                .append("b", 2)
                .append(data)
                .build();

        new DataFrameAsserts(df, "o", "i").expectHeight(2)
                .expectIntColumns("i")
                .expectRow(0, "b", 2)
                .expectRow(1, "L2", -2);
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
