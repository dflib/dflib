package com.nhl.dflib.benchmark.memory.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.benchmark.DataGenerator;
import com.nhl.dflib.benchmark.ValueMaker;
import com.nhl.dflib.benchmark.memory.MemoryTest;

public class DataFrameMemory extends MemoryTest {

    private static final int ROWS = 1_000_000;

    public static void main(String[] args) {

        int cells = ROWS * 2;

        DataFrameMemory test = new DataFrameMemory();
        test.run("nullCells", test::nullCells, cells);
        test.run("intCells", test::intCells, cells);
        test.run("longCells", test::longCells, cells);
        test.run("boolCells", test::boolCells, cells);
        test.run("repeatingStringCells", test::repeatingStringCells, cells);
        test.run("randStringCells", test::randStringCells, cells);
    }

    public DataFrame nullCells() {
        DataFrame df = DataGenerator.rowDF(ROWS,
                ValueMaker.nullSeq(),
                ValueMaker.nullSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame intCells() {
        DataFrame df = DataGenerator.rowDF(ROWS,
                ValueMaker.intSeq(),
                ValueMaker.intSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame longCells() {
        DataFrame df = DataGenerator.rowDF(ROWS,
                ValueMaker.longSeq(),
                ValueMaker.longSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame boolCells() {
        DataFrame df = DataGenerator.rowDF(ROWS,
                ValueMaker.booleanSeq(),
                ValueMaker.booleanSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame repeatingStringCells() {
        DataFrame df = DataGenerator.rowDF(ROWS,
                ValueMaker.constStringSeq("abc"),
                ValueMaker.constStringSeq("xyz"));
        df.materialize().iterator();
        return df;
    }

    public DataFrame randStringCells() {
        DataFrame df = DataGenerator.rowDF(ROWS,
                ValueMaker.semiRandomStringSeq("abc", ROWS),
                ValueMaker.semiRandomStringSeq("xyz", ROWS));
        df.materialize().iterator();
        return df;
    }
}
