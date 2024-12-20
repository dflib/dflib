package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.exp.ScalarExp;
import org.dflib.exp.bool.AndCondition;
import org.dflib.exp.bool.BoolScalarExp;
import org.dflib.exp.bool.NotCondition;
import org.dflib.exp.bool.OrCondition;
import org.dflib.exp.num.FloatScalarExp;
import org.dflib.exp.num.IntScalarExp;
import org.dflib.exp.num.LongScalarExp;
import org.dflib.exp.str.StrScalarExp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;

public class ExpTreeBuilder {

    private final Deque<Exp<?>> expStack = new ArrayDeque<>();

    void push(Exp<?> exp) {
        expStack.push(exp);
    }

    Condition popCondition() {
        if (!isCondition()) {
            throw new IllegalStateException("Not a condition");
        }
        Exp<Boolean> pop = pop();
        return (Condition) pop;
    }

    NumExp<?> popNum() {
        if (!isNum()) {
            throw new IllegalStateException("Not a num");
        }
        Exp<Number> exp = pop();
        return (NumExp<?>) exp;
    }

    StrExp popStr() {
        if (!isStr()) {
            throw new IllegalStateException("Not a str");
        }
        Exp<String> exp = pop();
        return (StrExp) exp;
    }

    boolean isNum() {
        return expStack.peekFirst() instanceof NumExp;
    }

    boolean isStr() {
        return expStack.peekFirst() instanceof StrExp;
    }

    boolean isCondition() {
        return expStack.peekFirst() instanceof Condition;
    }

    @SuppressWarnings("unchecked")
    <T> Exp<T> pop() {
        if (expStack.isEmpty()) {
            throw new IllegalStateException("Empty stack");
        }
        return (Exp<T>) expStack.pop();
    }

    Exp<?> root() {
        return expStack.getFirst();
    }

    void ifExp() {
        Exp<Object> right = pop();
        Exp<Object> left = pop();
        Condition condition = popCondition();
        push(Exp.ifExp(condition, left, right));
    }

    void notExp() {
        push(new NotCondition(popCondition()));
    }

    void orExp() {
        push(new OrCondition(popCondition(), popCondition()));
    }

    void andExp() {
        push(new AndCondition(popCondition(), popCondition()));
    }

    void gtExp() {
        if (isNum()) {
            NumExp<?> right = popNum();
            NumExp<?> left = popNum();
            push(left.gt(right));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void geExp() {
        if (isNum()) {
            NumExp<?> right = popNum();
            NumExp<?> left = popNum();
            push(left.ge(right));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void ltExp() {
        if (isNum()) {
            NumExp<?> right = popNum();
            NumExp<?> left = popNum();
            push(left.lt(right));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void leExp() {
        if (isNum()) {
            NumExp<?> right = popNum();
            NumExp<?> left = popNum();
            push(left.le(right));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void eqExp() {
        if (isNum()) {
            NumExp<?> right = popNum();
            NumExp<?> left = popNum();
            push(left.eq(right));
        } else if (isStr()) {
            StrExp right = popStr();
            StrExp left = popStr();
            push(left.eq(right));
        } else {
            throw new IllegalStateException("Numeric or String expression expected");
        }
    }

    void neExp() {
        if (isNum()) {
            NumExp<?> right = popNum();
            NumExp<?> left = popNum();
            push(left.ne(right));
        } else if (isStr()) {
            StrExp right = popStr();
            StrExp left = popStr();
            push(left.ne(right));
        } else {
            throw new IllegalStateException("Numeric or String expression expected");
        }
    }

    void addExp() {
        NumExp<?> right = popNum();
        NumExp<?> left = popNum();
        push(left.add(right));
    }

    void subExp() {
        NumExp<?> right = popNum();
        NumExp<?> left = popNum();
        push(left.sub(right));
    }

    void mulExp() {
        NumExp<?> right = popNum();
        NumExp<?> left = popNum();
        push(left.mul(right));
    }

    void divExp() {
        NumExp<?> right = popNum();
        NumExp<?> left = popNum();
        push(left.div(right));
    }

    void modExp() {
        NumExp<?> right = popNum();
        NumExp<?> left = popNum();
        push(left.mod(right));
    }

    void negateExp() {
        // TODO: check if this is (should be) supported by the DFLib Exp
        //       for now it's just (0 - exp)
        push(((NumExp<?>) Exp.$val(0, Integer.TYPE)).sub(popNum()));
    }


    void betweenExp() {
        NumExp<?> right = popNum();
        NumExp<?> left = popNum();
        NumExp<?> arg = popNum();
        push(arg.between(left, right));
    }

    void absExp() {
        push(popNum().abs());
    }

    void lenExp() {
        push(popStr().mapVal(String::length).castAsInt());
    }

    void matchesExp() {
        String right = popStr().reduce((Series<?>) null);
        push(popStr().matches(right));
    }

    void splitExp(boolean useLimit) {
        int limit = 0;
        if(useLimit) {
            limit = popNum().reduce((Series<?>) null).intValue();
        }
        String regex = popStr().reduce((Series<?>) null);
        push(popStr().split(regex, limit));
    }

    void minExp(boolean hasCondition) {
        Condition condition = hasCondition ? popCondition() : null;
        if (isNum()) {
            push(popNum().min(condition));
        } else if (isStr()) {
            push(popStr().min(condition));
        } else {
            throw new IllegalStateException("Numeric or String expression expected");
        }
    }

    void maxExp(boolean hasCondition) {
        Condition condition = hasCondition ? popCondition() : null;
        if (isNum()) {
            push(popNum().max(condition));
        } else if (isStr()) {
            push(popStr().max(condition));
        } else {
            throw new IllegalStateException("Numeric or String expression expected");
        }
    }

    void sumExp() {
        push(popNum().sum());
    }

    void avgExp() {
        push(popNum().avg());
    }

    void cumSumExp() {
        push(popNum().cumSum());
    }

    void medianExp() {
        push(popNum().median());
    }

    void firstExp(boolean hasCondition) {
        Condition filter = hasCondition ? popCondition() : null;
        push(pop().first(filter));
    }

    void lastExp() {
        push(pop().last());
    }

    void trueExp() {
        push(new BoolScalarExp(true));
    }

    void falseExp() {
        push(new BoolScalarExp(false));
    }

    void longVal(String image) {
        push(new LongScalarExp(Long.parseLong(image)));
    }

    void intVal(String image) {
        push(new IntScalarExp(Integer.parseInt(image)));
    }

    void floatVal(String image) {
        push(new FloatScalarExp(Float.parseFloat(image)));
    }

    void strVal(String image) {
        strVal(image, false);
    }

    void strVal(String image, boolean escape) {
        if (image.length() > 1 && image.startsWith("\"")) {
            image = image.substring(1, image.length() - 1);
            if (escape) {
                // TODO: unescape string values
                image = resolveEscapes(image);
            }
        }
        push(new StrScalarExp(image));
    }

    void intCol() {
        col(Exp::$int, Exp::$int);
    }

    void longCol() {
        col(Exp::$long, Exp::$long);
    }

    void floatCol() {
        col(Exp::$float, Exp::$float);
    }

    void strCol() {
        col(Exp::$str, Exp::$str);
    }

    void boolCol() {
        col(Exp::$bool, Exp::$bool);
    }

    void validateParenExp() {
        // TODO: do we need this check? popX() methods would enforce type check anyway...
    }

    @SuppressWarnings("unchecked")
    private void col(Function<Integer, Exp<?>> byIndex, Function<String, Exp<?>> byName) {
        // grammar ensures type, so we only check if it's named or a indexed column
        if (isNum()) {
            ScalarExp<Integer> numExp = (ScalarExp<Integer>) popNum();
            int value = numExp.reduce((DataFrame) null);
            push(byIndex.apply(value));
        } else if (isStr()) {
            // grammar ensures this
            ScalarExp<String> numExp = (ScalarExp<String>) popStr();
            String value = numExp.reduce((DataFrame) null);
            push(byName.apply(value));
        } else {
            throw new IllegalStateException("A number or a string expected");
        }
    }

    static String resolveEscapes(String image) {
        char[] chars = image.toCharArray();
        int i = 0;
        int to = 0;
        for (;i < chars.length; i++) {
            char ch = chars[i];
            if (ch == '\\') {
                if (i < chars.length - 1) {
                    ch = chars[++i];
                    switch (ch) {
                        case 'n':
                            ch = '\n';
                            break;
                        case 'r':
                            ch = '\r';
                            break;
                        case 't':
                            ch = '\t';
                            break;
                        case 'b':
                            ch = '\b';
                            break;
                        case 'f':
                            ch = '\f';
                            break;
                        case 's':
                            ch = ' ';
                            break;
                        case '"':
                        case '\'':
                        case '\\':
                            break;
                        // TODO: do we need a unicode escape sequences or it should be handled by the parser?
                        default:
                            throw new IllegalArgumentException("Invalid escape sequence: \\" + ch);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid escape sequence: \\");
                }
            }
            chars[to++] = ch;
        }
        return new String(chars, 0, to);
    }
}
