package org.dflib.exp.str;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.map.MapExp1;

import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * An expression that splits Strings into arrays based on a delimiter. Similar to {@link Pattern#split(CharSequence)},
 * it generates empty entries in the returned array for leading or repeating delimiters, but skips trailing empty
 * entries.
 *
 * @since 1.0.0-M19
 */
public class StrSplitExp extends MapExp1<String, String[]> {

    public static StrSplitExp splitOnChar(Exp<String> exp, char delimiter, int limit) {
        String sDelimiter = String.valueOf(delimiter);
        return new StrSplitExp(exp, valToSeries(s -> s.split(sDelimiter, limit)));
    }

    public static StrSplitExp splitOnRegex(Exp<String> exp, String regex) {
        Pattern p = Pattern.compile(regex);
        return new StrSplitExp(exp, valToSeries(s -> p.split(s)));
    }

    public static StrSplitExp splitOnRegex(Exp<String> exp, String regex, int limit) {
        Pattern p = Pattern.compile(regex);
        return new StrSplitExp(exp, valToSeries(s -> p.split(s, limit)));
    }

    protected StrSplitExp(Exp<String> exp, Function<Series<String>, Series<String[]>> op) {
        super("split", String[].class, exp, op);
    }
}
