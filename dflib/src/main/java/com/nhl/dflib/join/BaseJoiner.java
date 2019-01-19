package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.HConcatDataFrame;
import com.nhl.dflib.concat.HConcat;

public abstract class BaseJoiner {

    protected DataFrame zipJoinSides(Index joinedColumns, DataFrame lf, DataFrame rf) {
        return new HConcatDataFrame(joinedColumns, JoinType.full, lf, rf, HConcat.concatenator()).materialize();
    }
}
