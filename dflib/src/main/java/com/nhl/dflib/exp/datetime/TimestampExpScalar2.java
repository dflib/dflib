package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.map.MapExpScalar2;

import java.sql.Timestamp;
import java.util.function.BiFunction;


public class TimestampExpScalar2<L, R> extends MapExpScalar2<L, R, Timestamp> implements TimestampExp {

    public static <L, R> TimestampExpScalar2<L, R> mapVal(String opName, Exp<L> left, R right, BiFunction<L, R, Timestamp> op) {
        return new TimestampExpScalar2<>(opName, left, right, valToSeries(op, Timestamp.class));
    }

    public TimestampExpScalar2(String opName, Exp<L> left, R right, BiFunction<Series<L>, R, Series<Timestamp>> op) {
        super(opName, Timestamp.class, left, right, op);
    }
}
