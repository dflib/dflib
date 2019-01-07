package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.ZippingDataFrame;
import com.nhl.dflib.zip.Zipper;

public abstract class BaseJoiner {

    protected DataFrame zipJoinSides(Index joinedColumns, DataFrame lf, DataFrame rf) {
        return new ZippingDataFrame(joinedColumns, lf, rf, Zipper.rowZipper()).materialize();
    }
}
