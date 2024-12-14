package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.net.URISyntaxException;

public class CsvLoader_GzipCompressionTest {

    @Test
    void loadFromFile() throws URISyntaxException {
        File in = new File(getClass().getResource("compressed.comma-separated-and-gzipped").toURI());
        DataFrame df = Csv.loader().compression(CompressionCodec.GZIP).load(in);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @ParameterizedTest
    @ValueSource(strings = {"compressed.csv.gz", "compressed.csv.gzip"})
    void loadFromFile_DeCompressByExtension(String fileName) throws URISyntaxException {

        File in = new File(getClass().getResource(fileName).toURI());
        DataFrame df = Csv.loader().load(in);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }
}
