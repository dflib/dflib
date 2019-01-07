package com.nhl.yadf.join;

import com.nhl.yadf.DataFrame;
import com.nhl.yadf.Index;
import com.nhl.yadf.ZippingDataFrame;
import com.nhl.yadf.zip.Zipper;

public abstract class BaseJoiner {

    protected DataFrame zipJoinSides(Index joinedColumns, DataFrame lf, DataFrame rf) {
        return new ZippingDataFrame(joinedColumns, lf, rf, Zipper.rowZipper()).materialize();
    }
}
