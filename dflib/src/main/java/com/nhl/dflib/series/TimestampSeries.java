package com.nhl.dflib.series;

import com.nhl.dflib.Series;

import java.sql.Timestamp;
import java.util.Arrays;

public class TimestampSeries extends ObjectSeries<Timestamp> {

    @Override
    public Class<?> getNominalType() {
        return Timestamp.class;
    }

    @Override
    public Class<?> getInferredType() {
        return Timestamp.class;
    }

    private final Timestamp[] data;

    @SafeVarargs
    public TimestampSeries(Timestamp... data) {
        super(Object.class);
        this.data = data;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public Timestamp get(int index) {
        return data[index];
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        System.arraycopy(data, fromOffset, to, toOffset, len);
    }

    @Override
    public Series<Timestamp> materialize() {
        return this;
    }

    @Override
    public Series<Timestamp> fillNulls(Timestamp value) {

        int len = data.length;
        Timestamp[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = new Timestamp[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = value;
            }
        }

        return copy != null ? new TimestampSeries(copy) : this;
    }

    @Override
    public Series<Timestamp> fillNullsFromSeries(Series<? extends Timestamp> values) {
        int len = data.length;
        Timestamp[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = new Timestamp[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = values.get(i);
            }
        }

        return copy != null ? new TimestampSeries(copy) : this;
    }

    @Override
    public Series<Timestamp> fillNullsBackwards() {
        int len = data.length;
        Timestamp[] copy = null;
        int fillFrom = -1;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                if (copy == null) {
                    copy = (Timestamp[]) new Object[len];
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

        return copy != null ? new TimestampSeries(copy) : this;
    }

    @Override
    public Series<Timestamp> fillNullsForward() {
        int len = data.length;
        Timestamp[] copy = null;

        for (int i = 0; i < len; i++) {
            if (data[i] == null) {

                // leading nulls are fine
                if (i == 0) {
                    continue;
                }

                if (copy == null) {
                    copy = new Timestamp[len];
                    System.arraycopy(data, 0, copy, 0, len);
                }

                copy[i] = copy[i - 1];
            }
        }

        return copy != null ? new TimestampSeries(copy) : this;
    }
}
