package com.nhl.dflib.benchmark.memory;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.benchmark.DataGenerator;
import com.nhl.dflib.benchmark.ValueMaker;
import com.nhl.dflib.benchmark.memory.benchmark.MemoryTest;
import com.nhl.dflib.map.DoubleValueMapper;
import com.nhl.dflib.map.IntValueMapper;

public class ColumnarDataFrameMemory extends MemoryTest {

    private static final int ROWS = 1_000_000;

    public static void main(String[] args) {

        int cells = ROWS * 2;

        ColumnarDataFrameMemory test = new ColumnarDataFrameMemory();
        test.run("nullCells", test::nullCells, cells);
        test.run("intCells", test::intCells, cells);
        test.run("primitiveIntCells", test::primitiveIntCells, cells);
        test.run("doubleCells", test::doubleCells, cells);
        test.run("primitiveDoubleCells", test::primitiveDoubleCells, cells);
        test.run("longCells", test::longCells, cells);
        test.run("boolCells", test::boolCells, cells);
        test.run("repeatingStringCells", test::repeatingStringCells, cells);
        test.run("randStringCells", test::randStringCells, cells);
    }

    public DataFrame nullCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS,
                ValueMaker.nullSeq(),
                ValueMaker.nullSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame doubleCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS,
                ValueMaker.doubleSeq(),
                ValueMaker.doubleSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame primitiveDoubleCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS, ValueMaker.doubleSeq(), ValueMaker.doubleSeq())
                .convertColumnToDouble(0, DoubleValueMapper.fromNumber())
                .convertColumnToDouble(1, DoubleValueMapper.fromNumber());
        df.materialize().iterator();
        return df;
    }

    public DataFrame intCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS,
                ValueMaker.intSeq(),
                ValueMaker.intSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame primitiveIntCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS, ValueMaker.intSeq(), ValueMaker.intSeq())
                .convertColumnToInt(0, IntValueMapper.numToInt())
                .convertColumnToInt(1, IntValueMapper.numToInt());
        df.materialize().iterator();
        return df;
    }

    public DataFrame longCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS,
                ValueMaker.longSeq(),
                ValueMaker.longSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame boolCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS,
                ValueMaker.booleanSeq(),
                ValueMaker.booleanSeq());
        df.materialize().iterator();
        return df;
    }

    public DataFrame repeatingStringCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS,
                ValueMaker.constStringSeq("abc"),
                ValueMaker.constStringSeq("xyz"));
        df.materialize().iterator();
        return df;
    }

    public DataFrame randStringCells() {
        DataFrame df = DataGenerator.columnarDF(ROWS,
                ValueMaker.semiRandomStringSeq("abc", ROWS),
                ValueMaker.semiRandomStringSeq("xyz", ROWS));
        df.materialize().iterator();
        return df;
    }
}
