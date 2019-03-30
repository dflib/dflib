package com.nhl.dflib;

public abstract class BaseDataFrameTest {

    protected DataFrame createDf(Index i, Object... sequenceFoldedByRow) {
        return DataFrame.forSequenceFoldByRow(i, sequenceFoldedByRow);
    }
}
