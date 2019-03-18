package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.List;

public class ListSeries<T> implements Series<T> {

    private List<T> data;

    public ListSeries(List<T> data) {
        this.data = data;
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public T get(int index) {
        return data.get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        for(int i = 0; i < len ; i++) {
            to[toOffset + i] = data.get(fromOffset + i);
        }
    }
}
