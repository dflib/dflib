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
}
