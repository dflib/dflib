package com.nhl.dflib;

import com.nhl.dflib.column.ColumnDataFrame;

public class BaseDataFrameTest {

    protected DataFrame createDf(Index i, Object... sequenceFoldedByRow) {
        return ColumnDataFrame.fromSequenceFoldByRow(i, sequenceFoldedByRow);
    }
}
