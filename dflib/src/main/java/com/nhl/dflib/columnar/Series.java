package com.nhl.dflib.columnar;

public interface Series<T> {

    int size();

    T get(int index);
}
