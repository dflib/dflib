package com.nhl.dflib;

public class BaseDataFrameTest {

    protected DataFrame createDf(Index i, Object... sequenceFoldedByRow) {
        return ColumnDataFrame.fromSequenceFoldByRow(i, sequenceFoldedByRow);
    }
}
