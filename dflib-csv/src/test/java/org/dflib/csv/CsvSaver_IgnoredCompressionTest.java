package org.dflib.csv;

import org.dflib.DataFrame;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaver_IgnoredCompressionTest {

    @ParameterizedTest
    @EnumSource(CompressionCodec.class)
    public void saveToString_IgnoreCompression(CompressionCodec c) {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", Csv.saver().compression(c).saveToString(df), "Compression settings must have been ignored");
    }

    @ParameterizedTest
    @EnumSource(CompressionCodec.class)
    public void save_ToWriter_IgnoreCompression(CompressionCodec c) {

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
