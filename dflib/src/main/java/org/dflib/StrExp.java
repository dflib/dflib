package org.dflib;

import org.dflib.agg.Max;
import org.dflib.agg.Min;
import org.dflib.exp.agg.StrReduceExp1;
import org.dflib.exp.map.MapCondition1;
import org.dflib.exp.map.MapCondition2;
import org.dflib.exp.map.MapExp1;
import org.dflib.exp.num.DecimalExp1;
import org.dflib.exp.num.DoubleExp1;
import org.dflib.exp.num.FloatExp1;
import org.dflib.exp.num.IntExp1;
import org.dflib.exp.str.StrAsExp;
import org.dflib.exp.str.StrShiftExp;
import org.dflib.exp.str.StrSplitExp;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.dflib.Exp.$val;

/**
 * An expression applied to String columns.
 */
public interface StrExp extends Exp<String> {

    @Override
    default Class<String> getType() {
        return String.class;
    }

    /**
     * @since 2.0.0
     */
    @Override
    default StrExp as(String name) {
        Objects.requireNonNull(name, "Null 'name'");
        return new StrAsExp(name, this);
    }

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
    default NumExp<Float> castAsFloat() {
        return FloatExp1.mapVal("castAsFloat", this, Float::parseFloat);
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
        return MapCondition2.mapVal("matches", this, $val(regex), (s, r) -> p.matcher(s).matches());
    }

    /**
     * @since 2.0.0
     */
    default NumExp<Integer> len() {
        return IntExp1.mapVal("len", this, String::length);
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

    /**
     * @since 2.0.0
     */
    default StrExp min() {
        return min(null);
    }

    /**
     * @since 2.0.0
     */
    default StrExp min(Condition filter) {
        return new StrReduceExp1<>("min", this, Min::ofComparables, filter);
    }

    /**
     * @since 2.0.0
     */
    default StrExp max() {
        return max(null);
    }

    /**
     * @since 2.0.0
     */
    default StrExp max(Condition filter) {
        return new StrReduceExp1<>("max", this, Max::ofComparables, filter);
    }

    @Override
    default StrExp shift(int offset) {
        return shift(offset, null);
    }

    @Override
    default StrExp shift(int offset, String filler) {
        return new StrShiftExp(this, offset, filler);
    }
}
