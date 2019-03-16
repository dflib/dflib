package com.nhl.dflib.benchmark.memory;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.benchmark.data.RowByRowSequence;
import com.nhl.dflib.benchmark.data.ValueMaker;
import com.nhl.dflib.benchmark.memory.base.MemoryTest;

public class ColumnarDataFrameMemory extends MemoryTest {

    private static final int ROWS = 1_000_000;

    public static void main(String[] args) {

        int cells = ROWS * 2;

        ColumnarDataFrameMemory test = new ColumnarDataFrameMemory();
        test.run("nullCells", test::nullCells, cells);
        test.run("intCells", test::intCells, cells);
        test.run("longCells", test::longCells, cells);
        test.run("boolCells", test::boolCells, cells);
        test.run("repeatingStringCells", test::repeatingStringCells, cells);
        test.run("randStringCells", test::randStringCells, cells);
    }

    public DataFrame nullCells() {
        DataFrame df = RowByRowSequence.columnarDf(ROWS,
                ValueMaker.nullSeq(),
                ValueMaker.nullSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame intCells() {
        DataFrame df = RowByRowSequence.columnarDf(ROWS,
                ValueMaker.intSeq(),
                ValueMaker.intSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame longCells() {
        DataFrame df = RowByRowSequence.columnarDf(ROWS,
                ValueMaker.longSeq(),
                ValueMaker.longSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame boolCells() {
        DataFrame df = RowByRowSequence.columnarDf(ROWS,
                ValueMaker.booleanSeq(),
                ValueMaker.booleanSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame repeatingStringCells() {
        DataFrame df = RowByRowSequence.columnarDf(ROWS,
                ValueMaker.constStringSeq("abc"),
                ValueMaker.constStringSeq("xyz"));
        df.materialize().iterator();
        return df;
    }

    public DataFrame randStringCells() {
        DataFrame df = RowByRowSequence.columnarDf(ROWS,
                ValueMaker.semiRandomStringSeq("abc", ROWS),
                ValueMaker.semiRandomStringSeq("xyz", ROWS));
        df.materialize().iterator();
        return df;
    }
}
