package org.dflib.exp.parser;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
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
        return (StrExp)exp;
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
        if(expStack.isEmpty()) {
            throw new IllegalStateException("Empty stack");
        }
        return (Exp<T>)expStack.pop();
    }

    Exp<?> root() {
        return expStack.getFirst();
    }

    void ifExp() {
        push(Exp.ifExp(popCondition(), pop(), pop()));
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
            push(popNum().gt(popNum()));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void geExp() {
        if (isNum()) {
            push(popNum().ge(popNum()));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void ltExp() {
        if (isNum()) {
            push(popNum().lt(popNum()));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void leExp() {
        if (isNum()) {
            push(popNum().le(popNum()));
        } else {
            throw new IllegalStateException("Not a num");
        }
    }

    void eqExp() {
        if (isNum()) {
            push(popNum().eq(popNum()));
        } else if(isStr()) {
            push(popStr().eq(popStr()));
        } else {
            throw new IllegalStateException("Numeric or String expression expected");
        }
    }

    void neExp() {
        if (isNum()) {
            push(popNum().ne(popNum()));
        } else if(isStr()) {
            push(popStr().ne(popStr()));
        } else {
            throw new IllegalStateException("Numeric or String expression expected");
        }
    }

    void addExp() {
        push(popNum().add(popNum()));
    }

    void subExp() {
        push(popNum().sub(popNum()));
    }

    void mulExp() {
        push(popNum().mul(popNum()));
    }

    void divExp() {
        push(popNum().div(popNum()));
    }

    void modExp() {
        push(popNum().mod(popNum()));
    }

    void negateExp() {
        // TODO: check if this is (should be) supported by the DFLib Exp
        //       for now it's just (0 - exp)
        push(((NumExp<?>) Exp.$val(0, Integer.TYPE)).sub(popNum()));
    }


    void betweenExp() {
        push(popNum().between(popNum(), popNum()));
    }

    void absExp() {
        push(popNum().abs());
    }

    void minExp(boolean hasCondition) {
        Condition condition = hasCondition ? popCondition() : null;
        if(isNum()) {
            push(popNum().min(condition));
        } else if(isStr()) {
            push(popStr().min(condition));
        } else {
            throw new IllegalStateException("Numeric or String expression expected");
        }
    }

    void maxExp(boolean hasCondition) {
        Condition condition = hasCondition ? popCondition() : null;
        if(isNum()) {
            push(popNum().max(condition));
        } else if(isStr()) {
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
        if(escape) {
            // TODO: unescape string values
            image = resolveEscapes(image);
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
            ScalarExp<Integer> numExp = (ScalarExp<Integer>)popNum();
            int value = numExp.reduce((DataFrame) null);
            push(byIndex.apply(value));
        } else if(isStr()) {
            // grammar ensures this
            ScalarExp<String> numExp = (ScalarExp<String>)popStr();
            String value = numExp.reduce((DataFrame) null);
            push(byName.apply(value));
        } else {
            throw new IllegalStateException("A number or a string expected");
        }
    }

    private String resolveEscapes(String image) {
        return image;
    }
}
