package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class AutoColumnsTest {

    @Test
    void autoAndNamedColumns() {
        String csv = "id,value,comment\n1,2,test\n2,45,\n";
        CsvFormat format = CsvFormat
                .builder()
                .autoColumns(true)
                .column(CsvFormat.column("value").type(CsvColumnType.INTEGER))
                .build();

        new DfParserAsserts(csv, format, "id", "value", "comment")
                .expectHeight(2)
                .expectRow(0, "1", 2, "test")
                .expectRow(1, "2", 45, "");
    }

    @Test
    void autoAndIndexedColumns() {
        String csv = "id,value,comment\n1,2,test\n2,45,\n";
        CsvFormat format = CsvFormat
                .builder()
                .autoColumns(true)
                .column(CsvFormat.column(1).type(CsvColumnType.INTEGER))
                .build();

        new DfParserAsserts(csv, format, "id", "value", "comment")
                .expectHeight(2)
                .expectRow(0, "1", 2, "test")
                .expectRow(1, "2", 45, "");
    }

    @Test
    void duplicateColumns() {
        String csv = "id,value,value\n1,2,test\n2,45,\n";
        CsvFormat format = CsvFormat
                .builder()
                .autoColumns(true)
                .build();

        new DfParserAsserts(csv, format, "id", "value", "value_")
                .expectHeight(2)
                .expectRow(0, "1", "2", "test")
                .expectRow(1, "2", "45", "");
    }

}
