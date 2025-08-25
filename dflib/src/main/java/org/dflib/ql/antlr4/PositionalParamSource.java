package org.dflib.ql.antlr4;

public class PositionalParamSource {

    final Object[] data;
    int idx;

    public PositionalParamSource(Object[] data) {
        this.data = data;
    }

    Object next() {
        return next(Object.class);
    }

    <T> T next(Class<T> type) {
        if(idx >= data.length) {
            throw new IndexOutOfBoundsException("No parameter set for index " + idx);
        }
        Object next = data[idx++];
        if(next == null) {
            return null;
        }
        if(!type.isAssignableFrom(next.getClass())) {
            throw new ClassCastException("Expecting parameter type " + type + ", got " + next.getClass());
        }
        return type.cast(next);
    }

}
