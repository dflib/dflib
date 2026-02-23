package org.dflib.csv.parser.test;

import org.dflib.csv.parser.CsvParser;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.junit5.DataFrameAsserts;

import java.io.Reader;
import java.io.StringReader;

public class DfParserAsserts extends DataFrameAsserts {

    public DfParserAsserts(String resource, String... expectedColumns) {
        super(new CsvParser().parse(readerForResource(resource)), expectedColumns);
    }

    public DfParserAsserts(String resource, CsvParserConfig config, String... expectedColumns) {
        super(new CsvParser(config).parse(readerForResource(resource)), expectedColumns);
    }

    public DfParserAsserts(String resource, CsvFormat format, String... expectedColumns) {
        super(new CsvParser(CsvParserConfig.builder().csvFormat(format).build()).parse(readerForResource(resource)), expectedColumns);
    }

    public DfParserAsserts(String resource, CsvParserConfig.Builder config, String... expectedColumns) {
        super(new CsvParser(config.build()).parse(readerForResource(resource)), expectedColumns);
    }

    private static Reader readerForResource(String resource) {
        return new StringReader(resource);
    }

}
