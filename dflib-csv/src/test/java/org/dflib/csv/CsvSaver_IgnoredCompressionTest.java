package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.codec.Codec;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvSaver_IgnoredCompressionTest {

    @ParameterizedTest
    @ValueSource(strings = "a.gz")
    public void saveToString_IgnoreCompression(String fakeFileName) {
        Codec c = Codec.ofUri(fakeFileName).orElse(null);
        assertNotNull(c);

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", Csv.saver().compression(c).saveToString(df), "Compression settings must have been ignored");
    }

    @ParameterizedTest
    @ValueSource(strings = "a.gz")
    public void save_ToWriter_IgnoreCompression(String fakeFileName) {

        Codec c = Codec.ofUri(fakeFileName).orElse(null);
        assertNotNull(c);

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        StringWriter out = new StringWriter();
        Csv.saver().compression(c).save(df, out);
        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", out.toString(), "Compression settings must have been ignored");
    }

}
