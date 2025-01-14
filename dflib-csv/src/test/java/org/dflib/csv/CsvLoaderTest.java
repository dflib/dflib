package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.dflib.DataFrame;
import org.dflib.ValueMapper;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.*;

public class CsvLoaderTest extends BaseCsvTest {

    @Test
    public void notFound() {
        File file = new File("no-such-file.csv");

        try {
            new CsvLoader().load(file);
            fail("Expected exception was not thrown");
        } catch (RuntimeException e) {
            assertEquals("Error reading file: no-such-file.csv", e.getMessage());
        }
    }

    @Test
    public void fromReader() {

        StringReader r = new StringReader("A,B" + System.lineSeparator()
                + "1,2" + System.lineSeparator()
                + "3,4");

        DataFrame df = new CsvLoader().load(r);
        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void fromByteSource() {
        byte[] csvBytes = "A,b,C\n1,2,3\n4,5,6".getBytes();
        DataFrame df = new CsvLoader().load(ByteSource.of(csvBytes));

        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void fromByteSource_Encoding() {
        byte[] csvBytes = "A,b,C\n1,2,3\n4,5,6".getBytes(StandardCharsets.UTF_16BE);
        DataFrame df = new CsvLoader().encoding("UTF-16BE").load(ByteSource.of(csvBytes));

        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void fromByteSources() {
        byte[] csvBytes1 = "A,b,C\n1,2,3\n4,5,6".getBytes();
        byte[] csvBytes2 = "X,y,Z\n7,8,9\n10,11,12".getBytes();

        Map<String, DataFrame> dfs = new CsvLoader().loadAll(ByteSources.of(
                Map.of("b1", ByteSource.of(csvBytes1), "b2", ByteSource.of(csvBytes2))));

        assertEquals(2, dfs.size());

        new DataFrameAsserts(dfs.get("b1"), "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");

        new DataFrameAsserts(dfs.get("b2"), "X", "y", "Z")
                .expectHeight(2)
                .expectRow(0, "7", "8", "9")
                .expectRow(1, "10", "11", "12");
    }

    @Test
    public void fromFile() {
        DataFrame df = new CsvLoader().load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void fromFile_Encoding() {
        DataFrame df = new CsvLoader().encoding(StandardCharsets.UTF_16BE).load(inPath("f1_UTF16BE.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void fromFile_DefaultFormat_Excel() {
        DataFrame df = new CsvLoader().load(inPath("from_excel.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "commas,quotes\"'", "-85.7", "3")
                .expectRow(1, "with, commas", "5.50001", "6");
    }

    @Test
    public void fromFile_MySQLFormat() {
        DataFrame df = new CsvLoader().format(CSVFormat.MYSQL).load(inPath("from_mysql.csv"));
        new DataFrameAsserts(df, "1", "3365430", " xxxx")
                .expectHeight(4)
                .expectRow(0, "2", "2289959", "yyyy")
                .expectRow(1, "3", "3995478", "zzzzz")
                .expectRow(2, "4", "4112467", "nnn")
                .expectRow(3, "5", "1474364", "eee");
    }

    @Test
    public void fromFile_Header() {
        DataFrame df = new CsvLoader().header("X", "Y", "Z").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "X", "Y", "Z")
                .expectHeight(3)
                .expectRow(0, "A", "b", "C")
                .expectRow(1, "1", "2", "3")
                .expectRow(2, "4", "5", "6");
    }

    @Test
    public void fromFile_generateHeader() {
        DataFrame df = new CsvLoader().generateHeader().load(inPath("f1.csv"));
        new DataFrameAsserts(df, "c0", "c1", "c2")
                .expectHeight(3)
                .expectRow(0, "A", "b", "C")
                .expectRow(1, "1", "2", "3")
                .expectRow(2, "4", "5", "6");
    }

    @Test
    public void fromFile_Cols() {
        DataFrame df = new CsvLoader().cols("b", "A").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "b", "A")
                .expectHeight(2)
                .expectRow(0, "2", "1")
                .expectRow(1, "5", "4");
    }

    @Test
    public void fromFile_Cols_Pos() {
        DataFrame df = new CsvLoader().cols(1, 0).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "b", "A")
                .expectHeight(2)
                .expectRow(0, "2", "1")
                .expectRow(1, "5", "4");
    }

    @Test
    public void fromFile_Cols_Header() {
        DataFrame df = new CsvLoader().header("X", "Y", "Z").cols("Y", "X").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "Y", "X")
                .expectHeight(3)
                .expectRow(0, "b", "A")
                .expectRow(1, "2", "1")
                .expectRow(2, "5", "4");
    }

    @Test
    public void fromFile_Cols_generateHeader() {
        DataFrame df = new CsvLoader().generateHeader().cols("c1", "c0").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "c1", "c0")
                .expectHeight(3)
                .expectRow(0, "b", "A")
                .expectRow(1, "2", "1")
                .expectRow(2, "5", "4");
    }

    @Test
    public void fromFile_ColsPos_generateHeader() {
        DataFrame df = new CsvLoader().generateHeader().cols(1, 0).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "c1", "c0")
                .expectHeight(3)
                .expectRow(0, "b", "A")
                .expectRow(1, "2", "1")
                .expectRow(2, "5", "4");
    }

    @Test
    public void fromFile_ColsExcept() {
        DataFrame df = new CsvLoader().colsExcept("b").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, "1", "3")
                .expectRow(1, "4", "6");
    }

    @Test
    public void fromFile_ColsExcept_Pos() {
        DataFrame df = new CsvLoader().colsExcept(1).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, "1", "3")
                .expectRow(1, "4", "6");
    }

    @Test
    public void fromFile_Limit() {
        DataFrame df = new CsvLoader().limit(1).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(1)
                .expectRow(0, "1", "2", "3");
    }

    @Test
    public void fromFile_Limit_Header() {
        DataFrame df = new CsvLoader()
                .header("C0", "C1", "C2")
                .limit(2)
                .load(inPath("f1.csv"));

        new DataFrameAsserts(df, "C0", "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "A", "b", "C")
                .expectRow(1, "1", "2", "3");
    }

    @Test
    public void fromFile_Offset() {
        DataFrame df = new CsvLoader().offset(1).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "1", "2", "3")
                .expectHeight(1)
                .expectRow(0, "4", "5", "6");
    }

    @Test
    public void fromFile_Offset_Limit() {
        DataFrame df = new CsvLoader()
                .generateHeader()
                .offset(1)
                .limit(1)
                .load(inPath("f1.csv"));

        new DataFrameAsserts(df, "c0", "c1", "c2")
                .expectHeight(1)
                .expectRow(0, "1", "2", "3");
    }

    @Test
    public void fromFile_col() {
        DataFrame df = new CsvLoader()
                .col(0, ValueMapper.stringToInt())
                .col(2, ValueMapper.stringToLong())
                .load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1, "2", 3L)
                .expectRow(1, 4, "5", 6L);
    }

    @Test
    public void fromFile_colType_Override() {
        DataFrame df = new CsvLoader()
                .col(0, ValueMapper.stringToInt())
                .col("A", ValueMapper.stringToLong())
                .load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1L, "2", "3")
                .expectRow(1, 4L, "5", "6");
    }

    @Test
    public void fromFile_NumColumn1() {
        DataFrame df = new CsvLoader()
                .numCol(0, Integer.class)
                .numCol("b", Long.class)
                .numCol("C", Double.class)
                .load(inPath("f1.csv"));

        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1, 2L, 3.)
                .expectRow(1, 4, 5L, 6.);
    }

    @Test
    public void fromFile_NumColumn2() {
        DataFrame df = new CsvLoader()
                .numCol(0, Float.class)
                .numCol("b", BigDecimal.class)
                .numCol("C", BigInteger.class)
                .load(inPath("f1.csv"));

        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1.f, new BigDecimal(2), new BigInteger("3"))
                .expectRow(1, 4.f, new BigDecimal(5), new BigInteger("6"));
    }

    @Test
    public void fromFile_NumColumn_PartialColumns() {

        DataFrame df = new CsvLoader()
                .intCol("A")
                .intCol("C")
                .cols("C", "A")
                .load(inPath("f1.csv"));

        new DataFrameAsserts(df, "C", "A")
                .expectHeight(2)
                .expectRow(0, 3, 1)
                .expectRow(1, 6, 4);
    }

    @Test
    public void fromFile_IntCol_Nulls() {
        CsvLoader loader = new CsvLoader()
                .intCol(0)
                .intCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(inPath("numbers_w_nulls.csv")));
    }

    @Test
    public void fromFile_IntCol_Nulls_Default() {
        DataFrame df = new CsvLoader()
                .intCol(0, -100)
                .intCol(1, -200)
                .load(inPath("numbers_w_nulls.csv"));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectIntColumns(0, 1)
                .expectRow(0, -100, 3)
                .expectRow(1, 5, -200);
    }

    @Test
    public void fromFile_IntCol_Nulls_Throw() {
        CsvLoader loader = new CsvLoader()
                .intCol(0)
                .intCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(inPath("numbers_w_nulls.csv")));

    }

    @Test
    public void fromFile_LongCol_Nulls() {
        CsvLoader loader = new CsvLoader()
                .longCol(0)
                .longCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(inPath("numbers_w_nulls.csv")));
    }

    @Test
    public void fromFile_LongCol_Nulls_Default() {
        DataFrame df = new CsvLoader()
                .longCol(0, -100L)
                .longCol(1, -200L)
                .load(inPath("numbers_w_nulls.csv"));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectLongColumns(0, 1)
                .expectRow(0, -100L, 3L)
                .expectRow(1, 5L, -200L);
    }

    @Test
    public void fromFile_LongCol_Nulls_Throw() {
        CsvLoader loader = new CsvLoader()
                .longCol(0)
                .longCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(inPath("numbers_w_nulls.csv")));
    }


    @Test
    public void fromFile_DoubleCol_Nulls_Default() {
        DataFrame df = new CsvLoader()
                .doubleCol(0, -1.1)
                .doubleCol(1, -3.14)
                .load(inPath("doubles_w_nulls.csv"));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1.1, 3.1)
                .expectRow(1, 5.2002, -3.14);
    }

    @Test
    public void fromFile_DecimalCol() {
        DataFrame df = new CsvLoader()
                .decimalCol(0)
                .decimalCol(1)
                .load(inPath("doubles_w_nulls.csv"));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, null, new BigDecimal("3.1"))
                .expectRow(1, new BigDecimal("5.2002"), null);
    }

    @Test
    public void fromFile_DateTimeColumns() {
        DataFrame df = new CsvLoader()
                .dateCol(0)
                .dateTimeCol("default_date_time")
                .dateCol("custom_date", DateTimeFormatter.ofPattern("M/d/yy"))
                .dateTimeCol(3, DateTimeFormatter.ofPattern("M/d/yy HH:mm:ss"))
                .load(inPath("dt1.csv"));

        new DataFrameAsserts(df, "default_date", "default_date_time", "custom_date", "custom_date_time")
                .expectHeight(2)
                .expectRow(0,
                        LocalDate.of(2015, 1, 1),
                        LocalDateTime.of(2015, 1, 1, 5, 1, 20),
                        LocalDate.of(2015, 1, 1),
                        LocalDateTime.of(2015, 1, 1, 5, 1, 20))
                .expectRow(1,
                        LocalDate.of(2016, 3, 31),
                        LocalDateTime.of(2016, 3, 31, 12, 0, 25),
                        LocalDate.of(2016, 3, 31),
                        LocalDateTime.of(2016, 3, 31, 12, 0, 25));
    }

    @Test
    public void fromFile_EmptyStringColumn() {
        DataFrame df = new CsvLoader()
                .load(inPath("strings_w_nulls.csv"));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, "", "three")
                .expectRow(1, "five", "");
    }

    @Test
    public void valueCardinality_Nulls() {
        DataFrame df = new CsvLoader()
                .col("A", s -> s != null ? Integer.parseInt(s) : null)
                .emptyStringIsNull()
                .load(inPath("with_duplicates.csv"));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("A").mapVal(System::identityHashCode),
                $col("B").mapVal(System::identityHashCode));

        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(6, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void valueCardinality_compactCol_Name() {
        DataFrame df = new CsvLoader()
                .compactCol("A", s -> s != null ? Integer.parseInt(s) : null)
                .compactCol("B")
                .emptyStringIsNull()
                .load(inPath("with_duplicates.csv"));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("A").mapVal(System::identityHashCode),
                $col("B").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void valueCardinality_compactCol_Pos() {
        DataFrame df = new CsvLoader()
                .compactCol(0, s -> s != null ? Integer.parseInt(s) : null)
                .compactCol(1)
                .emptyStringIsNull()
                .load(inPath("with_duplicates.csv"));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("A").mapVal(System::identityHashCode),
                $col("B").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }
}
