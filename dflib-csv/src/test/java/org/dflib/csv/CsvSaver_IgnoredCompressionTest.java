package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvSaver_IgnoredCompressionTest {

    private static final CsvFormat FORMAT = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();

    @ParameterizedTest
    @ValueSource(strings = "a.gz")
    public void saveToString_IgnoreCompression(String fileName) {
        Codec c = Codec.ofUri(fileName).orElse(null);
        assertNotNull(c);

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        assertEquals("A,B\n1,2\n3,4\n",
                Csv.saver().format(FORMAT).compression(c).saveToString(df),
                "Compression settings must have been ignored");
    }

    @ParameterizedTest
    @ValueSource(strings = "a.gz")
    public void save_ToWriter_IgnoreCompression(String fileName) {
        Codec c = Codec.ofUri(fileName).orElse(null);
        assertNotNull(c);

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        StringWriter out = new StringWriter();
        Csv.saver().format(FORMAT).compression(c).save(df, out);
        assertEquals("A,B\n1,2\n3,4\n",
                out.toString(),
                "Compression settings must have been ignored");
    }
}
