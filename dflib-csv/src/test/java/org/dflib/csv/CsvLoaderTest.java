package org.dflib.csv;

import org.apache.commons.csv.CSVFormat;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.DataFrame;
import org.dflib.ValueMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
        String csv = """
                A,b,C
                1,2,3
                4,5,6""";
        DataFrame df = new CsvLoader().load(ByteSource.of(csv.getBytes()));

        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void fromByteSource_Encoding() {
        String csv = """
                A,b,C
                1,2,3
                4,5,6""";
        DataFrame df = new CsvLoader().encoding("UTF-16BE").load(ByteSource.of(csv.getBytes(StandardCharsets.UTF_16BE)));

        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void fromByteSources() {
        String csv1 = """
                A,b,C
                1,2,3
                4,5,6""";
        String csv2 = """
                X,y,Z
                7,8,9
                10,11,12""";

        Map<String, DataFrame> dfs = new CsvLoader().loadAll(ByteSources.of(
                Map.of("b1", ByteSource.of(csv1.getBytes()), "b2", ByteSource.of(csv2.getBytes()))));

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
    public void excel() {
        DataFrame df = new CsvLoader().load(inPath("from_excel.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "commas,quotes\"'", "-85.7", "3")
                .expectRow(1, "with, commas", "5.50001", "6");
    }

    @Test
    public void mySQLFormat() {
        DataFrame df = new CsvLoader().format(CSVFormat.MYSQL).load(inPath("from_mysql.csv"));
        new DataFrameAsserts(df, "1", "3365430", " xxxx")
                .expectHeight(4)
                .expectRow(0, "2", "2289959", "yyyy")
                .expectRow(1, "3", "3995478", "zzzzz")
                .expectRow(2, "4", "4112467", "nnn")
                .expectRow(3, "5", "1474364", "eee");
    }

    @Test
    public void header() {
        DataFrame df = new CsvLoader().header("X", "Y", "Z").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "X", "Y", "Z")
                .expectHeight(3)
                .expectRow(0, "A", "b", "C")
                .expectRow(1, "1", "2", "3")
                .expectRow(2, "4", "5", "6");
    }

    @Test
    public void generateHeader() {
        DataFrame df = new CsvLoader().generateHeader().load(inPath("f1.csv"));
        new DataFrameAsserts(df, "c0", "c1", "c2")
                .expectHeight(3)
                .expectRow(0, "A", "b", "C")
                .expectRow(1, "1", "2", "3")
                .expectRow(2, "4", "5", "6");
    }

    @Test
    public void cols() {
        DataFrame df = new CsvLoader().cols("b", "A").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "b", "A")
                .expectHeight(2)
                .expectRow(0, "2", "1")
                .expectRow(1, "5", "4");
    }

    @Test
    public void cols_Pos() {
        DataFrame df = new CsvLoader().cols(1, 0).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "b", "A")
                .expectHeight(2)
                .expectRow(0, "2", "1")
                .expectRow(1, "5", "4");
    }

    @Test
    public void cols_Header() {
        DataFrame df = new CsvLoader().header("X", "Y", "Z").cols("Y", "X").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "Y", "X")
                .expectHeight(3)
                .expectRow(0, "b", "A")
                .expectRow(1, "2", "1")
                .expectRow(2, "5", "4");
    }

    @Test
    public void cols_generateHeader() {
        DataFrame df = new CsvLoader().generateHeader().cols("c1", "c0").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "c1", "c0")
                .expectHeight(3)
                .expectRow(0, "b", "A")
                .expectRow(1, "2", "1")
                .expectRow(2, "5", "4");
    }

    @Test
    public void colsPos_generateHeader() {
        DataFrame df = new CsvLoader().generateHeader().cols(1, 0).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "c1", "c0")
                .expectHeight(3)
                .expectRow(0, "b", "A")
                .expectRow(1, "2", "1")
                .expectRow(2, "5", "4");
    }

    @Test
    public void colsExcept() {
        DataFrame df = new CsvLoader().colsExcept("b").load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, "1", "3")
                .expectRow(1, "4", "6");
    }

    @Test
    public void colsExcept_Pos() {
        DataFrame df = new CsvLoader().colsExcept(1).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, "1", "3")
                .expectRow(1, "4", "6");
    }

    @Test
    public void limit() {
        DataFrame df = new CsvLoader().limit(1).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(1)
                .expectRow(0, "1", "2", "3");
    }

    @Test
    public void limit_Header() {
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
    public void offset() {
        DataFrame df = new CsvLoader().offset(1).load(inPath("f1.csv"));
        new DataFrameAsserts(df, "1", "2", "3")
                .expectHeight(1)
                .expectRow(0, "4", "5", "6");
    }

    @Test
    public void offset_Limit() {
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
    public void col() {
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
    public void colType_Override() {
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
    public void numColumn1() {
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
    public void numColumn2() {
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
    public void dateTimeColumns() {
        String csv = """
                default_date,default_date_time,custom_date,custom_date_time
                2015-01-01,2015-01-01T05:01:20,1/1/15,1/1/15 05:01:20
                2016-03-31,2016-03-31T12:00:25,3/31/16,3/31/16 12:00:25""";

        DataFrame df = new CsvLoader()
                .dateCol(0)
                .dateTimeCol("default_date_time")
                .dateCol("custom_date", DateTimeFormatter.ofPattern("M/d/yy"))
                .dateTimeCol(3, DateTimeFormatter.ofPattern("M/d/yy HH:mm:ss"))
                .load(ByteSource.of(csv.getBytes()));

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
}
