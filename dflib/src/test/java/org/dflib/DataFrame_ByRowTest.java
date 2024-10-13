package org.dflib;

import org.dflib.series.SingleValueSeries;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.*;

public class DataFrame_ByRowTest {

    @Test
    public void objectSource() {

        List<From> data = List.of(new From("L1", -1), new From("L2", -2));

        DataFrame df = DataFrame
                .byRow(
                        Extractor.$col(From::getS),
                        Extractor.$int(From::getI)
                )
                .columnNames("o", "i")
                .ofIterable(data);

        new DataFrameAsserts(df, "o", "i").expectHeight(2)
                .expectIntColumns("i")
                .expectRow(0, "L1", -1)
                .expectRow(1, "L2", -2);
    }

    @Test
    public void objectSource_WithAppender() {

        List<From> data = List.of(new From("L1", -1), new From("L2", -2));

        DataFrame df = DataFrame
                .byRow(
                        Extractor.$col(From::getS),
                        Extractor.$int(From::getI),
                        Extractor.$long(From::getL),
                        Extractor.$float(From::getF),
                        Extractor.$double(From::getD),
                        Extractor.$bool(From::isB)
                )
                .appender()
                .append(new From("a", 1))
                .append(new From("b", 2))
                .append(new From("c", 3))
                .append(data)
                .toDataFrame();

        new DataFrameAsserts(df, "0", "1", "2", "3", "4", "5").expectHeight(5)

                .expectIntColumns("1")
                .expectLongColumns("2")
                .expectFloatColumns("3")
                .expectDoubleColumns("4")
                .expectBooleanColumns("5")

                .expectRow(0, "a", 1, 10_000_000_001L, 1.12f, 1.01, false)
                .expectRow(1, "b", 2, 10_000_000_002L, 2.12f, 2.01, true)
                .expectRow(2, "c", 3, 10_000_000_003L, 3.12f, 3.01, false)
                .expectRow(3, "L1", -1, 9_999_999_999L, -0.88f, -0.99, false)
                .expectRow(4, "L2", -2, 9_999_999_998L, -1.88f, -1.99, true);
    }

    @Test
    public void objectSource_WithVal() {

        List<From> data = List.of(new From("L1", -1), new From("L2", -2));

        DataFrame df = DataFrame
                .byRow(
                        Extractor.$val("const"),
                        Extractor.$col(From::getS)
                )
                .appender()
                .append(new From("a", 1))
                .append(new From("b", 2))
                .append(new From("c", 3))
                .append(data)
                .toDataFrame();

        new DataFrameAsserts(df, "0", "1").expectHeight(5)
                .expectRow(0, "const", "a")
                .expectRow(1, "const", "b")
                .expectRow(2, "const", "c")
                .expectRow(3, "const", "L1")
                .expectRow(4, "const", "L2");

        assertInstanceOf(SingleValueSeries.class, df.getColumn(0));
    }

    @Test
    public void noExtractors() {
        assertThrows(IllegalArgumentException.class, () -> DataFrame
                .byRow()
                .columnNames("a", "b")
                .appender());
    }

    @Test
    public void selectRows() {

        DataFrame df = DataFrame
                .byRow(
                        Extractor.$col(From::getS),
                        Extractor.$int(From::getI)
                )
                .selectRows(r -> r.get("0").toString().startsWith("a"))
                .appender()
                .append(new From("a", 1))
                .append(new From("ab", 2))
                .append(new From("ca", 3))
                .toDataFrame();

        new DataFrameAsserts(df, "0", "1").expectHeight(2)

                .expectIntColumns("1")

                .expectRow(0, "a", 1)
                .expectRow(1, "ab", 2);
    }

    @Test
    public void noCompaction() {

        List<From> data = List.of(
                new From(new String("L1"), -1000),
                new From(new String("L2"), -1000),
                new From(new String("L2"), -2001));

        DataFrame df = DataFrame
                .byRow(
                        Extractor.$col(From::getS),
                        Extractor.$col(From::getI)
                )
                .columnNames("o", "i")
                .ofIterable(data);

        new DataFrameAsserts(df, "o", "i").expectHeight(3)
                .expectRow(0, "L1", -1000)
                .expectRow(1, "L2", -1000)
                .expectRow(2, "L2", -2001);

        DataFrame idCardinality = df.cols().select(
                $col("o").mapVal(System::identityHashCode),
                $col("i").mapVal(System::identityHashCode));

        assertEquals(3, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void compaction() {

        List<From> data = List.of(
                new From(new String("L1"), -1000),
                new From(new String("L2"), -1000),
                new From(new String("L2"), -2001));

        DataFrame df = DataFrame
                .byRow(
                        Extractor.$col(From::getS).compact(),
                        Extractor.$col(From::getI).compact()
                )
                .columnNames("o", "i")
                .ofIterable(data);

        new DataFrameAsserts(df, "o", "i").expectHeight(3)
                .expectRow(0, "L1", -1000)
                .expectRow(1, "L2", -1000)
                .expectRow(2, "L2", -2001);

        DataFrame idCardinality = df.cols().select(
                $col("o").mapVal(System::identityHashCode),
                $col("i").mapVal(System::identityHashCode));

        assertEquals(2, idCardinality.getColumn(0).unique().size());
        assertEquals(2, idCardinality.getColumn(1).unique().size());
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

        public float getF() {
            return i + 0.12f;
        }

        public long getL() {
            return i + 10_000_000_000L;
        }

        public boolean isB() {
            return i % 2 == 0;
        }
    }
}
