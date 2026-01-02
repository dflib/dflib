package org.dflib.exp.str;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.builder.ValueCompactor;
import org.dflib.exp.map.MapExp1;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An expression that splits Strings into arrays based on a delimiter. Similar to {@link Pattern#split(CharSequence)},
 * it generates empty entries in the returned array for leading or repeating delimiters, but skips trailing empty
 * entries.
 */
public class StrSplitExp extends MapExp1<String, String[]> {

    public static StrSplitExp splitOnChar(Exp<String> exp, char delimiter, int limit) {
        String sDelimiter = String.valueOf(delimiter);
        return new StrSplitExp(exp, valToCompactSeries(s -> s.split(sDelimiter, limit)));
    }

    public static StrSplitExp splitOnRegex(Exp<String> exp, String regex) {
        Pattern p = Pattern.compile(regex);
        return new StrSplitExp(exp, valToCompactSeries(p::split));
    }

    public static StrSplitExp splitOnRegex(Exp<String> exp, String regex, int limit) {
        Pattern p = Pattern.compile(regex);
        return new StrSplitExp(exp, valToCompactSeries(s -> p.split(s, limit)));
    }

    private static Function<Series<String>, Series<String[]>> valToCompactSeries(Function<String, String[]> op) {
        ValueCompactor<String> compactor = new ValueCompactor<>();
        return s -> s.map(v -> {

            if (v != null) {
                String[] a = op.apply(v);
                int len = a.length;
                for (int i = 0; i < len; i++) {
                    a[i] = compactor.get(a[i]);
                }

                return a;
            }

            return null;
        });
    }

    protected StrSplitExp(Exp<String> exp, Function<Series<String>, Series<String[]>> op) {
        super("split", String[].class, exp, op);
    }
}
