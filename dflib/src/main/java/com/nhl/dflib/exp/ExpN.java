package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * A  N-ary expression with multiple {@link Exp} arguments.
 *
 * @since 0.11
 */
public abstract class ExpN<T> implements Exp<T> {

    private final String opName;
    private final Class<T> type;
    private final Exp<?>[] args;

    public ExpN(String opName, Class<T> type, Exp<?>... args) {
        this.opName = opName;
        this.type = type;
        this.args = args;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return Arrays.stream(args).map(a -> a.getName()).collect(Collectors.joining(", ", opName + "(", ")"));
    }

    @Override
    public String getName(DataFrame df) {
        return Arrays.stream(args).map(a -> a.getName(df)).collect(Collectors.joining(", ", opName + "(", ")"));
    }

    @Override
    public Series<T> eval(DataFrame df) {

        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(df);
        }

        return doEval(df.height(), columns);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(s);
        }

        return doEval(s.size(), columns);
    }

    protected abstract Series<T> doEval(int height, Series<?>[] args);
}
