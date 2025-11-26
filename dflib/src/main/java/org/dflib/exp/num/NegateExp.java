package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.Column;
import org.dflib.exp.Exp0;
import org.dflib.exp.Exp1;
import org.dflib.exp.ScalarExp;

import org.dflib.exp.map.MapExp1;

import java.util.function.UnaryOperator;

public class NegateExp<N extends Number> extends MapExp1<N, N> implements NumExp<N> {

    public NegateExp(Class<N> type, Exp<N> exp, UnaryOperator<N> op) {
        super("-", type, exp, valToSeries(op));
    }

    @Override
    public String toQL() {
        String expQL = exp.toQL();
        boolean parenthesesNeeded = expQL.startsWith(opName)
                || !(exp instanceof ScalarExp
                || exp instanceof Column
                || exp instanceof Exp0
                || exp instanceof Exp1);
        return opName + (parenthesesNeeded ? "(" + expQL + ")" : expQL);
    }

    @Override
    public String toQL(DataFrame df) {
        String expQL = exp.toQL(df);
        boolean parenthesesNeeded = expQL.startsWith(opName)
                || !(exp instanceof ScalarExp
                || exp instanceof Column
                || exp instanceof Exp0
                || exp instanceof Exp1 && !(exp instanceof NegateExp));
        return opName + (parenthesesNeeded ? "(" + expQL + ")" : expQL);
    }
}
