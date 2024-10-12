package org.dflib;

import org.dflib.exp.map.MapCondition1;
import org.dflib.exp.map.MapExp1;
import org.dflib.exp.map.MapExpScalarCondition2;
import org.dflib.exp.num.DecimalExp1;
import org.dflib.exp.num.DoubleExp1;
import org.dflib.exp.str.StrExp1;
import org.dflib.exp.str.StrSplitExp;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * An expression applied to String columns.
 */
public interface StrExp extends Exp<String> {

    @Override
    default StrExp castAsStr() {
        return this;
    }

    @Override
    default Condition castAsBool() {
        return MapCondition1.mapVal("castAsBool", this, Boolean::valueOf);
    }


    default <E extends Enum<E>> Exp<E> castAsEnum(Class<E> type) {
        return MapExp1.mapVal("castAsEnum", type, this, s -> Enum.valueOf(type, s));
    }


    @Override
    default NumExp<Integer> castAsInt() {
        // Need to do an extra decimal conversion, so that we can properly cast any number format.
        // Int expressions must override this method to return "this"
        return castAsDecimal().castAsInt();
    }


    @Override
    default NumExp<Long> castAsLong() {
        // Need to do an extra decimal conversion, so that we can properly cast any number format.
        // Long expressions must override this method to return "this"
        return castAsDecimal().castAsLong();
    }

    @Override
    default NumExp<Double> castAsDouble() {
        return DoubleExp1.mapVal("castAsDouble", this, Double::parseDouble);
    }


    @Override
    default DecimalExp castAsDecimal() {
        return DecimalExp1.mapVal("castAsDecimal", this, BigDecimal::new);
    }

    default Condition matches(String regex) {
        // precompile pattern..
        Pattern p = Pattern.compile(regex);
        return MapExpScalarCondition2.mapVal("matches", this, regex, (s, r) -> p.matcher(s).matches());
    }

    default Condition startsWith(String prefix) {
        return MapExpScalarCondition2.mapVal("startsWith", this, prefix, (s, p) -> s.startsWith(prefix));
    }

    default Condition endsWith(String suffix) {
        return MapExpScalarCondition2.mapVal("endsWith", this, suffix, (s, p) -> s.endsWith(suffix));
    }

    default Condition contains(String suffix) {
        return MapExpScalarCondition2.mapVal("contains", this, suffix, (s, p) -> s.contains(suffix));
    }

    /**
     * A substring expression. Unlike Java "substring" methods, this expression does not throw out of bounds exceptions
     * if the String is shorter than the start index, and simply returns an empty string.
     *
     * @param fromInclusive a zero-based substring starting position. Can be negative, in which case the index is counted
     *                      from the end of the String.
     */
    default StrExp substr(int fromInclusive) {
        if (fromInclusive == 0) {
            return StrExp1.mapVal("substr", this, s -> s);
        } else if (fromInclusive < 0) {
            int endOffset = -fromInclusive;
            return StrExp1.mapVal("substr", this, s -> s.length() <= endOffset ? "" : s.substring(s.length() - endOffset));
        } else {
            return StrExp1.mapVal("substr", this, s -> s.length() <= fromInclusive ? "" : s.substring(fromInclusive));
        }
    }

    /**
     * A substring expression. Unlike Java "substring" methods, this expression does not throw out of bounds exceptions
     * if the String is shorter than the start index, or the substring is shorter than "len", and simply returns an
     * empty string.
     *
     * @param fromInclusive a zero-based substring starting position
     * @param len           a max length of the substring.
     */
    default StrExp substr(int fromInclusive, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("'len' must be non-negative: " + len);
        } else if (len == 0) {
            return StrExp1.mapVal("substr", this, s -> "");
        }

        if (fromInclusive < 0) {
            int endOffset = -fromInclusive;
            return StrExp1.mapVal("substr", this, s ->
                    s.length() <= endOffset ? "" : s.substring(s.length() - endOffset, Math.min(s.length(), s.length() - endOffset + len)));
        } else {
            return StrExp1.mapVal("substr", this, s ->
                    s.length() <= fromInclusive ? "" : s.substring(fromInclusive, Math.min(s.length(), fromInclusive + len)));
        }
    }


    default StrExp trim() {
        return StrExp1.mapVal("trim", this, s -> {
            String trimmed = s.trim();
            return trimmed.isEmpty() ? null : trimmed;
        });
    }

    /**
     * An expression that splits this String into a String array using the specified regex as a delimiter.
     */
    default Exp<String[]> split(char delimiter) {
        return split(delimiter, 0);
    }

    /**
     * An expression that splits this String into a String array using the specified regex as a delimiter. The limit
     * parameter follows the rules of the <code>Pattern.split(..)</code> method, namely a positive limit results in the
     * regex evaluated at most the "limit" number of times, "zero" limit results in the regex evaluated as many times as
     * needed, but trailing empty elements removed, while negative limit is the same as zero, except no trimming of
     * the trailing empty elements.     *
     */
    default Exp<String[]> split(char delimiter, int limit) {
        return StrSplitExp.splitOnChar(this, delimiter, limit);
    }

    /**
     * An expression that splits this String into a String array using the specified regex as a delimiter.
     */
    default Exp<String[]> split(String regex) {
        return StrSplitExp.splitOnRegex(this, regex);
    }

    /**
     * An expression that splits this String into a String array using the specified regex as a delimiter. The limit
     * parameter follows the rules of the <code>Pattern.split(..)</code> method, namely a positive limit results in the
     * regex evaluated at most the "limit" number of times, "zero" limit results in the regex evaluated as many times as
     * needed, but trailing empty elements removed, while negative limit is the same as zero, except no trimming of
     * the trailing empty elements.
     */
    default Exp<String[]> split(String regex, int limit) {
        return StrSplitExp.splitOnRegex(this, regex, limit);
    }
}
