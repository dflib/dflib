package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

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
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toQL() {
        return Arrays.stream(args).map(a -> a.toQL()).collect(Collectors.joining(", ", opName + "(", ")"));
    }

    @Override
    public String toQL(DataFrame df) {
        return Arrays.stream(args).map(a -> a.toQL(df)).collect(Collectors.joining(", ", opName + "(", ")"));
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
