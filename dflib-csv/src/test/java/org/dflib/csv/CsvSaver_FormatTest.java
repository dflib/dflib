package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

public class CsvSaver_FormatTest {

    private static final CsvFormat DEFAULT_FORMAT = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();
    private static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @ParameterizedTest(name = "{0}")
    @MethodSource
    public void saveToString(String name, CsvFormat format, DataFrame df, String expected) {
        assertEquals(expected, Csv.saver().format(format).saveToString(df));
    }

    private static Stream<Arguments> saveToString() {
        return Stream.of(
                Arguments.of("default format",
                        DEFAULT_FORMAT,
                        DF,
                        "A,B\n1,2\n3,4\n"),
                Arguments.of("minimal quoting for special chars",
                        DEFAULT_FORMAT,
                        DataFrame.foldByRow("A", "B").of("x,y", "z\"w", "a\nb", "c\rd"),
                        "A,B\n\"x,y\",\"z\"\"w\"\n\"a\nb\",\"c\rd\"\n"),
                Arguments.of("quote always",
                        CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).quote(Quote.of('"')).build(),
                        DF,
                        "\"A\",\"B\"\n\"1\",\"2\"\n\"3\",\"4\"\n"),
                Arguments.of("escape backslash in quoted field",
                        CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).escape(Escape.BACKSLASH).build(),
                        DataFrame.foldByRow("A").of("z\"w"),
                        "A\n\"z\\\"w\"\n"),
                Arguments.of("escape custom in quoted field",
                        CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).escape('$').build(),
                        DataFrame.foldByRow("A").of("z\"w"),
                        "A\n\"z$\"w\"\n"),
                Arguments.of("null string",
                        CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).nullString("NULL").build(),
                        DataFrame.foldByRow("A", "B").of("x", null, null, "y"),
                        "A,B\nx,NULL\nNULL,y\n"),
                Arguments.of("trailing delimiter",
                        CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).trailingDelimiter(true).build(),
                        DF,
                        "A,B,\n1,2,\n3,4,\n"),
                Arguments.of("custom delimiter",
                        CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).delimiter(";").build(),
                        DF,
                        "A;B\n1;2\n3;4\n"),
                Arguments.of("quote none for plain data",
                        CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT)
                                .quote(Quote.none())
                                .escape(Escape.NONE)
                                .build(),
                        DF,
                        "A,B\n1,2\n3,4\n")
        );
    }
}
