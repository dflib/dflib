package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.Exp;

import java.sql.Timestamp;

public interface TimestampExp extends Exp<Timestamp> {


    static TimestampExp $datetime(String name) {
        return new TimestampColumn(name);
    }
}