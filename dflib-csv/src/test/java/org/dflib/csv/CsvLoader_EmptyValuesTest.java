package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Quote;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvLoader_EmptyValuesTest {

    static final ByteSource EMPTY_WHOLES = ByteSource.of("""
            One,Two
            ,3
            5,""".getBytes());

    static final ByteSource EMPTY_DECIMALS = ByteSource.of("""
            One,Two
            ,3.1
            5.2002,""".getBytes());

    static final ByteSource EMPTY_STRINGS = ByteSource.of("""
            One,Two
            "",three
            five,""".getBytes());

    @Test
    public void emptyString() {
        DataFrame df = new CsvLoader().load(EMPTY_STRINGS);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, "", "three")
                .expectRow(1, "five", "");
    }

    @Test
    public void nullString_MandatoryQuotes() {
        String csv = """
                "One","Two","Three"
                "a",NULL,"NULL"
                NULL, "b",NULL
                ,"c",
                "d","e",NULL""";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().quote(Quote.of('"')).build())
                .nullString("NULL")
                .load(ByteSource.of(csv.getBytes()));

        // unquoted nulls are allowed with mandatory quotes, as quoted values are never null
        new DataFrameAsserts(df, "One", "Two", "Three")
                .expectHeight(4)
                .expectRow(0, "a", null, "NULL")
                .expectRow(1, null, "b", null)
                .expectRow(2, "", "c", "")
                .expectRow(3, "d", "e", null);
    }

    @Test
    public void nullString_MandatoryQuotes_TabDelimiter() {
        String csv = "\"One\"\t\"Two\"\t\"Three\"\n"
                + "\"a\"\t\t\"c\"\n"
                + "\t\"b\"\t\n"
                + "NULL\t\t\"NULL\"";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().delimiter("\t").quote(Quote.of('"')).build())
                .nullString("NULL")
                .load(ByteSource.of(csv.getBytes()));

        new DataFrameAsserts(df, "One", "Two", "Three")
                .expectHeight(3)
                .expectRow(0, "a", "", "c")
                .expectRow(1, "", "b", "")
                .expectRow(2, null, "", "NULL");
    }

    @Test
    public void mandatoryQuotes_UnquotedWhitespaceAtEndOfFile() {
        CsvFormat format = CsvFormat.defaultFormat().quote(Quote.of('"')).build();

        // an unquoted whitespace-only value should be read the same way regardless of what follows it
        for (String end : new String[]{"", "\n", "\n\"y\",\"z\""}) {
            DataFrame df = new CsvLoader()
                    .format(format)
                    .load(ByteSource.of(("\"A\",\"B\"\n\"x\",  " + end).getBytes()));

            assertEquals("x", df.getColumn("A").get(0), end);
            assertEquals("  ", df.getColumn("B").get(0), end);
        }
    }

    @Test
    public void nullStringSingleChar() {
        String csv = """
                One,Two
                ":",three
                five,:""";

        DataFrame df = new CsvLoader()
                .nullString(":")
                .load(ByteSource.of(csv.getBytes()));

        // quoted values are never null
        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, ":", "three")
                .expectRow(1, "five", null);
    }

    @Test
    void nullStringMultiChar() {
        String csv = """
                A,B
                1,NULL
                NULL,4
                """;

        DataFrame df = new CsvLoader()
                .nullString("NULL")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", null)
                .expectRow(1, null, "4");
    }

    @Test
    public void emptyStringIsNull() {
        DataFrame df = new CsvLoader()
                .emptyStringIsNull()
                .load(EMPTY_STRINGS);

        // quoted values are never null
        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, "", "three")
                .expectRow(1, "five", null);
    }

    @Test
    public void emptyInt_Throws() {
        CsvLoader loader = new CsvLoader()
                .intCol(0)
                .intCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(EMPTY_WHOLES));
    }

    @Test
    public void emptyInt_Default() {
        DataFrame df = new CsvLoader()
                .intCol(0, -100)
                .intCol(1, -200)
                .load(EMPTY_WHOLES);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectIntColumns(0, 1)
                .expectRow(0, -100, 3)
                .expectRow(1, 5, -200);
    }

    @Test
    public void emptyLong_Throws() {
        CsvLoader loader = new CsvLoader()
                .longCol(0)
                .longCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(EMPTY_WHOLES));
    }

    @Test
    public void emptyLong_Default() {
        DataFrame df = new CsvLoader()
                .longCol(0, -100L)
                .longCol(1, -200L)
                .load(EMPTY_WHOLES);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectLongColumns(0, 1)
                .expectRow(0, -100L, 3L)
                .expectRow(1, 5L, -200L);
    }

    @Test
    public void doubleCol_Nulls_Default() {
        DataFrame df = new CsvLoader()
                .doubleCol(0, -1.1)
                .doubleCol(1, -3.14)
                .load(EMPTY_DECIMALS);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1.1, 3.1)
                .expectRow(1, 5.2002, -3.14);
    }

    @Test
    public void decimalCol() {
        DataFrame df = new CsvLoader()
                .decimalCol(0)
                .decimalCol(1)
                .load(EMPTY_DECIMALS);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, null, new BigDecimal("3.1"))
                .expectRow(1, new BigDecimal("5.2002"), null);
    }

}
