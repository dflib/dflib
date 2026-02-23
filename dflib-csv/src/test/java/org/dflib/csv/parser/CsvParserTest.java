package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvColumnType;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class CsvParserTest {

    @Test
    void parse() {
        String csv = """
                id,name,value
                1,Name1,Value1
                2,Name2,Value2
                """;

        DataFrame df = new CsvParser().parse(new StringReader(csv));

        new DataFrameAsserts(df, "id", "name", "value")
                .expectHeight(2)
                .expectRow(0, "1", "Name1", "Value1")
                .expectRow(1, "2", "Name2", "Value2");
    }

    @Test
    void parserReuse() {
        String csv1 = """
                id,name,value
                1,Name1,Value1
                2,Name2,Value2
                """;

        String csv2 = """
                id,name,value
                3,Name3,Value3
                4,Name4,Value4
                """;

        CsvParser csvParser = new CsvParser();

        DataFrame df1 = csvParser.parse(new StringReader(csv1));
        new DataFrameAsserts(df1, "id", "name", "value")
                .expectHeight(2)
                .expectRow(0, "1", "Name1", "Value1")
                .expectRow(1, "2", "Name2", "Value2");

        DataFrame df2 = csvParser.parse(new StringReader(csv2));
        new DataFrameAsserts(df2, "id", "name", "value")
                .expectHeight(2)
                .expectRow(0, "3", "Name3", "Value3")
                .expectRow(1, "4", "Name4", "Value4");
    }

    @Test
    void parseWithFormat() {
        String csv = """
                "id";"name";"value"
                "1";"Name1";"Value1"
                "2";"Name2";"Value2"
                """;

        CsvParserConfig config = CsvParserConfig.builder()
                .csvFormat(CsvFormat.defaultFormat().delimiter(";"))
                .build();

        DataFrame df = new CsvParser(config).parse(new StringReader(csv));

        new DataFrameAsserts(df, "id", "name", "value")
                .expectHeight(2)
                .expectRow(0, "1", "Name1", "Value1")
                .expectRow(1, "2", "Name2", "Value2");
    }

    @Test
    void parseNoHeader() {
        String csv = """
                1,Name1,Value1
                2,Name2,Value2
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("f0"))
                .column(CsvColumnMapping.column(1).name("f1"))
                .column(CsvColumnMapping.column(2).name("f2"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .build();

        DataFrame df = new CsvParser(format).parse(new StringReader(csv));

        new DataFrameAsserts(df, "f0", "f1", "f2")
                .expectHeight(2)
                .expectRow(0, "1", "Name1", "Value1")
                .expectRow(1, "2", "Name2", "Value2");
    }

    @Test
    void parseTypes() {
        String csv = """
                id,name,active
                1,Name1,true
                2,Name2,false
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id").type(CsvColumnType.INTEGER))
                .column(CsvColumnMapping.column("name").type(CsvColumnType.STRING))
                .column(CsvColumnMapping.column("active").type(CsvColumnType.BOOLEAN))
                .build();

        DataFrame df = new CsvParser(format).parse(new StringReader(csv));

        new DataFrameAsserts(df, "id", "name", "active")
                .expectHeight(2)
                .expectRow(0, 1, "Name1", true)
                .expectRow(1, 2, "Name2", false);
    }

    @Test
    void parseSkipEmptyRows() {
        String csv = """
                1,Name1
                
                2,Name2
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("id"))
                .column(CsvColumnMapping.column(1).name("name"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat().skipEmptyRows())
                .build();

        DataFrame df = new CsvParser(format).parse(new StringReader(csv));

        new DataFrameAsserts(df, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "Name1")
                .expectRow(1, "2", "Name2");
    }

    @Test
    void parseSkipEmptyComments() {
        String csv = """
                
                # comment before data
                1,Name1
                
                # comment between rows
                2,Name2
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("id"))
                .column(CsvColumnMapping.column(1).name("name"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat().enableComments("#").skipEmptyRows())
                .build();

        DataFrame df = new CsvParser(format).parse(new StringReader(csv));

        new DataFrameAsserts(df, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "Name1")
                .expectRow(1, "2", "Name2");
    }

    @Test
    void parseSkipEmptyStart() {
        String csv = """
                
                1,Name1
                2,Name2
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("id"))
                .column(CsvColumnMapping.column(1).name("name"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat().skipEmptyRows())
                .build();

        DataFrame df = new CsvParser(format).parse(new StringReader(csv));

        new DataFrameAsserts(df, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "Name1")
                .expectRow(1, "2", "Name2");
    }

    @Test
    void parseEscapedAcrossBuffer() {
        int bufferSize = CsvScanner.INITIAL_BUFFER_SIZE;
        String prefix = "a".repeat(bufferSize - 2);
        String csv = "\"" + prefix + "\"\"b\"\n";

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("value"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .build();

        DataFrame df = new CsvParser(format).parse(new StringReader(csv));

        new DataFrameAsserts(df, "value")
                .expectHeight(1)
                .expectRow(0, prefix + "\"b");
    }

    @Test
    void parseDefinedColumns() {
        String csv = """
                a,b,c
                1,Name1,true
                2,Name2,false
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name"))
                .column(CsvColumnMapping.column("active"))
                .autoColumns(false)
                .build();

        DataFrame df = new CsvParser(format).parse(new StringReader(csv));

        new DataFrameAsserts(df, "id", "name", "active")
                .expectHeight(2)
                .expectRow(0, "1", "Name1", "true")
                .expectRow(1, "2", "Name2", "false");
    }
}
