package org.dflib.csv.parser.format;

import org.dflib.csv.parser.CsvParser;
import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

class DataTypeTest {

    private static final String TYPES_CSV = """
            BOOLEAN,INTEGER,LONG,BIGINT,BIG_DECIMAL,DOUBLE,FLOAT,STRING
            true,42,9223372036854775807,123456789012345678901234567890,1234567890.12345,3.141592653589793,2.71828,hello world
            false,-7,-9223372036854775808,-987654321098765432109876543210,-0.00001,-1.23e-10,-3.4e+38,"string with, comma"
            TRUE,0,0,0,0.00000000000000000001,1.0E5,1.0E-5,"quoted ""text""\"
            FALSE,2147483647,1234567890123,999999999999999999999999999999,9999999999999.9999999999,NaN,Infinity,"special chars !@#$%^&*()"
            true,-2147483648,-1234567890123,-999999999999999999999999999999,-9999999999999.9999999999,-Infinity,-0.0,"multiline
            text"
            """;

    @Test
    void parseTypesByName() {
        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvFormat.column("BOOLEAN").type(CsvColumnType.BOOLEAN))
                .column(CsvFormat.column("INTEGER").type(CsvColumnType.INTEGER))
                .column(CsvFormat.column("LONG").type(CsvColumnType.LONG))
                .column(CsvFormat.column("BIGINT").type(CsvColumnType.BIG_INTEGER))
                .column(CsvFormat.column("BIG_DECIMAL").type(CsvColumnType.BIG_DECIMAL))
                .column(CsvFormat.column("DOUBLE").type(CsvColumnType.DOUBLE))
                .column(CsvFormat.column("FLOAT").type(CsvColumnType.FLOAT))
                .column(CsvFormat.column("STRING").type(CsvColumnType.STRING))
                .build();

        new DfParserAsserts(TYPES_CSV, format, "BOOLEAN", "INTEGER", "LONG", "BIGINT", "BIG_DECIMAL", "DOUBLE", "FLOAT", "STRING")
                .expectHeight(5)
                .expectRow(0, true, 42, 9223372036854775807L, new BigInteger("123456789012345678901234567890"), new BigDecimal("1234567890.12345"), 3.141592653589793, 2.71828f, "hello world")
                .expectRow(1, false, -7, -9223372036854775808L, new BigInteger("-987654321098765432109876543210"), new BigDecimal("-0.00001"), -1.23e-10, -3.4e+38f, "string with, comma")
                .expectRow(2, true, 0, 0L, new BigInteger("0"), new BigDecimal("0.00000000000000000001"), 1.0E5, 1.0E-5f, "quoted \"text\"")
                .expectRow(3, false, 2147483647, 1234567890123L, new BigInteger("999999999999999999999999999999"), new BigDecimal("9999999999999.9999999999"), Double.NaN, Float.POSITIVE_INFINITY, "special chars !@#$%^&*()")
                .expectRow(4, true, -2147483648, -1234567890123L, new BigInteger("-999999999999999999999999999999"), new BigDecimal("-9999999999999.9999999999"), Double.NEGATIVE_INFINITY, -0.0f, "multiline\ntext");
    }

    @Test
    void parseTypesByIndex() {
        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvFormat.column(0).type(CsvColumnType.BOOLEAN))
                .column(CsvFormat.column(1).type(CsvColumnType.INTEGER))
                .column(CsvFormat.column(2).type(CsvColumnType.LONG))
                .column(CsvFormat.column(3).type(CsvColumnType.BIG_INTEGER))
                .column(CsvFormat.column(4).type(CsvColumnType.BIG_DECIMAL))
                .column(CsvFormat.column(5).type(CsvColumnType.DOUBLE))
                .column(CsvFormat.column(6).type(CsvColumnType.FLOAT))
                .column(CsvFormat.column(7).type(CsvColumnType.STRING))
                .build();

        new DfParserAsserts(TYPES_CSV, format, "c0", "c1", "c2", "c3", "c4", "c5", "c6", "c7")
                .expectRow(0, true, 42, 9223372036854775807L, new BigInteger("123456789012345678901234567890"), new BigDecimal("1234567890.12345"), 3.141592653589793, 2.71828f, "hello world");
    }

    @Test
    void specialFloatValues() {
        String csv = """
                d,f
                nan,+inf
                -NAN,-InFiNiTy
                +NaN,inf
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvFormat.column("d").type(CsvColumnType.DOUBLE))
                .column(CsvFormat.column("f").type(CsvColumnType.FLOAT))
                .build();

        new DfParserAsserts(csv, format, "d", "f")
                .expectRow(0, Double.NaN, Float.POSITIVE_INFINITY)
                .expectRow(1, Double.NaN, Float.NEGATIVE_INFINITY)
                .expectRow(2, Double.NaN, Float.POSITIVE_INFINITY);
    }

    @Test
    void floatDoubleJdkParityLiterals() {
        String csv = """
                d,f
                0.1234567890123456789,0.1234567890123456789
                1.7976931348623159E308,3.4028236e38
                1e-325,1e-46
                -0.0,-0.0
                +NaN,+Inf
                -Infinity,-Infinity
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvFormat.column("d").type(CsvColumnType.DOUBLE))
                .column(CsvFormat.column("f").type(CsvColumnType.FLOAT))
                .build();

        new DfParserAsserts(csv, format, "d", "f")
                .expectRow(0, Double.parseDouble("0.1234567890123456789"), Float.parseFloat("0.1234567890123456789"))
                .expectRow(1, Double.parseDouble("1.7976931348623159E308"), Float.parseFloat("3.4028236e38"))
                .expectRow(2, Double.parseDouble("1e-325"), Float.parseFloat("1e-46"))
                .expectRow(3, -0.0d, -0.0f)
                .expectRow(4, Double.NaN, Float.POSITIVE_INFINITY)
                .expectRow(5, Double.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
    }

    @Test
    void nullableColumnDefaultNull() {
        String csv = """
                id,name,value
                1,,foo
                2,B,bar
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name").nullable(true))
                .column(CsvFormat.column("value"))
                .build();

        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectRow(0, "1", null, "foo")
                .expectRow(1, "2", "B", "bar");
    }

    @Test
    void nullableColumnCustomNull() {
        String csv = """
                id,name,value
                1,,foo
                2,NA,bar
                3,null,baz
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name").nullable(true, "NA"))
                .column(CsvFormat.column("value"))
                .build();

        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectRow(0, "1", "", "foo")
                .expectRow(1, "2", null, "bar")
                .expectRow(2, "3", "null", "baz");
    }

    @Test
    void nonNullableKeepsMarker() {
        String csv = """
                id,name
                1,NA
                2,
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name").nullable(false, "NA"))
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectRow(0, "1", "NA")
                .expectRow(1, "2", "");
    }

    @Test
    void formatNullableDefaultEmpty() {
        String csv = """
                id,name
                1,
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .nullable(true)
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name"))
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectRow(0, "1", null)
                .expectRow(1, "2", "B");
    }

    @Test
    void formatNullablePerColumn() {
        String csv = """
                id,name,code
                1,NA,NA
                2,B,NA
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .nullable(true, "NA")
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name").nullable(false))
                .column(CsvFormat.column("code"))
                .build();

        new DfParserAsserts(csv, format, "id", "name", "code")
                .expectRow(0, "1", "NA", null)
                .expectRow(1, "2", "B", null);
    }

    @Test
    void nullableWithNullTokenAndDefaults() {
        String csv = """
                i,l,d,f
                1,2,3.5,4.5
                NA,NA,NA,NA
                3,4,5.5,6.5
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column("i").type(CsvColumnType.INTEGER).nullableWithDefault(true, "NA", -1))
                .column(CsvFormat.column("l").type(CsvColumnType.LONG).nullableWithDefault(true, "NA", -2L))
                .column(CsvFormat.column("d").type(CsvColumnType.DOUBLE).nullableWithDefault(true, "NA", -3.5d))
                .column(CsvFormat.column("f").type(CsvColumnType.FLOAT).nullableWithDefault(true, "NA", -4.5f))
                .build();

        new DfParserAsserts(csv, format, "i", "l", "d", "f")
                .expectRow(0, 1, 2L, 3.5d, 4.5f)
                .expectRow(1, -1, -2L, -3.5d, -4.5f)
                .expectRow(2, 3, 4L, 5.5d, 6.5f);
    }

    @Test
    void nullableWithDefaults() {
        String csv = """
                i,l,d,f
                1,2,3.5,4.5
                ,,,
                3,4,5.5,6.5
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column("i").type(CsvColumnType.INTEGER).nullableWithDefault(true, -1))
                .column(CsvFormat.column("l").type(CsvColumnType.LONG).nullableWithDefault(true, -2L))
                .column(CsvFormat.column("d").type(CsvColumnType.DOUBLE).nullableWithDefault(true, -3.5d))
                .column(CsvFormat.column("f").type(CsvColumnType.FLOAT).nullableWithDefault(true, -4.5f))
                .build();

        new DfParserAsserts(csv, format, "i", "l", "d", "f")
                .expectRow(0, 1, 2L, 3.5d, 4.5f)
                .expectRow(1, -1, -2L, -3.5d, -4.5f)
                .expectRow(2, 3, 4L, 5.5d, 6.5f);
    }

    @Test
    void integerTypeInvalidInput() {
        String csv = """
                id,val
                1,42
                2,abc
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column("id").type(CsvColumnType.INTEGER))
                .column(CsvFormat.column("val").type(CsvColumnType.INTEGER))
                .build();

        assertThrows(NumberFormatException.class, () -> new CsvParser(format).parse(new StringReader(csv)));
    }

    @Test
    void booleanType() {
        String csv = """
                b
                true
                false
                0
                yes
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column("b").type(CsvColumnType.BOOLEAN))
                .build();

        new DfParserAsserts(csv, format, "b")
                .expectHeight(4)
                .expectColumn("b", true, false, false, false);
    }
}
