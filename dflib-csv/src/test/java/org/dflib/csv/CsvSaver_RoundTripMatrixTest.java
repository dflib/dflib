package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.dflib.csv.parser.format.Trim;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Checks the CsvSaver contract over a matrix of formats, values and column positions: saving a value either produces
 * a CSV that the loader with the same format reads back as an equal value, or throws an IllegalArgumentException.
 * There should be no silent data changes, no loader failures, and no rejections of the values that can be quoted.
 */
public class CsvSaver_RoundTripMatrixTest {

    private static final int MAX_REPORTED_FAILURES = 40;

    enum Position {FIRST, MIDDLE, LAST, SINGLE}

    @Test
    public void roundTripOrThrow() {

        List<String> failures = new ArrayList<>();
        int formats = 0;
        int failureCount = 0;

        for (FormatSpec spec : formatSpecs()) {
            CsvFormat format = spec.build();
            formats++;
            List<String> values = values(spec);
            for (Position position : Position.values()) {
                for (String failure : check(spec, format, position, values)) {
                    failureCount++;
                    if (failures.size() < MAX_REPORTED_FAILURES) {
                        failures.add(failure);
                    }
                }
            }
        }

        if (failureCount > 0) {
            fail(failureCount + " round trip failure(s) in " + formats + " formats. First " + failures.size() + ":\n"
                    + String.join("\n", failures));
        }
    }

    private static List<String> check(FormatSpec spec, CsvFormat format, Position position, List<String> values) {

        // values that can't be represented in the format must be rejected by the saver
        List<String> writable = new ArrayList<>(values.size());
        for (String value : values) {
            try {
                Csv.saver().format(format).saveToString(df(position, Collections.singletonList(value)));
                writable.add(value);
            } catch (IllegalArgumentException e) {
                // expected for unrepresentable values, but never for the plain ones, and never for the non-null
                // values that can be quoted and escaped
                if ("a".equals(value) || (value != null && spec.quotesAndEscapes())) {
                    return List.of(spec + ", " + position + ", value=<" + printable(value)
                            + ">: a representable value was rejected: " + e.getMessage());
                }
            }
        }

        // the fast path: all values in a single DataFrame
        if (roundTrip(spec, format, position, writable) == null) {
            return List.of();
        }

        // the slow path: locating the offending values
        List<String> failures = new ArrayList<>();
        for (String value : writable) {
            String failure = roundTrip(spec, format, position, Collections.singletonList(value));
            if (failure != null) {
                failures.add(spec + ", " + position + ", value=<" + printable(value) + ">: " + failure);
            }
        }

        if (failures.isEmpty()) {
            failures.add(spec + ", " + position + ": values fail together, but not individually: "
                    + roundTrip(spec, format, position, writable));
        }

        return failures;
    }

    private static String roundTrip(FormatSpec spec, CsvFormat format, Position position, List<String> values) {

        DataFrame df = df(position, values);
        String csv = Csv.saver().format(format).saveToString(df);

        CsvLoader loader = Csv.loader().format(format);
        if (spec.nullString != null) {
            loader.nullString(spec.nullString);
        }

        DataFrame loaded;
        try {
            loaded = loader.load(new StringReader(csv));
        } catch (RuntimeException e) {
            return "load failed: " + e + ", csv=<" + printable(csv) + ">";
        }

        if (loaded.width() != df.width() || loaded.height() != df.height()) {
            return "expected " + df.height() + "x" + df.width() + ", got " + loaded.height() + "x" + loaded.width()
                    + ", csv=<" + printable(csv) + ">";
        }

        for (int i = 0; i < df.height(); i++) {
            for (int j = 0; j < df.width(); j++) {

                // without a null string, nulls are saved as empty strings by design
                Object expected = df.getColumn(j).get(i);
                if (expected == null && spec.nullString == null) {
                    expected = "";
                }

                Object actual = loaded.getColumn(j).get(i);
                if (!Objects.equals(expected, actual)) {
                    return "expected=<" + printable(expected) + ">, actual=<" + printable(actual)
                            + ">, csv=<" + printable(csv) + ">";
                }
            }
        }

        return null;
    }

    // each value goes to its own row. The values are followed by a row of plain values to detect broken row
    // boundaries, and then repeated, so that each one is also checked at the end of the file
    private static DataFrame df(Position position, List<String> values) {
        int w = position == Position.SINGLE ? 1 : 3;
        int valuePos = switch (position) {
            case FIRST, SINGLE -> 0;
            case MIDDLE -> 1;
            case LAST -> 2;
        };

        int n = values.size();
        int h = n * 2 + 1;
        Object[] data = new Object[h * w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                data[i * w + j] = i != n && j == valuePos ? values.get(i < n ? i : i - n - 1) : "p" + j;
            }
        }

        return w == 1
                ? DataFrame.foldByRow("A").of(data)
                : DataFrame.foldByRow("A", "B", "C").of(data);
    }

    // values are built of the chars that have a special meaning in the format
    private static List<String> values(FormatSpec spec) {

        Set<String> specials = new LinkedHashSet<>();
        specials.add("\"");
        specials.add("'");
        specials.add("\\");
        specials.add("$");
        specials.add("\r");
        specials.add("\n");
        specials.add(" ");
        specials.add("#");
        specials.add(spec.delimiter);
        for (char c : spec.delimiter.toCharArray()) {
            specials.add(String.valueOf(c));
        }

        Set<String> values = new LinkedHashSet<>();
        values.add(null);
        values.add("");
        values.add("a");
        values.add("NULL");
        if (spec.nullString != null) {
            values.add(spec.nullString);
        }
        values.add("\r\n");
        values.add("a\r\nb");

        for (String s : specials) {
            values.add(s);
            values.add(s + "a");
            values.add("a" + s);
            values.add("a" + s + "b");
            values.add(s + s);
        }

        for (String s1 : specials) {
            for (String s2 : specials) {
                values.add("a" + s1 + s2 + "b");
                values.add("a" + s1 + s2);
            }
        }

        return new ArrayList<>(values);
    }

    private static List<FormatSpec> formatSpecs() {
        List<FormatSpec> specs = new ArrayList<>();

        for (String delimiter : new String[]{",", "\t", "||", "=>"}) {
            for (String quote : new String[]{"optional", "always", "none"}) {
                for (String escape : new String[]{"NONE", "DOUBLE", "BACKSLASH", "$"}) {
                    for (LineBreak lineBreak : LineBreak.values()) {
                        for (Trim trim : new Trim[]{Trim.NONE, Trim.FULL}) {
                            for (String nullString : new String[]{null, "", "NULL", "#N/A", " N "}) {
                                for (String comment : new String[]{null, "#"}) {
                                    for (boolean trailingDelimiter : new boolean[]{false, true}) {
                                        specs.add(new FormatSpec(
                                                delimiter,
                                                quote,
                                                escape,
                                                lineBreak,
                                                trim,
                                                nullString,
                                                comment,
                                                trailingDelimiter));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return specs;
    }

    private static String printable(Object value) {
        return value == null
                ? "null"
                : value.toString().replace("\\", "\\\\").replace("\r", "\\r").replace("\n", "\\n");
    }

    record FormatSpec(
            String delimiter,
            String quote,
            String escape,
            LineBreak lineBreak,
            Trim trim,
            String nullString,
            String comment,
            boolean trailingDelimiter) {

        boolean quotesAndEscapes() {
            return !"none".equals(quote) && !"NONE".equals(escape);
        }

        CsvFormat build() {
            CsvFormat.Builder b = CsvFormat.defaultFormat()
                    .delimiter(delimiter)
                    .lineBreak(lineBreak)
                    .trim(trim)
                    .trailingDelimiter(trailingDelimiter);

            switch (quote) {
                case "optional" -> b.quote(Quote.optionalOf('"'));
                case "always" -> b.quote(Quote.of('"'));
                default -> b.quote(Quote.none());
            }

            switch (escape) {
                case "NONE" -> b.escape(Escape.NONE);
                case "DOUBLE" -> b.escape(Escape.DOUBLE);
                case "BACKSLASH" -> b.escape(Escape.BACKSLASH);
                default -> b.escape(escape.charAt(0));
            }

            if (nullString != null) {
                b.nullString(nullString);
            }

            if (comment != null) {
                b.enableComments(comment);
            }

            return b.build();
        }
    }
}
