package com.nhl.dflib;

import com.nhl.dflib.column.ColumnDataFrame;
import com.nhl.dflib.row.RowDataFrame;

public class BaseDataFrameTest {

    protected boolean columnar;

    public BaseDataFrameTest(boolean columnar) {
        this.columnar = columnar;
    }

    protected DataFrame createDf(Index i, Object... sequenceFoldedByRow) {
        return columnar
                ? ColumnDataFrame.fromSequenceFoldByRow(i, sequenceFoldedByRow)
                : RowDataFrame.fromSequenceFoldByRow(i, sequenceFoldedByRow);
    }
}
