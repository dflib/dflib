package org.dflib.ql.antlr4;

public class PositionalParamSource {

    Object[] data;
    int idx;

    void setData(Object... data) {
        this.data = data;
    }

    Object next() {
        if(idx >= data.length) {
            throw new IndexOutOfBoundsException("No parameter set for index " + idx);
        }
        return data[idx++];
    }

}
