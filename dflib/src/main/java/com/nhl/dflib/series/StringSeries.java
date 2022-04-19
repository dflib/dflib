package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.util.Arrays;

public class StringSeries extends ObjectSeries<String> {

    @SafeVarargs
    public StringSeries(String... data) {
        super(Object.class);
        this.data = data;
    }


    @Override
    public Class<?> getNominalType() {
        return String.class;
    }

    @Override
    public Class<?> getInferredType() {
        return String.class;
    }

    private final String[] data;

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public String get(int index) {
        return data[index];
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public Series<String> materialize() {
        return this;
    }

    @Override
    public Series<String> fillNulls(String value) {

        int len = data.length;
        String[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = new String[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = value;
            }
        }

        return copy != null ? new StringSeries(copy) : this;
    }

    @Override
    public Series<String> fillNullsFromSeries(Series<? extends String> values) {
        int len = data.length;
        String[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = new String[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = values.get(i);
            }
        }

        return copy != null ? new StringSeries(copy) : this;
    }

    @Override
    public Series<String> fillNullsBackwards() {
        int len = data.length;
        String[] copy = null;
        int fillFrom = -1;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = (String[]) new Object[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                if (fillFrom < 0) {
                    fillFrom = i;
                }
            } else if (fillFrom >= 0) {
                Arrays.fill(copy, fillFrom, i, data[i]);
                fillFrom = -1;
            }
        }

        return copy != null ? new StringSeries(copy) : this;
    }

    @Override
    public Series<String> fillNullsForward() {
        int len = data.length;
        String[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                // leading nulls are fine
                if (i == 0) {
                    continue;
                }

                if (copy == null) {
                    copy = new String[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = copy[i - 1];
            }
        }

        return copy != null ? new StringSeries(copy) : this;
    }
}
