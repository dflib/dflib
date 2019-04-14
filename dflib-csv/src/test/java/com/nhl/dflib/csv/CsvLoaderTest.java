package com.nhl.dflib.csv;

import com.nhl.dflib.unit.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.map.ValueMapper;
import org.apache.commons.csv.CSVFormat;
import org.junit.Test;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvLoaderTest extends BaseCsvTest {

    @Test
    public void testFromReader() {

        StringReader r = new StringReader("A,B" + System.lineSeparator()
                + "1,2" + System.lineSeparator()
                + "3,4");

        DataFrame df = new CsvLoader().load(r);
        new DFAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void testFromFile() {
        DataFrame df = new CsvLoader().load(inPath("f1.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void testFromFile_DefaultFormat_Excel() {
        DataFrame df = new CsvLoader().load(inPath("from_excel.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "commas,quotes\"'", "-85.7", "3")
                .expectRow(1, "with, commas", "5.50001", "6");
    }

    @Test
    public void testFromFile_MySQLFormat() {
        DataFrame df = new CsvLoader().format(CSVFormat.MYSQL).load(inPath("from_mysql.csv"));
        new DFAsserts(df, "1", "3365430", " xxxx")
                .expectHeight(4)
                .expectRow(0, "2", "2289959", "yyyy")
                .expectRow(1, "3", "3995478", "zzzzz")
                .expectRow(2, "4", "4112467", "nnn")
                .expectRow(3, "5", "1474364", "eee");
    }

    @Test
    public void testFromFile_Columns() {
        DataFrame df = new CsvLoader().columns("X", "Y", "Z").load(inPath("f1.csv"));
        new DFAsserts(df, "X", "Y", "Z")
                .expectHeight(3)
                .expectRow(0, "A", "b", "C")
                .expectRow(1, "1", "2", "3")
                .expectRow(2, "4", "5", "6");
    }

    @Test
    public void testFromFile_SkipRows() {
        DataFrame df = new CsvLoader().skipRows(1).load(inPath("f1.csv"));
        new DFAsserts(df, "1", "2", "3")
                .expectHeight(1)
                .expectRow(0, "4", "5", "6");
    }

    @Test
    public void testFromFile_ColumnType() {
        DataFrame df = new CsvLoader()
                .columnType(0, ValueMapper.stringToInt())
                .columnType(2, ValueMapper.stringToLong())
                .load(inPath("f1.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1, "2", 3L)
                .expectRow(1, 4, "5", 6L);
    }

    @Test
    public void testFromFile_ColumnType_OverrideType() {
        DataFrame df = new CsvLoader()
                .columnType(0, ValueMapper.stringToInt())
                .columnType("A", ValueMapper.stringToLong())
                .load(inPath("f1.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1L, "2", "3")
                .expectRow(1, 4L, "5", "6");
    }

    @Test
    public void testFromFile_NumColumn1() {
        DataFrame df = new CsvLoader()
                .numColumn(0, Integer.class)
                .numColumn("b", Long.class)
                .numColumn("C", Double.class)
                .load(inPath("f1.csv"));

        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectIntColumn(0)
                .expectRow(0, 1, 2L, 3.)
                .expectRow(1, 4, 5L, 6.);
    }

    @Test
    public void testFromFile_NumColumn2() {
        DataFrame df = new CsvLoader()
                .numColumn(0, Float.class)
                .numColumn("b", BigDecimal.class)
                .numColumn("C", BigInteger.class)
                .load(inPath("f1.csv"));

        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1.f, new BigDecimal(2), new BigInteger("3"))
                .expectRow(1, 4.f, new BigDecimal(5), new BigInteger("6"));
    }

    @Test
    public void testFromFile_DateTimeColumns() {
        DataFrame df = new CsvLoader()
                .dateColumn(0)
                .dateTimeColumn("default_date_time")
                .dateColumn("custom_date", DateTimeFormatter.ofPattern("M/d/yy"))
                .dateTimeColumn(3, DateTimeFormatter.ofPattern("M/d/yy HH:mm:ss"))
                .load(inPath("dt1.csv"));

        new DFAsserts(df, "default_date", "default_date_time", "custom_date", "custom_date_time")
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

    @Deprecated
    @Test
    public void testFromFile_ColumnTypes() {
        DataFrame df = new CsvLoader()
                .columnTypes(ValueMapper.stringToInt(), ValueMapper.stringToString(), ValueMapper.stringToDouble())
                .load(inPath("f1.csv"));
        new DFAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, 1, "2", 3.)
                .expectRow(1, 4, "5", 6.);
    }
}
